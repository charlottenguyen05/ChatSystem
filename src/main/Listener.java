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
        DatagramSocket socket;
        private final Contact myself;
        public ListenerThread(Contact myself) {
            this.myself = myself;
        }

        @Override
        public void run() {
            int receive_port = 12346;
            int send_port = 12345;
            byte[] buffer = new byte[1024];
            byte[] OKbuffer = new byte[1024];

            try {
                socket = new DatagramSocket(receive_port);
                System.out.println("Listening on port : " + receive_port);
                InetAddress address = InetAddress.getByName("10.1.255.255"); // Don't forget to change to send to the sender

                boolean broadcastReceived = false;
                while(!broadcastReceived){ 
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // Check if message received is a BROADCAST
                    // From doc : String(byte[] bytes, int offset, int length)
                    String message = new String(packet.getData(), 0, packet.getLength());
                    
                    if (contactList.isNicknameValid(message.split(":")[0])) {
                        // SENDING OK MESSAGE
                        String OKmessage = "OK:" + myself.getUUID() + myself.getNickname();
                        OKbuffer = OKmessage.getBytes();
                        packet = new DatagramPacket(
                                OKbuffer,
                                OKbuffer.length,
                                address,
                                send_port);
                        socket.send(packet);
                        System.out.println("SENDING OK: " + message);
                        contactList.addOrUpdateContact(new Contact(message.split(":")[2], UUID.fromString(message.split(":")[1])));
                    } else {
                        // Notify user that username is not valid
                        // SENDING USERNAME NOT CORRECT MESSAGE
                        String OKmessage = "INVALID_NICKNAME:" + myself.getUUID() + myself.getNickname();
                        OKbuffer = OKmessage.getBytes();
                        packet = new DatagramPacket(
                                OKbuffer,
                                OKbuffer.length,
                                address,
                                send_port);
                        socket.send(packet);
                        System.out.println("SENDING INVALID NICKNAME: " + message);
                    }


                    Thread.sleep(1000);
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
                if (socket != null && !socket.isClosed()) {
                    socket.close();
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
        this.listenerThread = new ListenerThread(myself);
        this.listenerThread.start();
    }

    // Call when app exists
    public void shutdown() {
        listenerThread.shutdown();
    }
}
