import java.net.UnknownHostException;

import contact.Contact;
import contact.ContactList;

public class App {
    public static void main(String[] args){
        ContactList contactList = ContactList.getInstance(); // contains users on the network
        System.out.println("Creation of ContactList : empty");

        try {
            String myUsername = contactList.nicknameInput();
            Contact myself = new Contact(myUsername);
            ContactDiscovery contacts = new ContactDiscovery();
            contacts.start(myself);

            System.out.println("CALLING STOP");
            Thread.sleep(60000); // wait 60s
            
            contacts.stop(myself);

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("An error has occured : " + e);
        }
    }
}
