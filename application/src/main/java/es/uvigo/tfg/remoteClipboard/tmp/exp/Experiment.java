package es.uvigo.tfg.remoteClipboard.tmp.exp;

import static java.util.stream.Collectors.toSet;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;

interface RemoteClipboard {
  public void registerTransferable(String user, CustomTransferable transferable);
}

public class Experiment implements ClipboardOwner {
  private Map<String, List<RemoteClipboard>> networks;
  private Executor executor;

  public Experiment() {
    this.executor = Executors.newFixedThreadPool(50);
  }

  private Set<RemoteClipboard> listRemoteClipboards() {
    return this.networks.values().stream().flatMap(List::stream).collect(toSet());
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    try {
      // Notificar remotos
      CustomTransferable transferable = new CustomTransferable(contents);

      for (RemoteClipboard remote : this.listRemoteClipboards()) {
        this.executor.execute(() -> remote.registerTransferable("EU", transferable));
      }
    } catch (UnsupportedFlavorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    this.executor.execute(() -> {
      clipboard.setContents(contents, this);
    });
  }
  
  public static void main(String[] args) {
  }

}
