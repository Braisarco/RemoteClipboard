package es.uvigo.tfg.remoteClipboard.clipboardUtiles;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public class ClipboardModifier implements Subscriber, ClipboardOwner {
    private Clipboard systemClipboard;

    public ClipboardModifier(){
        this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    private void setContent(Transferable transferable){
        this.systemClipboard.setContents(transferable, this);
    }

    @Override
    public void execute(Transferable transferable) {
        setContent(transferable);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
