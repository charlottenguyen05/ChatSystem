// A Contact represents one discovered user on the network.
package contact;

import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Contact {
    private final UUID uuid; // immutable
    private String nickname; // mutable
    private InetAddress ip;

    // Constructor for myself
    public Contact(String nickname) throws java.net.UnknownHostException {
        this.uuid = UUID.randomUUID(); // before the try because it may not be initialized
        try {
            this.nickname = nickname;
            this.ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
    }

    public Contact (String nickname, UUID uuid) {
        this.uuid = uuid; 
        try {
            this.nickname = nickname;
            this.ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
    }

    // Constructor for received contact
    public Contact(UUID uuid, String nickname, InetAddress ip) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.ip = ip;
    }

    // Getters + setters for nickname, ip, lastSeen
    public UUID getUUID() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    // Compare different users based on uuid only
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Contact contact = (Contact) obj;
        return uuid.equals(contact.uuid);
    }

    // distant hash value based on uuid for HashMap and HashSet
    public int hashCode() {
        return uuid.hashCode();
    }
}
