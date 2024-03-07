package org.tfg;

import org.tfg.client.Client;
import org.tfg.server.Server;
import org.tfg.server.servers.ServerThreadPool;
import org.tfg.services.ClipboardManager;

public class Application {
    private Server server;
    private Client client;
    private ClipboardManager clipboardManager;

    public Application (){
        System.out.println("APPLICATION: Initialaizing application components");
        this.clipboardManager = new ClipboardManager();
        this.server = new ServerThreadPool(this.clipboardManager);
        this.client = new Client(this.clipboardManager);
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.server.start();
        app.client.start();
        while (true) {

        }
    }
}
