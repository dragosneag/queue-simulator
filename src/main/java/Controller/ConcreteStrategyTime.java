package Controller;

import Model.Client;
import Model.Server;

import java.util.List;

/**Time Strategy class
 * This class adds a client to the server with the least time for waiting in its queue.*/
public class ConcreteStrategyTime implements Strategy{

    @Override
    public void addClient(List<Server> servers, Client c) {
        int shortestWaiting = Integer.MAX_VALUE;
        for (Server server : servers) {

            /**It iterates through the list of servers and searches for the server with the
             * smallest waiting time*/
            if(server.getWaitingPeriod().intValue() < shortestWaiting){
                shortestWaiting = server.getWaitingPeriod().intValue();
            }
        }
        for (Server server : servers) {

            /**Then it iterates again through the list until the first server with the smallest waiting
             * time, adds the client to it, and exits the iteration*/
            if(server.getWaitingPeriod().intValue() == shortestWaiting){

                server.addClient(c);
                break;
            }
        }
    }
}