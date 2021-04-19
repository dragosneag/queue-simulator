package Controller;

import Model.Client;
import Model.Server;

import java.util.List;

/**Queue Strategy class
 * This class adds a client to the server with the least clients in its queue.*/
public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addClient(List<Server> servers, Client c) {

        int shortestQueue = Integer.MAX_VALUE;
        for (Server server : servers) {

            /**It iterates through the list of servers and searches for the server with the
             * smallest number of clients*/
            if(server.getClients().length < shortestQueue){
                shortestQueue = server.getClients().length;
            }
        }
        for (Server server : servers) {

            /**Then it iterates again through the list until the first server with the least
             * clients in its queue, adds the client to it, and exits the iteration*/
            if(server.getClients().length == shortestQueue){

                server.addClient(c);
                break;
            }
        }
    }
}
