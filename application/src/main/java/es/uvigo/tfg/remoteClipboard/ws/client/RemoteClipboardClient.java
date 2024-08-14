package es.uvigo.tfg.remoteClipboard.ws.client;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.ws.utils.RemoteServicesManager;
import es.uvigo.tfg.remoteClipboard.ws.utils.User;
import es.uvigo.tfg.remoteClipboard.ws.service.RemoteClipboardProxy;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.net.MalformedURLException;
import java.util.List;

public class RemoteClipboardClient implements ClipboardOwner {
    private RemoteServicesManager services;
    private Transferable actualContent;
    private Clipboard systemClipboard;
    private String user;

    public RemoteClipboardClient(String username, RemoteServicesManager servicesManager){
        this.user = username;
        this.services = servicesManager;
        this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.actualContent = this.systemClipboard.getContents(this);
        this.systemClipboard.setContents(this.actualContent, this);
    }

    public List<User> connect(String wsdl, List<String> nets) throws MalformedURLException {
        RemoteClipboardProxy clipboardService = new RemoteClipboardProxy(this.user, wsdl,nets);
        clipboardService.register();
        if (this.services.serviceAlreadyExist(clipboardService.getUsername()) &&
                clipboardService.addUserToNet(this.user, nets)){
            List<User> remoteUsers = clipboardService.getRemoteUsers(nets);
            addAllRemoteServices(remoteUsers, nets);
            return remoteUsers;
        }else {
            this.services.addRemoteService(clipboardService.getUsername(), clipboardService);
            List<User> remoteUsers = clipboardService.getRemoteUsers(nets);
            addAllRemoteServices(remoteUsers, nets);
            return remoteUsers;
        }
    }

    private void addAllRemoteServices(List<User> users, List<String> nets){
        for (User user : users){
            if (!user.getUsername().equals(this.user) && this.services.serviceAlreadyExist(user.getUsername())) {
                RemoteClipboardProxy clipboardService = new RemoteClipboardProxy(user.getUsername(), user.getWsdl(), nets);
                if (clipboardService.register()) {
                    this.services.addRemoteService(clipboardService.getUsername(), clipboardService);
                }
            }
        }
    }

    public void dissconnect(){
        this.services.dissconnect(this.user);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
        try{
            Thread.sleep(200);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.actualContent = this.systemClipboard.getContents(this);

        try{
            this.services.sendContent(new CustomTransferable(transferable));
        }catch (Exception e){
            e.printStackTrace();
        }
        this.systemClipboard.setContents(transferable, this);
    }
}
