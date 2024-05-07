package es.uvigo.tfg.remoteClipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CustomClipboard {
    private List<Transferable> clipboardContent;

    public CustomClipboard() {
        this.clipboardContent = new ArrayList<>();
    }

    public void setClipboardContent(List<Transferable> collection) {
        this.clipboardContent = collection;
    }

    public List<Transferable> getClipboardContent() {
        return this.clipboardContent;
    }

    public List<String> getClipboardContentString(){
        List<String> result = new ArrayList<>();

        for (Transferable transferable : clipboardContent){
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)){
                try {
                    result.add((String)transferable.getTransferData(DataFlavor.stringFlavor));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                result.add("Unidentified transferable");
            }
        }

        return result;
    }

    public void addContent(Transferable newTransferable) {
        this.clipboardContent.add(newTransferable);
    }
}
