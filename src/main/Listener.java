import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

import contact.Contact;
import contact.ContactList;

public class Listener {
    private final Contact myself;
    private final ListenerThread listenerThread;

    private static class ListenerThread extends Thread {
        ContactList contactList = ContactList.getInstance(); 
        private volatile boolean running = true;
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

                    //System.out.println("Received from " + packet.getAddress() + " -> " + message);
                    if (message.contains("ONLINE BROADCAST")) {
                        AddUserToContactList(message);
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
