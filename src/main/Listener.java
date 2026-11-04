import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

import contact.Contact;
import contact.ContactList;

public class Listener {
    private static class ListenerThread extends Thread {
        ContactList contactList = ContactList.getInstance(); 

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
                    AddUserToContactList(message);
                    

                } 
            } catch (Exception e) {
                System.out.println(e);
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


    public Listener() {
        Thread listenerThread = new ListenerThread();
        listenerThread.start();
    }
}
