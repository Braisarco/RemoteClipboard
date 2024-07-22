package es.uvigo.tfg.remoteClipboard.tmp.ws.client;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoteClipboardClient implements ClipboardOwner {
    private List<RemoteClipboardSEI> remoteServices;
    private Executor executor;

    public RemoteClipboardClient(){
        this.remoteServices = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(50);
    }

    public List<User> connect(String wsdl, List<String> nets) throws MalformedURLException {
        URL url = new URL(wsdl);
        QName name = new QName(
                "http://es.uvigo.tfg.remoteClipboard.tmp.ws.service/",
                "RemoteClipboard");

        Service service = Service.create(url, name);
        RemoteClipboardSEI clipboardService = service.getPort(RemoteClipboardSEI.class);

        if (clipboardService.register("me", "wsdl", nets)){
            this.remoteServices.add(clipboardService);
            List<User> remoteUsers = clipboardService.getRemoteUsers(nets);
            addAllRemoteServices(remoteUsers, nets);
            return remoteUsers;
        }

        return null;
    }

    private void addAllRemoteServices(List<User> users, List<String> nets) throws MalformedURLException {
        URL url = null;
        QName name = new QName(
                "http://es.uvigo.tfg.remoteClipboard.tmp.ws.service/",
                "RemoteClipboard");
        for (User user : users){
            url = new URL(user.getWsdl());
            Service service = Service.create(url, name);
            RemoteClipboardSEI clipboardService = service.getPort(RemoteClipboardSEI.class);

            if(clipboardService.register("me", "wsdl", nets)){
                this.remoteServices.add(clipboardService);
            }
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        try {
            CustomTransferable transferable = new CustomTransferable(contents);

            for (RemoteClipboardSEI service : this.remoteServices) {
                this.executor.execute(() -> service.addContent("EU", transferable));
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
