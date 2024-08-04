package es.uvigo.tfg.remoteClipboard.tmp.ws.server;

import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSIB;

import javax.xml.ws.Endpoint;


public class RemoteClipboardServer {
    private RemoteClipboardSIB clipboard;
    public RemoteClipboardServer(RemoteClipboardSIB cb){
        this.clipboard = cb;
    }
    public void publishService(){
        Endpoint.publish("http://localhost:1010/remoteClipboard",
                this.clipboard);
    }
}
