package Controller;

import Model.Client;
import Model.Server;
import View.Dashboard;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**Simulation Manger class
 * This class is responsible with running the simulator. It will generate a list
 * of clients and then for a specified period of time, at each step it will add and
 * extract clients in the queue.*/
public class SimulationManager extends JFrame implements Runnable, Comparator<Client> {

    public volatile int timeLimit;
    public volatile int maxProcessingTime;
    public volatile int minProcessingTime;
    public volatile int maxArrivalTime;
    public volatile int minArrivalTime;
    public volatile int numberOfServers;
    public volatile int numberOfClients;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    /**Variables responsible with calculating the necessary statistics*/
    private volatile float averageWaitingTime;
    private volatile float averageServiceTime;
    private volatile int peakHour;
    private volatile int maximumClients;

    private Scheduler scheduler;
    private List<Client> generatedClients;
    private String resultText;
    private String fullResultText;
    private Dashboard dashboard;
    private int stop = 0;

    public SimulationManager(int numberOfServers, int numberOfClients, int timeLimit, int minProcessingTime, int maxProcessingTime, int minArrivalTime, int maxArrivalTime, Dashboard dashboard){

        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.timeLimit = timeLimit;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        setDashboard(dashboard);

        this.averageWaitingTime = 1;
        this.averageServiceTime = 1;
        this.peakHour = 0;
        maximumClients = Integer.MIN_VALUE;

        scheduler = new Scheduler(numberOfServers, numberOfClients);
        scheduler.changeStrategy(selectionPolicy);
        generateNRandomClients();
        for (Client generatedClient : generatedClients) {
            this.averageServiceTime = this.averageServiceTime + generatedClient.getServiceTime();
        }
        this.averageServiceTime = this.averageServiceTime / numberOfClients;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public String getResultText() {
        return resultText;
    }

    public String getFullResultText() {
        return fullResultText;
    }

    /**Method that generates a given number of random clients*/
    private void generateNRandomClients(){

        Random rand = new Random();
        generatedClients = new ArrayList<Client>();

        for (int i = 0; i < numberOfClients; i++) {

            Client client = new Client();
            /**The arrival and service time are set randomly*/
            int rand_arrival = rand.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime;
            int rand_service = rand.nextInt(maxProcessingTime - minProcessingTime) + minProcessingTime;
            client.setArrivalTime(rand_arrival);
            client.setServiceTime(rand_service);
            generatedClients.add(client);
        }
        /**The list is then sorted depending on the clients' arrival time*/
        Collections.sort(generatedClients, this::compare);
        for (int i = 1; i <= numberOfClients; i++) {
            generatedClients.get(i - 1).setId(i);
        }
    }

    /**Method for comparing two clients by their arrival time*/
    @Override
    public int compare(Client o1, Client o2) {
        return o1.getArrivalTime() - o2.getArrivalTime();
    }

    /**The method executes itself since the thread is started and while the time limit hasn't
     * been reached, or the stopping conditions haven't been met, at each step it adds to a server
     * each client which have the arrival time equal to the current one. It then displays the servers
     * in the interface and waits 1 second before the next step.*/
    @Override
    public void run() {
        resultText = "";
        fullResultText = "";
        int currentTime = 0;
        while (currentTime < timeLimit && stop == 0) {

            if(checkStop()){
                stop = 1;
            }
            int tempMaxClients = 0;
            /**The clients that must be dispatched are dispatched*/
            dispatchClients(currentTime, tempMaxClients);
            resultText = resultText + "\n";
            fullResultText = fullResultText + "\n";
            decrementTime(currentTime);
            displayQueues();
            dashboard.updateResults(resultText, resultText);
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /**at the end it displays the statistics of the simulation*/
        averageWaitingTime = averageWaitingTime / timeLimit;
        resultText = resultText + "\nAverage waiting time: " + averageWaitingTime + "\nAverage service time: " + averageServiceTime + "\nPeak hour: " + peakHour + "\n";
        fullResultText = fullResultText + "\nAverage waiting time: " + averageWaitingTime + "\nAverage service time: " + averageServiceTime + "\nPeak hour: " + peakHour + "\n";
        dashboard.updateResults(resultText, fullResultText);
    }

    /**This method adds each client that has the arrival time equal to the current time
     * to a server*/
    private void dispatchClients(int currentTime, int countClients){

        resultText = "Time " + currentTime + "\n" + "Waiting clients: ";
        fullResultText = fullResultText + "Time " + currentTime + "\n" + "Waiting clients: ";
        int index = 0;
        while(index < generatedClients.size()) {
            if(generatedClients.get(index).getArrivalTime() == currentTime){
                /**It iterates through the clients list and checks who must be dispatched*/
                countClients++;
                scheduler.dispatchClient(generatedClients.get(index));
                generatedClients.remove(generatedClients.get(index));
            }
            else {
                resultText = resultText + "(" + generatedClients.get(index).getId() + "," + generatedClients.get(index).getArrivalTime() + "," + generatedClients.get(index).getServiceTime() + "); ";
                fullResultText = fullResultText + "(" + generatedClients.get(index).getId() + "," + generatedClients.get(index).getArrivalTime() + "," + generatedClients.get(index).getServiceTime() + "); ";
                index++;
            }
        }
    }

    /**This method decrements the waiting time for each server after one step, and also
     * the service time of each client that is at the head of one server*/
    private void decrementTime(int currentTime){
        int tempWaitingTime = 0;
        int tempClientsNumber = 0;
        for (Server server : scheduler.getServers()) {
            AtomicInteger tempAtomicInteger = server.getWaitingPeriod();
            Client[] tempClients = server.getClients();
            BlockingQueue<Client> clients = new LinkedBlockingDeque<Client>();
            tempClientsNumber = tempClientsNumber + tempClients.length;
            if(tempAtomicInteger.intValue() > 0) {
                tempAtomicInteger.decrementAndGet();
                server.setWaitingPeriod(tempAtomicInteger); /**For each server it decrements the waiting time by one*/
            }
            tempWaitingTime = tempWaitingTime + server.getWaitingPeriod().intValue();
            for (Client tempClient : tempClients) {

                if(tempClient.getIsFirst() == 2){/**If a client just became the head of the queue, we do not decrement its service time yet*/
                    int tempServiceTime = tempClient.getServiceTime();
                    tempClient.setServiceTime(tempServiceTime - 1);
                }
                if(tempClient.getIsFirst() == 1){
                    tempClient.setIsFirst(2);
                }
            }
        }
        if(tempClientsNumber > maximumClients){ /**Computes the statistics*/
            maximumClients = tempClientsNumber;
            peakHour = currentTime;
        }
        tempWaitingTime = tempWaitingTime / scheduler.getServers().size();
        averageWaitingTime = averageServiceTime + tempWaitingTime;
    }

    /**Method for checking if the simulation must be stopped*/
    private boolean checkStop(){
        if(generatedClients.size() > 0){ /**If the generated clients list still has clients we continue the process*/
            return false;
        }
        else {
            /**If there are servers with clients in it, also we continue the process*/
            for (Server server : scheduler.getServers()) {
                if(server.getClients().length > 0){
                    return false;
                }
            }
        }
        return true;
    }

    /**Method that displays the content of each queue*/
    public void displayQueues(){

        List<Server> servers = new ArrayList<Server>();
        servers = scheduler.getServers();
        for (Server server : servers) {

            resultText = resultText + "Queue " + server.getId() + ": ";
            fullResultText = fullResultText + "Queue " + server.getId() + ": ";
            Client[] tempClients = server.getClients();
            for (Client tempClient : tempClients) {

                resultText = resultText + "(" + tempClient.getId() + "," + tempClient.getArrivalTime() + "," + tempClient.getServiceTime() + "); ";
                fullResultText = fullResultText + "(" + tempClient.getId() + "," + tempClient.getArrivalTime() + "," + tempClient.getServiceTime() + "); ";
            }
            resultText = resultText + "\n";
            fullResultText = fullResultText + "\n";
        }
    }
}