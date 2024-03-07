package org.tfg;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class CustomClipboard {
    private List<Transferable> clipboardContent;
    private long id;
    private ClipboardType type;

    public CustomClipboard(ClipboardType type, long id){
        this.id = id;
        this.clipboardContent = new ArrayList<>();
        this.type = type;
    }

    public long getId(){ return this.id; }

    public void setClipboardContent(List<Transferable> collection){
        this.clipboardContent = collection;
    }

    public List<Transferable> getClipboardContent(){ return this.clipboardContent; }
    public void addContent(Transferable newTransferable){
        this.clipboardContent.add(newTransferable);
    }

//    public List<Transferable> getContent(int amount){
//
//    }

    public ClipboardType getType(){ return this.type; }
}
