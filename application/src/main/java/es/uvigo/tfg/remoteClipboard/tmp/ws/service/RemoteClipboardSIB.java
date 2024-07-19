package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.tmp.ws.db.DataBase;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;

import javax.jws.WebService;

import java.awt.datatransfer.Transferable;
import java.util.List;

@WebService(endpointInterface = "es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI")
public class RemoteClipboardSIB implements RemoteClipboardSEI {
  private DataBase db;

  public RemoteClipboardSIB(DataBase data){
    this.db = data;
  }

  @Override
  public boolean addContent(String user, Transferable content){
    return db.addContent(user, content);
  }

  @Override
  public boolean removeUser(String user) {
    return false;
  }

  @Override
  public boolean register(String username, String wsdl, List<String> networks){
    return db.addUser(username, wsdl, networks);
  }

  @Override
  public List<User> getRemoteUsers(List<String> nets){
    return this.db.getNetUsers(nets);
  }
}
