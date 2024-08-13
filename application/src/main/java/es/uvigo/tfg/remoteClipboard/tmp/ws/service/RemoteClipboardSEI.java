package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RegisterResult;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.datatransfer.Transferable;
import java.util.List;

@WebService(name = "RemoteClipboard")
//@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface RemoteClipboardSEI {
  @WebMethod
  boolean removeUserFromNet(String netName, String username);
  @WebMethod
  String getUsername();
  @WebMethod
  boolean register(String user, String wsdl, List<String> nets);
  @WebMethod
  boolean addUserToNet(String user, List<String> nets);
  @WebMethod
  boolean addContent(String user, CustomTransferable content);

  @WebMethod
  boolean removeUser(String user);

  @WebMethod
  List<User> getRemoteUsers(List<String> nets);
}
