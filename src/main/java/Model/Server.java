package Model;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**Server class
 * The class is responsible with the management of the queues, with adding and also
 * removing clients from the queue*/

public class Server implements Runnable{

    private volatile int id;
    private volatile BlockingQueue<Client> clients;
    private volatile AtomicInteger waitingPeriod;
    private Thread thread; /**Each queue has its own thread*/

    public Server(int id) {

        this.id = id;
        clients = new LinkedBlockingDeque<Client>();
        waitingPeriod = new AtomicInteger();
    }

    public int getId() {
        return id;
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public void setClients(BlockingQueue<Client> clients) {
        this.clients = clients;
    }

    public BlockingQueue<Client> getQueueClients() {
        return clients;
    }

    /**When this method is called, the thread of the queue is started*/
    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    /**This method converts the queue into an array of clients*/
    public Client[] getClients() {

        Client[] clients1 = new Client[clients.size()];
        Iterator<Client> clientIterator = clients.iterator();

        for(int i = 0; i < clients.size(); i++)
        {
            clients1[i] = clientIterator.next();
        }
        return clients1;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    /**This method adds a given client to the queue and increments the waiting period with
     * the client's service time*/
    public void addClient(Client client) {

        clients.add(client);
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    /**The method executes itself since the thread is started and while the queue is not empty,
     * eliminates the client in the head of the queue, and puts the thread to sleep for the service
     * time of the respective client*/
    @Override
    public void run() {

        while (true){
            if(clients.peek() != null) {
                try {
                    Client client = clients.peek();
                    client.isFirstInQueue();
                    thread.sleep(client.getServiceTime()*1000);
                    clients.poll();
                } catch (IllegalArgumentException e1) {
                    System.out.println("Argument is illegal");
                } catch (InterruptedException e2) {
                    System.out.println("Interrupted");
                }
            }
        }
    }
}