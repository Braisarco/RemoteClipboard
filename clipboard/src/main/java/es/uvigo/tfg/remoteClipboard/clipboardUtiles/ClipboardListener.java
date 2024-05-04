package es.uvigo.tfg.remoteClipboard.clipboardUtiles;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class ClipboardListener extends Thread implements ClipboardOwner{
    private Boolean turnOff = false;
    private List<Subscriber> subscribers;
    private Clipboard systemClipboard;
    private Transferable actualContent;

    /*
     * The listener will start working at creation, until the method
     * stopListener is executed.
     */
    public ClipboardListener(){
        subscribers = new ArrayList<>();
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        actualContent = systemClipboard.getContents(this);
    }

    public void run(){
        Transferable trans = systemClipboard.getContents(this);
        systemClipboard.setContents(trans, this);
        System.out.println("CLIPBOARD LISTENER: Listening to clipboard");
    }

    /*
     * When the listener detects a change on the clipboard content it will notify
     * all the subsribers.
     */
    public void lostOwnership(Clipboard clipBoard, Transferable transefable){

        System.out.println("CLIPBOARDLISTENER: Sending new content");

        try{ this.sleep(50); }catch(Exception e){ e.printStackTrace(); }
        actualContent = systemClipboard.getContents(this);
        for (Subscriber subscriber : subscribers) {
            subscriber.execute(systemClipboard.getContents(transefable));
        }
        systemClipboard.setContents(actualContent, this);
    }

    public void subsribe(Subscriber newSubscriber){
        this.subscribers.add(newSubscriber);
    }

    public void stopListener(){
        turnOff = true;
    }
}

