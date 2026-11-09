import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import contact.Contact;
import contact.ContactList;

public class App {
    public static void main(String[] args){
        ContactList contactList = ContactList.getInstance(); // contains users on the network
        System.out.println("Creation of ContactList : empty");

        try {
            /* List<Contact> initialUsers = new ArrayList<>();
            initialUsers.add(new Contact("100"));
            initialUsers.add(new Contact("200"));
            initialUsers.add(new Contact("300"));
            initialUsers.add(new Contact("400"));
            initialUsers.add(new Contact("500")); */
            
            String myNickname = contactList.nicknameInput();
            
            Contact myself = new Contact(myNickname);

            /*ContactList initialUsers = ContactList.getInstance(); // contains initial users. If you connect in another device on the network you won't be in that list
            initialUsers.addOrUpdateContactTestVersion(new Contact("100"));
            initialUsers.addOrUpdateContactTestVersion(new Contact("200"));
            initialUsers.addOrUpdateContactTestVersion(new Contact("300"));
            initialUsers.addOrUpdateContactTestVersion(new Contact("400"));
            initialUsers.addOrUpdateContactTestVersion(new Contact("500"));*/

            //List<UUID> uuids = initialUsers.getListUUID();
            //System.out.println("After initial users");
            ContactDiscovery contacts = new ContactDiscovery();
            /*for (int i=0; i<=initialUsers.size()-1;i++) {
                contacts.start(initialUsers.get(i));
                //System.out.println("After loop");
                Thread.sleep(1000);
                //System.out.println("After sleep");
            } */
            contacts.start(myself);

            System.out.println("CALLING STOP");
            Thread.sleep(60000); // wait 5s
            
            contacts.stop(myself);

            // Starting to stop all threads
            /* for (int i=0; i<=initialUsers.size()-1;i++) {
                contacts.stop(initialUsers.get(i));
            } */
            
            //System.out.println("After stop");
            

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("An error has occured : " + e);
        }
        
    }
}
