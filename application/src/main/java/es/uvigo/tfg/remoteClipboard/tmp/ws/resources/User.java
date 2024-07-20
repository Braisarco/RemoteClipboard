package es.uvigo.tfg.remoteClipboard.tmp.ws.resources;

import es.uvigo.tfg.remoteClipboard.CustomClipboard;
import es.uvigo.tfg.remoteClipboard.CustomTransferable;

import java.awt.datatransfer.Transferable;
import java.util.List;

public class User {
    private String username;
    private String wsdl;
    private CustomClipboard clipboard;

    public User(String name, String wsdl){
        this.username = name;
        this.wsdl = wsdl;
        this.clipboard = new CustomClipboard();
    }

    public String getUsername() {
        return username;
    }

    public String getWsdl(){
        return this.wsdl;
    }

    public boolean addContent(Transferable content){
        try{
            this.clipboard.addContent(content);
            return true;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<String> getContentStr(){
        return this.clipboard.getClipboardContentString();
    }
}
