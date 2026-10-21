public class ContactDiscovery {

    /*
     * This class should:
     * 1) Listen for UDP broadcasts
     * 2) Emit a broadcast
     * 3) Maintain a list of discovered contacts
     */

    public ContactDiscovery() {
    }



    public void start() {
        Listen listen = new Listen(); // Creates a Listen object which creates and runs a thread
        Send send = new Send(); // Creates a Send object and sends an UDP Broadcast every second
    }
}
