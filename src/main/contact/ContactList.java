// Contains the list of all discovered contacts and 
// allows to add / remove / update them
package contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.util.UUID;

public class ContactList {
    private final HashMap<UUID, Contact> contactList;

    // Singleton: Only one ContactList at a time
    private static class Holder {
        private static final ContactList INSTANCE = new ContactList();
    }

    private ContactList() {
        this.contactList = new HashMap<>();
    }

    public static ContactList getInstance() {
        return Holder.INSTANCE;
    }
    
    public int getListSize() {
        if (!contactList.isEmpty()) {
            return contactList.size();
        }
        return 0;
    }

    // Add / Update a discovered contact
    public void addOrUpdateContact(Contact c) {
        synchronized (contactList) {
            // Case 1: UUID not yet in the map then add it
            if (!contactList.containsKey(c.getUUID())) {
                contactList.put(c.getUUID(), c);
            }
            // Case 2: UUID already in the map then update its nickname and IP
            else {
                contactList.get(c.getUUID()).setNickname(c.getNickname());
                contactList.get(c.getUUID()).setIp(c.getIp());
            }

            // Display all the elements of the list
            displayContactList();
        }
    }

     // Remove user (when the user goes offline)
    public void removeUser(UUID uuid) {
        synchronized (contactList) {
            contactList.remove(uuid);
        }

        // Display contact list
        displayContactList();
    }

    // Display all users on the list (be careful when too many users)
    public void displayContactList() {
        synchronized (contactList) {
            int i = 0;
            for (Contact c : contactList.values()) {
                if (c != null) {
                    System.out.println(i + " " + c.getNickname());
                    i++;
                }
            }
        }
    }

    /*public void addOrUpdateContactTestVersion(Contact c) { // delete when users comes from network
                    // Case 1: UUID not yet in the map then add it
            if (!contactList.containsKey(c.getUUID())) {
                contactList.put(c.getUUID(), c);
            }
            // Case 2: UUID already in the map then update its nickname and IP
            else {
                contactList.get(c.getUUID()).setNickname(c.getNickname());
            }
    }*/

    // Nickname validation
    public boolean isNicknameValid(String nickname) {
        synchronized (contactList) {
            for (Contact c : contactList.values()) {
                if (c.getNickname().equals(nickname)) {
                    return false;
                }
            }
            return true;
        }
    }

    public Contact getElement(UUID uuid) {
        return contactList.get(uuid);
    }

    // returns a list of Contact
    public List<Contact> getContactList() {
        synchronized (contactList) {
            return new ArrayList<>(contactList.values());
        }
    }

    public List<UUID> getListUUID() {
        synchronized (contactList) {
            List<UUID> list_uuid = new ArrayList<>(); // list of uuids that will be returned
            Set<UUID> uuids = contactList.keySet(); // set of uuids that will be transformed into a list
            for (UUID uuid : uuids) {
                list_uuid.add(uuid);
            }
            return list_uuid;
        }
    }

    public String nicknameInput() {
            Scanner myScanner = new Scanner(System.in);
            System.out.println("Enter username");
            String username = myScanner.nextLine();
            myScanner.close();
            return username;
        }
}
