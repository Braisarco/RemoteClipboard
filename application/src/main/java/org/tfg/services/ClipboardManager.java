package org.tfg.services;

import org.tfg.ClipboardType;
import org.tfg.CustomClipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClipboardManager {
    private CustomClipboard localClipboard;
    private List<CustomClipboard> remoteClipboards;

    public ClipboardManager(){
        this.remoteClipboards = new ArrayList<>();
        this.localClipboard = new CustomClipboard(ClipboardType.LOCAL, 1);
    }

    public void createClipboard(long id){
        System.out.println("CLIPBOARDMANAGER: New remote clipboard created");
        this.remoteClipboards.add(new CustomClipboard(ClipboardType.REMOTE, id));
    }

//    public void addRemoteContent(Transferable transferable, long id){
//        for (CustomClipboard clipboard : this.applicationClipboards){
//            if(clipboard.getId() == id){
//                clipboard.addContent(transferable);
//            }
//        }
//    }
    public void addRemoteContent(Transferable transferable) throws IOException, UnsupportedFlavorException {
        this.remoteClipboards.get(0).addContent(transferable);
        System.out.println(this.remoteClipboards.get(0).getClipboardContent().get(0).getTransferData(DataFlavor.stringFlavor));
    }


    public void addLocalContent(Transferable transferable){
        this.localClipboard.addContent(transferable);
    }

    public void removeRemoteClipboard(long id){
    }
}
