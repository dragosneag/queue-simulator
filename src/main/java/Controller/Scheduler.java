package Controller;

import Model.Client;
import Model.Server;

import java.util.ArrayList;
import java.util.List;

/**Scheduler class
 * The class, as its name suggests, schedules the distribution of each client
 * to a queue, depending on the strategy that is the most efficient.*/

public class Scheduler {

    private volatile List<Server> servers;
    private volatile int maxNoServers;
    private volatile int maxClientsPerServer;
    private Strategy strategy;

    /**The constructor initializes the servers and starts the execution of that server's thread*/
    public Scheduler(int Q, int maxClients) {

        this.maxNoServers = Q;
        this.maxClientsPerServer = maxClients;
        servers = new ArrayList<Server>();
        for (int i = 0; i < maxNoServers; i++) {

            Server server = new Server(i + 1);
            servers.add(server);
            server.startThread();
        }
    }

    public List<Server> getServers() {
        return servers;
    }

    /**This method selects the strategy for distributing each client to a queue, depending
     * on the specified policy*/
    public void changeStrategy(SelectionPolicy policy) {

        if (policy == SelectionPolicy.SHORTEST_QUEUE){

            strategy = new ConcreteStrategyQueue();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME){

            strategy = new ConcreteStrategyTime();
        }
    }

    /**This method adds a client to one of the servers, applying the selected strategy*/
    public void dispatchClient(Client c) {

        strategy.addClient(servers, c);
    }
}