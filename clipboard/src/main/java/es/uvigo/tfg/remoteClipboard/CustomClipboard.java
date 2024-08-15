package es.uvigo.tfg.remoteClipboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class CustomClipboard {
  private List<CustomTransferable> clipboardContent;
  private Clipboard systemClipboard;

  public CustomClipboard() {
    this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    this.clipboardContent = new ArrayList<>();
  }

  public void setClipboardContent(List<CustomTransferable> collection) {
    this.clipboardContent = collection;
  }

  public List<CustomTransferable> getClipboardContent() {
    return this.clipboardContent;
  }

  public List<String> getClipboardContentString() {
    List<String> result = new ArrayList<>();
    int contentIndex = 0;

    for (Transferable transferable : clipboardContent) {
      if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          result.add(contentIndex + ". " + (String) transferable.getTransferData(DataFlavor.stringFlavor));
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        result.add("Unidentified transferable");
      }
    }

    return result;
  }

  public void addContent(CustomTransferable newTransferable) {
    this.clipboardContent.add(newTransferable);
  }

  public void selectTransferable(int index, ClipboardOwner owner){
    this.systemClipboard.setContents(this.clipboardContent.get(index), owner);
  }
}
