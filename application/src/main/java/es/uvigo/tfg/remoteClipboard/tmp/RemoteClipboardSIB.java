package es.uvigo.tfg.remoteClipboard.tmp;

import java.util.List;

public class RemoteClipboardSIB implements RemoteClipboardSEI {

  @Override
  public boolean register(String user, String wsdl, List<String> networks) {
    return false;
  }

  
  public static void main(String[] args) {
    // RemoteClipboardSEI remote = Service.create("wsdl", "servicio");
    RemoteClipboardSEI remote = null;
  }
}
