import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import contact.Contact;
import contact.ContactList;

public class Listener {
    private final Contact myself;
    private final ListenerThread listenerThread;

    private static class ListenerThread extends Thread {
        ContactList contactList = ContactList.getInstance(); 
        private volatile boolean running = true;
        DatagramSocket socket_send;
        DatagramSocket socket_receive;

        @Override
        public void run() {
            int receive_port = 12345;
            int sender_port = 12346; 
            byte[] receive_buffer = new byte[1024];

            try {
                socket_receive = new DatagramSocket(receive_port);
                System.out.println("Listening on port : " + receive_port);

                while(running){ 
                    DatagramPacket packet = new DatagramPacket(receive_buffer, receive_buffer.length);
                    socket_receive.receive(packet);

                    // From doc : String(byte[] bytes, int offset, int length)
                    String message = new String(packet.getData(), 0, packet.getLength());

                    //System.out.println("Received from " + packet.getAddress() + " -> " + message);
                    if (message.contains("ONLINE BROADCAST")) {
                        String username = message.split(":")[2];
                        String keyString = message.split(":")[1];
                        if (contactList.isNicknameValid(username)) {
                            AddUserToContactList(message);
                        } else {
                            // Send invalid username response 
                            socket_send = new DatagramSocket();
                            socket_send.setBroadcast(true);
                            InetAddress address = InetAddress.getByName("10.1.255.255");  // Change to sender's adr later
                            String invalid_name_msg = "INVALID_NICKNAME:" + keyString + ":" + username;
                            byte[] send_buffer = invalid_name_msg.getBytes();
                            DatagramPacket send_packet = new DatagramPacket(
                                    send_buffer,
                                    send_buffer.length,
                                    address,
                                    sender_port);
                            socket_send.send(send_packet);
                            System.out.println("Send invalid name message: " + invalid_name_msg);
                            Thread.sleep(1000);
                        }
                    } else if (message.contains("BYE")) {
                        contactList.removeUser(UUID.fromString(message.split(":")[1]));;
                    }

                } 
            } catch (Exception e) {
                System.out.println(e);
            }


        }

        // Stops the heartbeat and waits 2s for the BYE message to be sent.
        public void shutdown() {
            running = false;
            interrupt(); // wake up from Thread.sleep()
            try {
                join(2000);
                if (socket_send != null && !socket_send.isClosed()) {
                    socket_send.close();
                }
                if (socket_receive != null && !socket_receive.isClosed()) {
                    socket_receive.close();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // add new user to contact list
        public void AddUserToContactList(String message) {
            String username = message.split(":")[2];
            String keyString = message.split(":")[1];
            UUID key = UUID.fromString(keyString);
            System.out.println("Username received : " + username); // delete after
            contactList.addOrUpdateContact(new Contact(username, key)); // ContactList method to add or update inside a list
        }


    }


    public Listener(Contact myself) {
        this.myself = myself;
        // Affectation directe au champ de la classe (sans le type 'Thread' devant)
        this.listenerThread = new ListenerThread();
        this.listenerThread.start();
    }

    // Call when app exists
    public void shutdown() {
        listenerThread.shutdown();
    }
}
