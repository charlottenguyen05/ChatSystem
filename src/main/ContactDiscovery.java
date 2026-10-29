import contact.Contact;

public class ContactDiscovery {
    private Listener listener;
    private Sender sender;

    public void start(Contact myself) {
        this.listener = new Listener();
        this.sender = new Sender(myself);
    }

    public void stop() {
        if (sender != null) {
            sender.shutdown();
        }
    }
}
