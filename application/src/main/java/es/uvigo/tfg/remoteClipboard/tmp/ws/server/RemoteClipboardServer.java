package es.uvigo.tfg.remoteClipboard.tmp.ws.server;

import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardProxy;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSIB;
import jakarta.xml.ws.Endpoint;


public class RemoteClipboardServer {
    public void publishService(){
        Endpoint.publish("http://localhost/remoteClipboard",
                new RemoteClipboardProxy(new RemoteClipboardSIB()));
    }
}
