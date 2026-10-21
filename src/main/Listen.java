import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Listen {

    private static class ListenerThread extends Thread {

        @Override
        public void run() {
            int port = 12345;
            byte[] buffer = new byte[1024];

            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("Listening on port : " + port);

                while(true){ 
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // From doc : String(byte[] bytes, int offset, int length)
                    String message = new String(packet.getData(), 0, packet.getLength());

                    System.out.println("Received from " + packet.getAddress() + " -> " + message);
                } 
            } catch (Exception e) {
                System.out.println(e);
            }


        }
    }

    public Listen() {
        Thread listenerThread = new ListenerThread();
        listenerThread.start();
    }
}
