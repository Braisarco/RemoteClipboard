package es.uvigo.tfg.remoteClipboard.ws.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {
        "username",
        "wsdl",
        "nets",
        "clipboard"
})
public class User {
    private String username;
    private String wsdl;
    @XmlElement
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
            this.clipboard.addContent(new CustomTransferable(content));
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
    public void selectClipboardContent(int index, ClipboardOwner owner){
        this.clipboard.selectTransferable(index, owner);
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

