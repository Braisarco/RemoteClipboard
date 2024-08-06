package es.uvigo.tfg.remoteClipboard.tmp.ws.resources;

import es.uvigo.tfg.remoteClipboard.CustomClipboard;
import es.uvigo.tfg.remoteClipboard.CustomTransferable;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String wsdl;
    private List<String> nets;
    private CustomClipboard clipboard;

    public User(){}

    public User(String name, String wsdl){
        this.username = name;
        this.wsdl = wsdl;
        this.nets = new ArrayList<>();
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

    public void addNet(String net){
        this.nets.add(net);
    }

    public void removeNet(String net){
        this.nets.remove(net);
    }

    public List<String> getNets(){
        return this.nets;
    }

    public List<String> getContentStr(){
        return this.clipboard.getClipboardContentString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof User && this.username.equals(((User) obj).getUsername())){
            equal = true;
        }
        return equal;
    }
}

