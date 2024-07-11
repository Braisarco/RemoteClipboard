package es.uvigo.tfg.remoteClipboard.clipboardUtiles;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClipboardListener extends Thread implements ClipboardOwner {
  private List<Subscriber> subscribers;
  private Clipboard systemClipboard;
  private Transferable actualContent;
  private Executor executor;

  /*
   * The listener will start working at creation, until the method stopListener is executed.
   */
  public ClipboardListener() {
    subscribers = new ArrayList<>();
    systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    actualContent = systemClipboard.getContents(this);
    executor = Executors.newFixedThreadPool(1);
  }

  public void takeOwnership() {
    Transferable trans = systemClipboard.getContents(this);
    systemClipboard.setContents(trans, this);
    System.out.println("CLIPBOARD LISTENER: Listening to clipboard");
  }

  /*
   * When the listener detects a change on the clipboard content it will notify all the subsribers.
   */
  public void lostOwnership(Clipboard clipBoard, Transferable transefable) {
    executor.execute(() -> {
      System.out.println("CLIPBOARDLISTENER: Sending new content");
      
      try {
        Thread.sleep(50);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      clipBoard.setContents(transefable, this);
      for (Subscriber subscriber : subscribers) {
        subscriber.execute(transefable);
      }
      
//      actualContent = systemClipboard.getContents(this);
//      for (Subscriber subscriber : subscribers) {
//        subscriber.execute(systemClipboard.getContents(transefable));
//      }
//      systemClipboard.setContents(actualContent, this);
    });
  }

  public void subsribe(Subscriber newSubscriber) {
    this.subscribers.add(newSubscriber);
  }
}
