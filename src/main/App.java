import contact.Contact;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Test");
            Contact myself = new Contact("NHI");
            ContactDiscovery contacts = new ContactDiscovery();
            // Testing
            contacts.start(myself);
            Thread.sleep(10000);
            contacts.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
