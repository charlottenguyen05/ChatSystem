public class ContactDiscovery {

    /*
     * This class should:
     * 1) Listen for UDP broadcasts
     * 2) Emit a broadcast
     * 3) Maintain a list of discovered contacts
     */

    public ContactDiscovery() {
    }

    private static class ListenerThread extends Thread {

        @Override
        public void run() {
            System.out.println("Greetings !!!");

            // TODO: Listen for UDP broadcasts
        }
    }

    public void start() {
        Thread listen = new ListenerThread();
        listen.start();
    }
}
