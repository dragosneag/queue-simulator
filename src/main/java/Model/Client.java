package Model;

/**Client class
 * This class is responsible to represent a client with its arrival and service time*/

public class Client {

    private volatile int id;
    private volatile int arrivalTime;
    private volatile int serviceTime;
    private volatile int isFirst; /**helps us understand if client is the head of the queue*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.isFirst = 0;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(int isFirst) {
        this.isFirst = isFirst;
    }

    /**If the client just became the head of the queue it has value 1, and after one iteration
     * it will get value 2, so we can decrement its service time*/
    public void isFirstInQueue(){

        this.isFirst = 1;
    }
}
