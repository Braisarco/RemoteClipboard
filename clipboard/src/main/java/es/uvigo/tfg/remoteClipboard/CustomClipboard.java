package es.uvigo.tfg.remoteClipboard;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CustomClipboard {
    private List<Transferable> clipboardContent;
    private String ip;
    private String user;

    public CustomClipboard(String ip, String user) {
        this.ip = ip;
        this.user = user;
        this.clipboardContent = new ArrayList<>();
    }

    public String getIp() {
        return this.ip;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setClipboardContent(List<Transferable> collection) {
        this.clipboardContent = collection;
    }

    public List<Transferable> getClipboardContent() {
        return this.clipboardContent;
    }

    public void addContent(Transferable newTransferable) {
        this.clipboardContent.add(newTransferable);
    }

    public Byte[] getBytes(){
        return this.getBytes();
    }

//    public List<Transferable> getContent(int amount){
//
//    }
}
