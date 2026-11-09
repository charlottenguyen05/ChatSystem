import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import contact.Contact;
import contact.ContactList;

public class Sender {
    private static final int sender_port = 12345;
    private static final int receive_port = 12346;
    private final Contact myself;
    private final SendThread sendThread;

    // Send heartbeat packets every second to notify others of my presence
    // and a BYE packet when shutting down
    private static class SendThread extends Thread {
        private final Contact myself;
        ContactList contactList = ContactList.getInstance(); 
        private volatile boolean running = true;
        DatagramSocket socket_send;
        DatagramSocket socket_receive;
        public SendThread(Contact myself) {
            this.myself = myself;
        }

        @Override
        public void run() {
            byte[] send_buffer = new byte[1024]; // buffer to receive OK message from listener
            byte[] receive_buffer = new byte[1024];
            try {
                socket_send = new DatagramSocket();
                socket_send.setBroadcast(true);
                InetAddress address = InetAddress.getByName("10.1.255.255");
                while (running) {
                    String message = "ONLINE BROADCAST:" + myself.getUUID() + ":" + myself.getNickname();
                    send_buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            send_buffer,
                            send_buffer.length,
                            address,
                            sender_port);
                    socket_send.send(packet);
                    System.out.println("Broadcasting: " + message);
                    Thread.sleep(1000);

                    
                    socket_receive = new DatagramSocket(receive_port);
                    System.out.println("before receive");
                    DatagramPacket packet_receive = new DatagramPacket(receive_buffer, receive_buffer.length);
                    socket_receive.receive(packet_receive);
                    String receive_message = new String(packet_receive.getData(), 0, packet_receive.getLength());
                    System.out.println("[Sender] Receive message: " + receive_message);
                    if (receive_message.startsWith("INVALID_NICKNAME")) {
                        String newNickname = contactList.nicknameInput();
                        myself.setNickname(newNickname);
                    }
                }
            } catch (InterruptedException e) {
                // Shutdown requested, handled in finally
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                sendBye();
                if (socket_send != null && !socket_send.isClosed()) {
                    socket_send.close();
                }
                if (socket_receive != null && !socket_receive.isClosed()) {
                    socket_receive.close();
                }
            }
        }

        // Broadcasts BYE packet to notify peers this user is leaving.
        private void sendBye() {
            try {
                socket_send.setBroadcast(true);
                String byeMessage = "BYE:" + myself.getUUID() + ":" + myself.getNickname();
                byte[] buffer = byeMessage.getBytes(StandardCharsets.UTF_8);
                InetAddress address = InetAddress.getByName("10.1.255.255");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sender_port);
                socket_send.send(packet);
                System.out.println("Send Bye : " + byeMessage); 
            } catch (IOException e) {
                System.err.println("Sender fail to send BYE: " + e.getMessage());
            }
        }

        public void shutdown() {
            running = false;
            interrupt(); // wake up from Thread.sleep()
            try {
                join(2000);
                if (socket_receive != null && !socket_receive.isClosed()) {
                    socket_receive.close();
                }
                if (socket_send != null && !socket_send.isClosed()) {
                    socket_send.close();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Sender(Contact myself) {
        this.myself = myself;
        this.sendThread = new SendThread(myself);
        sendThread.start();
    }

    public void shutdown() {
        sendThread.shutdown();
    }

}