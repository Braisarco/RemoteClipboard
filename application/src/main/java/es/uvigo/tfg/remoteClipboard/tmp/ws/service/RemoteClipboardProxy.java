package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RegisterResult;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.awt.datatransfer.Transferable;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoteClipboardProxy {
  private RemoteClipboardSEI remoteClipboard;
  private String username;
  private String wsdl;
  private List<String> networks;
  
  public RemoteClipboardProxy(String user, String wsdl, List<String> nets) {
    this.remoteClipboard = null;
    this.username = user;
    this.wsdl = wsdl;
    this.networks = nets;
  }

  public boolean register() {
    for (int attempts = 0; attempts < 3; attempts++) {
      try {
        if (this.remoteClipboard == null) {
          URL url = new URL(this.wsdl);
          QName name = new QName(
                  "http://service.ws.tmp.remoteClipboard.tfg.uvigo.es/",
                  "RemoteClipboardSIBService");

          Service service = Service.create(url, name);
          this.remoteClipboard = service.getPort(RemoteClipboardSEI.class);
        }

        return this.remoteClipboard.register(this.username, "http://" +
                InetAddress.getLocalHost().getHostAddress() +":10101/remoteClipboard?wsdl", this.networks);
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

  public String getUsername(){
    return this.remoteClipboard.getUsername();
  }
  public boolean removeUserFromNet(String netName, String username){
    return this.remoteClipboard.removeUserFromNet(netName, username);
  }

  public boolean addContent(CustomTransferable content){
    return this.remoteClipboard.addContent(this.username, content);
  }

  public boolean removeUser(String user){
    return this.remoteClipboard.removeUser(user);
  }

  public List<User> getRemoteUsers(List<String> nets){
    return this.remoteClipboard.getRemoteUsers(nets);
  }

  public boolean addUserToNet(String username, List<String> nets){
    return this.remoteClipboard.addUserToNet(username, nets);
  }
}
