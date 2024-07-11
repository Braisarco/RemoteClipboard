package es.uvigo.tfg.remoteClipboard.tmp;

import java.util.List;

public class RemoteClipboardProxy implements RemoteClipboardSEI {
  private final String wsdl;
  private final String serviceName;
  private RemoteClipboardSEI remoteClipboard;
  
  public RemoteClipboardProxy(String wsdl, String serviceName) {
    this.wsdl = wsdl;
    this.serviceName = serviceName;
  }

  @Override
  public boolean register(String user, String wsdl, List<String> networks) {
    for (int attempts = 0; attempts < 3; attempts++) {
      try {
        if (this.remoteClipboard == null) {
          this.remoteClipboard = null;// Service.create(this.wsdl, this.serviceName);
        }
        
        return this.remoteClipboard.register(user, wsdl, networks);
      } catch (Exception e) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }
    
    throw new RuntimeException("Error stablishing connection");
  }

}
