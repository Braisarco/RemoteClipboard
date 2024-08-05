package es.uvigo.tfg.remoteClipboard.tmp.ws.server;

import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSIB;

import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;


public class RemoteClipboardServer {
    private RemoteClipboardSIB clipboard;
    public RemoteClipboardServer(RemoteClipboardSIB cb){
        this.clipboard = cb;
    }
    public void publishService(){
        try{
            Endpoint.publish("http://"+ InetAddress.getLocalHost().getHostAddress() +":1010/remoteClipboard",
                    this.clipboard);
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
    }
}
