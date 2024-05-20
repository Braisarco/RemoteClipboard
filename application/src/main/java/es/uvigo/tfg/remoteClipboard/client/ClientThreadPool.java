package es.uvigo.tfg.remoteClipboard.client;

import es.uvigo.tfg.remoteClipboard.services.AppManager;

import java.util.ArrayList;
import java.util.List;

public class ClientThreadPool extends Thread{
    private boolean active;
    private List<Client> clientPool = new ArrayList<>();
    private AppManager manager;

    public ClientThreadPool(AppManager manager){
        this.manager = manager;
        this.active = true;
        for(int i = 0; i <50; i++){
            clientPool.add(new Client(this.manager,manager.getLocalUserName()));
        }
    }

    public void executeClient(String ip, String netName){
        for (Client client : clientPool){
            if(!client.isAlive()){
                client.setIp(ip);
                client.setNetName(netName);
                client.start();
                break;
            }
        }
    }
}
