// Contains the list of all discovered contacts and 
// allows to add / remove / update them
package contact;

import java.util.ArrayList;
import java.util.List;
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
        }
    }

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

    // Remove user (when the user goes offline)
    public void removeUser(UUID uuid) {
        synchronized (contactList) {
            contactList.remove(uuid);
        }
    }

    public List<Contact> getContactList() {
        synchronized (contactList) {
            return new ArrayList<>(contactList.values());
        }
    }
}
