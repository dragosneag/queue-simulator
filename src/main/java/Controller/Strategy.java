package Controller;

import Model.Client;
import Model.Server;

import java.util.List;

public interface Strategy {

    public void addClient(List<Server> servers, Client c);
}
