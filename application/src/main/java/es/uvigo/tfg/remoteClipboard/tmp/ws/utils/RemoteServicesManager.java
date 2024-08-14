package es.uvigo.tfg.remoteClipboard.tmp.ws.utils;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public RemoteClipboardProxy getServiceByName(String username){
        if (this.remoteServices.containsKey(username)){
            return this.remoteServices.get(username);
        }else{
            return null;
        }
    }

    public void removeUserFromNet(String username, String localUsername, String netName){
        if (!this.remoteServices.isEmpty()){
            this.remoteServices.get(username).removeUserFromNet(netName, localUsername);
        }
    }

    public void dissconnect(String user){
        this.remoteServices.forEach((k,v)->{
            this.executor.execute(()->v.removeUser(user));
        });
        this.remoteServices.clear();
    }

    public void removeService(String username){
        this.remoteServices.get(username).removeUser(username);
        this.remoteServices.remove(username);
    }

    public void sendContent(CustomTransferable content){
        this.remoteServices.forEach((k,v) ->{
            this.executor.execute(() -> v.addContent(content));
        });
    }

    public boolean serviceAlreadyExist(String username){
        AtomicBoolean userExist = new AtomicBoolean(false);
        this.remoteServices.forEach((k,v) ->{
            String auxiliarUsername = v.getUsername();
            if (auxiliarUsername.equals(username)){
                userExist.set(true);
            }
        });
        return userExist.get();
    }
}
