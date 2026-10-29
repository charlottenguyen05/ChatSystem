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

        public SendThread(Contact myself) {
            this.myself = myself;
        }

        // Stops the heartbeat and waits 2s for the BYE message to be sent.
        public void shutdown() {
            running = false;
            interrupt(); // wake up from Thread.sleep()
            try {
                join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);
                InetAddress address = InetAddress.getByName("255.255.255.255");
                while (running) {
                    String message = "ONLINE BROADCAST:" + myself.getUUID() + ":" + myself.getNickname();
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            address,
                            PORT);
                    socket.send(packet);
                    System.out.println("Broadcasting: " + message);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // Shutdown requested, handled in finally
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                sendBye();
            }
        }

        // Broadcasts BYE packet to notify peers this user is leaving.
        private void sendBye() {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);
                String byeMessage = "BYE:" + myself.getUUID();
                byte[] buffer = byeMessage.getBytes(StandardCharsets.UTF_8);
                InetAddress address = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
                socket.send(packet);
                System.out.println("Sender sent: " + byeMessage);
            } catch (IOException e) {
                System.err.println("Sender fail to send BYE: " + e.getMessage());
            }
        }
    }

    public Sender(Contact myself) {
        this.myself = myself;
        this.sendThread = new SendThread(myself);
        sendThread.start();
    }

    // Call when app exists
    public void shutdown() {
        sendThread.shutdown();
    }
}