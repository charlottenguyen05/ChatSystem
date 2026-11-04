import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import contact.Contact;
import contact.ContactList;

public class ContactDiscovery {
    HashMap<Contact, List<Object>> threadCorrespondingToContact = new HashMap<>();  

    public void start(Contact myself) {
        List<Object> correspondingSenderListener = new ArrayList<>(); // object to store inside a Listener and a Sender
        Listener listener = new Listener(myself);
        Sender sender = new Sender(myself);
        correspondingSenderListener.add(listener);
        correspondingSenderListener.add(sender);
        threadCorrespondingToContact.put(myself, correspondingSenderListener);
        System.out.println("Starting : " + myself.getNickname());
    }

    public void stop(Contact c) {
        // check if the list contains c
        if (!threadCorrespondingToContact.containsKey(c)) {
            System.out.println("Cannot stop the asked contact");
            return;
        } 

        List<Object> threads = threadCorrespondingToContact.get(c);

        Object listenerThread = threads.get(0);
        if (listenerThread instanceof Listener l) {
            l.shutdown();
            System.out.println("Listener thread successfully stopped");
        }

        Object senderThread = threads.get(1);
        if (senderThread instanceof Sender s) {
            // s.shutdown(); // Pensez à l'implémenter dans Sender si ce n'est pas fait
            System.out.println("Sender thread successfully stopped");
        }
        
        ContactList.getInstance().removeUser(c.getUUID());
    }
}
