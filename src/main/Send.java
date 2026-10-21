
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Send {

    private static class SendThread extends Thread {

        @Override
        public void run() {
            int port = 12345;
            String message = "UDP BROADCAST";
            byte[] buffer = message.getBytes();

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);

                InetAddress address =
                    InetAddress.getByName("10.10.40.245"); // My adress with ifconfig 
               while (true) {
                    DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        address,
                        port
                    );
                    System.out.println("Sending to : " + packet.getAddress());
                    socket.send(packet);
                    Thread.sleep(1000);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Send() {
        Thread sendThread = new SendThread();
        sendThread.start();
    }
}
