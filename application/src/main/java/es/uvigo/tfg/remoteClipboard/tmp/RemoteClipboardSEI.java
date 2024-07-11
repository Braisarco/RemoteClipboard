package es.uvigo.tfg.remoteClipboard.tmp;

import java.util.List;

public interface RemoteClipboardSEI {
  public boolean register(String user, String wsdl, List<String> networks);
}
