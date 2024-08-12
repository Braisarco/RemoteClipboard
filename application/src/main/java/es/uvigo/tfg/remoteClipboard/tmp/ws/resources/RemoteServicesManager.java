package es.uvigo.tfg.remoteClipboard.tmp.ws.resources;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoteServicesManager {
    private Map<String, RemoteClipboardProxy> remoteServices;
    private Executor executor;

    public RemoteServicesManager(){
        this.remoteServices = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(50);
    }

    public void addRemoteService(String username, RemoteClipboardProxy service){
        this.remoteServices.put(username, service);
    }

    public void removeUserFromNet(String username, String localUsername, String netName){
        this.remoteServices.get(username).removeUserFromNet(netName, localUsername);
    }

    public void dissconnect(String user){
        this.remoteServices.forEach((k,v)->{
            this.executor.execute(()->v.removeUser(user));
        });
    }

    public void removeService(String username){
        this.remoteServices.remove(username);
    }

    public void sendContent(CustomTransferable content){
        this.remoteServices.forEach((k,v) ->{
            this.executor.execute(() -> v.addContent(content));
        });
    }
}