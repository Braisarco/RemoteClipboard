package es.uvigo.tfg.remoteClipboard.ws.utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CustomTransferable implements Serializable, Transferable {
  private static final long serialVersionUID = 1L;
  private Map<DataFlavor, Object> customData;

  public CustomTransferable() {
  }

  public CustomTransferable(Transferable transferable) throws UnsupportedFlavorException, IOException {
    customData = new HashMap<>();
    for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
      if (transferable.getTransferData(flavor) instanceof Serializable) {
        this.customData.put(flavor, transferable.getTransferData(flavor));
      }
    }
  }

  // public CustomTransferable(String base64){
  // byte[] bytes = Base64.getDecoder().decode(base64);
  // ByteArrayInputStream input = new ByteArrayInputStream(bytes);
  // try (ObjectInputStream objectInput = new ObjectInputStream(input)){
  // this = objectInput.readObject();
  // objectInput.close();
  // }catch(Exception e){
  // System.err.println("CUSTOMTRANSFERABLE: Error decoding object");
  // }
  // }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return this.customData.keySet().toArray(DataFlavor[]::new);
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return this.customData.containsKey(flavor);
  }

  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return this.customData.get(flavor);
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  public byte[] serialize() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
      objectOutput.writeObject(this);
    } catch (Exception e) {
      System.err.println("CUSTOMTRANSFERABLE: Error while serializing transferable");
      e.printStackTrace();
    }

    return output.toByteArray();
  }
}
