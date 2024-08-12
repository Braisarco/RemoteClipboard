package es.uvigo.tfg.remoteClipboard.tmp.ws.resources;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoteServicesManager {
    private List<RemoteClipboardProxy> remoteServices;
    private Executor executor;

    public RemoteServicesManager(){
        this.remoteServices = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(50);
    }

    public void addRemoteService(RemoteClipboardProxy service){
        this.remoteServices.add(service);
    }

    public void removeRemoteService(String username){
        for (RemoteClipboardProxy service : this.remoteServices){
            if (service.getUsername().equals(username)){
                this.remoteServices.remove(service);
                break;
            }
        }
    }

    public void removeAllServices(String user){
        for (RemoteClipboardProxy service : this.remoteServices){
            this.executor.execute(() -> service.removeUser(user));
        }
    }

    public void sendContent(CustomTransferable content){
        for (RemoteClipboardProxy service : this.remoteServices){
            this.executor.execute(() -> service.addContent(content));
        }
    }
}
