package es.uvigo.tfg.remoteClipboard.tmp.ws.client;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoteClipboardClient implements ClipboardOwner {
    private List<RemoteClipboardProxy> remoteServices;
    private Executor executor;
    private String user;

    public RemoteClipboardClient(String username){
        this.user = username;
        this.remoteServices = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(50);
    }

    public List<User> connect(String wsdl, List<String> nets) throws MalformedURLException {
        RemoteClipboardProxy clipboardService = new RemoteClipboardProxy(this.user, wsdl,nets);
        if (clipboardService.register()){
            this.remoteServices.add(clipboardService);
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
                if (clipboardService.register()) {
                    this.remoteServices.add(clipboardService);
                }
            }
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        try {
            CustomTransferable transferable = new CustomTransferable(clipboard.getContents(this));

            for (RemoteClipboardProxy service : this.remoteServices) {
                this.executor.execute(() -> service.addContent(transferable));
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.executor.execute(() -> {
            clipboard.setContents(contents, this);
        });
    }
}
