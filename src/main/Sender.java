import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import contact.Contact;
import contact.ContactList;

public class Sender {
    private static final int receive_port = 12345;
    private static final int send_port = 12346;
    private final Contact myself;
    private final SendThread sendThread;

    // Send heartbeat packets every second to notify others of my presence
    // and a BYE packet when shutting down
    private static class SendThread extends Thread {
        ContactList contactList = ContactList.getInstance(); 
        private final Contact myself;
        private volatile boolean running = true;
        DatagramSocket socket;
        public SendThread(Contact myself) {
            this.myself = myself;
        }

        @Override
        public void run() {
            byte[] send_buffer = new byte[1024]; // buffer to receive OK message from listener
            byte[] receive_buffer = new byte[1024];
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                InetAddress address = InetAddress.getByName("10.1.255.255");
                boolean broadcastOK = false;

                while (!broadcastOK) {
                    String message = "ONLINE BROADCAST:" + myself.getUUID() + ":" + myself.getNickname();
                    send_buffer = message.getBytes();
                    DatagramPacket packet_send = new DatagramPacket(
                            send_buffer,
                            send_buffer.length,
                            address,
                            send_port);
                    socket.send(packet_send);
                    System.out.println("Broadcasting: " + message);
                    Thread.sleep(100);

                    // Receive OK packet
                    socket = new DatagramSocket(receive_port);
                    System.out.println("before receive");
                    DatagramPacket packet_receive = new DatagramPacket(receive_buffer, receive_buffer.length);
                    String receive_message = new String(packet_receive.getData(), 0, packet_receive.getLength());
                    if (receive_message.startsWith("OK")) {
                        socket.receive(packet_receive);
                        System.out.println("[Sender] OK MESSAGE: " + receive_message);

                        if (!isBroadcastOK(receive_message)) {
                            System.out.println("[Sender] Broadcast NOT OK : resend");
                        } else {
                            System.out.println("[Sender] OK MESSAGE RECEIVED");
                            broadcastOK = true;
                        }
                    } else if (receive_message.startsWith("INVALID_NICKNAME")) {
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
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }

        // Broadcasts BYE packet to notify peers this user is leaving.
        private void sendBye() {
            try {
                socket.setBroadcast(true);
                String byeMessage = "BYE:" + myself.getUUID() + ":" + myself.getNickname();
                byte[] buffer = byeMessage.getBytes(StandardCharsets.UTF_8);
                InetAddress address = InetAddress.getByName("10.1.255.255");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, send_port);
                socket.send(packet);
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
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public boolean isBroadcastOK(String OKmessage) {
            if(OKmessage.startsWith("OK")) {
                System.out.println("BROADCAST TRUE");
                return true;
            } else {
                System.out.println("BROADCAST FALSE");
                return false;
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