import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import contact.Contact;

public class Sender {
    private static final int PORT = 12345;
    private final Contact myself;
    private final SendThread sendThread;

    // Send heartbeat packets every second to notify others of my presence
    // and a BYE packet when shutting down
    private static class SendThread extends Thread {
        private final Contact myself;
        private volatile boolean running = true;
        DatagramSocket socket;
        public SendThread(Contact myself) {
            this.myself = myself;
        }

        @Override
        public void run() {

            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                InetAddress address = InetAddress.getByName("10.1.255.255");
                while (running) {
                    String message = "ONLINE BROADCAST:" + myself.getUUID() + ":" + myself.getNickname();
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            address,
                            PORT);
                    socket.send(packet);
                    //System.out.println("Broadcasting: " + message);
                    Thread.sleep(1000);
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
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
                socket.send(packet);
                /* System.out.println("Sen\r\n" + //
                                        "\r\n" + //
                                        "    // Call when app exists\r\n" + //
                                        "    public void shutdown() {\r\n" + //
                                        "     der sent: " + byeMessage); */
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