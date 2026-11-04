import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import contact.Contact;
import contact.ContactList;

public class App {
    public static void main(String[] args){
        ContactList contactList = new ContactList(); // contains users on the network
        try {
            ContactList initialUsers = new ContactList(); // contains initial users. If you connect in another device on the network you won't be in that list
            initialUsers.addOrUpdateContact(new Contact("Contact1"));
            initialUsers.addOrUpdateContact(new Contact("Contact2"));
            initialUsers.addOrUpdateContact(new Contact("Contact3"));
            initialUsers.addOrUpdateContact(new Contact("Contact4"));
            initialUsers.addOrUpdateContact(new Contact("Contact5"));
            List<UUID> uuids = initialUsers.getListUUID();
            System.out.println("After initial users");
            ContactDiscovery contacts = new ContactDiscovery();
            for (int i=0; i<=initialUsers.getListSize()-1;i++) {
                contacts.start(initialUsers.getElement(uuids.get(i)));
                System.out.println("After loop");
                Thread.sleep(2000);
                System.out.println("After sleep");
            }
            contacts.stop();
            System.out.println("After stop");
            

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("An error has occured : " + e);
        }
        
    }
}
