package es.uvigo.tfg.remoteClipboard.client;

import es.uvigo.tfg.remoteClipboard.services.AppManager;

import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;

public class ClientThreadPool extends Thread{
    private boolean active;
    private List<Client> clientPool = new ArrayList<>();

    public ClientThreadPool(String userName){

        this.active = true;
        for(int i = 0; i <50; i++){
            clientPool.add(new Client(userName));
        }
    }

    public void executeClient(String ip, String netName, AppManager manager){
        for (Client client : clientPool){
            if(!client.isAlive()){
                client.setIp(ip);
                client.setManager(manager);
                client.setNetName("");
                client.start();
                break;
            }
        }
    }

    public void shutdown(){
        for(Client client : clientPool){
            try{
                client.join();
            }catch(InterruptedException e){
                System.err.println("CLIENTPOOL: Error while shutting down");
                e.printStackTrace();
            }
        }
    }
}
