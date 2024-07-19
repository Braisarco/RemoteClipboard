package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;

import java.awt.datatransfer.Transferable;
import java.util.List;

public class RemoteClipboardProxy implements RemoteClipboardSEI {
  private RemoteClipboardSEI remoteClipboard;
  
  public RemoteClipboardProxy(RemoteClipboardSEI rc) {
    this.remoteClipboard = rc;
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

  @Override
  public boolean addContent(String user, Transferable content) {
    return remoteClipboard.addContent(user, content);
  }

  @Override
  public boolean removeUser(String user) {
    return remoteClipboard.removeUser(user);
  }

  @Override
  public List<User> getRemoteUsers(List<String> nets) {
    return remoteClipboard.getRemoteUsers(nets);
  }

}
