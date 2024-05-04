package es.uvigo.tfg.remoteClipboard;

import es.uvigo.tfg.remoteClipboard.net.Network;
import es.uvigo.tfg.remoteClipboard.server.Server;
import es.uvigo.tfg.remoteClipboard.server.servers.ServerThreadPool;
import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;
import es.uvigo.tfg.remoteClipboard.services.ui.UIApplication;
import es.uvigo.tfg.remoteClipboard.client.Client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private Server server;
    private Client client;
    private ClipboardManager clipboardManager;

    public Application (){
        System.out.println("APPLICATION: Initialaizing application components");
        this.clipboardManager = new ClipboardManager();
        //this.server = new ServerThreadPool(this.clipboardManager);
        try{
           // this.client = new Client(this.clipboardManager, ,InetAddress.getLocalHost().getHostName());
        }catch(Exception e){
            e.printStackTrace();
        }
        UIApplication windowApp = new UIApplication(clipboardManager);
    }

    public static void main(String[] args) {
        ConsoleApplication app = new ConsoleApplication();
        app.inicializate();
    }
}
