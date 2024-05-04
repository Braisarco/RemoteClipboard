package es.uvigo.tfg.remoteClipboard.services;

import es.uvigo.tfg.remoteClipboard.CustomClipboard;
import es.uvigo.tfg.remoteClipboard.net.User;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClipboardManager {
    private CustomClipboard localClipboard;
    private List<CustomClipboard> remoteClipboards;

    public ClipboardManager(){
        this.remoteClipboards = new ArrayList<>();
        try {
            this.localClipboard = new CustomClipboard(InetAddress.getLocalHost().getHostName(), "LocalClipboard");
        }catch (Exception e){
            System.err.println("CLIPBOARDMANAGER: Error creating localClipboard");
        }
    }

    public void createClipboard(String ip, String user){
        System.out.println("CLIPBOARDMANAGER: New remote clipboard created");
        this.remoteClipboards.add(new CustomClipboard(ip, user));
    }

    public void addRemoteContent(Transferable transferable, String ip) throws IOException, UnsupportedFlavorException {
        for(int i = 0; i < remoteClipboards.size(); i++){
            if (remoteClipboards.get(i).getUser().equals(ip)){
                remoteClipboards.get(i).addContent(transferable);
                System.out.println(remoteClipboards.get(i));
            }
        }
    }

    public List<String> getAllNames(){
        List allNames = new ArrayList();
        allNames.add(localClipboard.getUser());
        for (CustomClipboard clipboard : remoteClipboards) {
            allNames.add(clipboard.getUser());
        }
        return allNames;
    }

    public List<String>getLocalUserStringContent(){
        List<String> result = new ArrayList<>();
        for (Transferable transferable : localClipboard.getClipboardContent()){
            try{
                result.add((String)transferable.getTransferData(DataFlavor.stringFlavor));
            }catch (Exception e){
            result.add("Undfined");
            }
        }
        return result;
    }
    public List<String> getRemoteUserStringContent(User usr){
        List<String> toret = new ArrayList<>();
        for (CustomClipboard clipboard : remoteClipboards){
            int index = 0;
            if(usr.getUsername().equals(clipboard.getUser())){
               for(Transferable transferable : clipboard.getClipboardContent()){
                   index ++;
                   if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)){
                       try {
                           toret.add("\t-" + index + (String) transferable.getTransferData(DataFlavor.stringFlavor));
                       }catch (Exception e){
                           System.err.println("ERROR GETTING USER CONTENT");
                       }
                   }else{
                       toret.add("\t-" + index + "Unidentified content");
                   }

               }
               break;
           }
        }
        return toret;
    }

    public List<Transferable> getLocalClipboardContent(){
        return this.localClipboard.getClipboardContent();
    }
    public List<Transferable> getRemoteClipboardContent(String username){
        for(CustomClipboard clipboard : remoteClipboards){
            if(clipboard.getUser().equals(username)){
                return clipboard.getClipboardContent();
            }
        }
        return null;
    }


    public void addLocalContent(Transferable transferable){
        this.localClipboard.addContent(transferable);
    }

    public void removeRemoteClipboard(long id){
    }
}
