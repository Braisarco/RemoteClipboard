package es.uvigo.tfg.remoteClipboard.tmp.ws.client;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RegisterResult;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RemoteServicesManager;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class RemoteClipboardClient implements ClipboardOwner {
    private RemoteServicesManager services;
    private String user;

    public RemoteClipboardClient(String username, RemoteServicesManager servicesManager){
        this.user = username;
        this.services = servicesManager;
    }

    public List<User> connect(String wsdl, List<String> nets) throws MalformedURLException {
        RemoteClipboardProxy clipboardService = new RemoteClipboardProxy(this.user, wsdl,nets);
        RegisterResult connection = clipboardService.register();
        if (connection.equals(RegisterResult.REGISTERED)){
            this.services.addRemoteService(clipboardService.getUsername(), clipboardService);
            List<User> remoteUsers = clipboardService.getRemoteUsers(nets);
            addAllRemoteServices(remoteUsers, nets);
            return remoteUsers;
        } else if (connection.equals(RegisterResult.EXIST)) {
            List<User> remoteUsers = clipboardService.getRemoteUsers(nets);
            addAllRemoteServices(remoteUsers, nets);
            return remoteUsers;
        }
        return null;
    }

    private void addAllRemoteServices(List<User> users, List<String> nets){
        for (User user : users){
            if (!user.getUsername().equals(this.user)) {
                RemoteClipboardProxy clipboardService = new RemoteClipboardProxy(user.getUsername(), user.getWsdl(), nets);
                if (clipboardService.register().equals(RegisterResult.REGISTERED)) {
                    this.services.addRemoteService(clipboardService.getUsername(), clipboardService);
                }
            }
        }
    }

    public void dissconnect(){
        this.services.dissconnect(this.user);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        try {
            CustomTransferable transferable = new CustomTransferable(clipboard.getContents(this));
            this.services.sendContent(transferable);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clipboard.setContents(contents, this);
    }
}
