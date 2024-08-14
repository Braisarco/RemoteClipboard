package es.uvigo.tfg.remoteClipboard.ws.app;

import es.uvigo.tfg.remoteClipboard.ws.app.console.ConsoleApplication;
import es.uvigo.tfg.remoteClipboard.ws.service.RemoteClipboardSIB;

public class MainApp {
    public static void main(String[]args){
        RemoteClipboardSIB clipboard = new RemoteClipboardSIB();
        ConsoleApplication app = new ConsoleApplication(clipboard);
        app.init();
    }
}
