package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.datatransfer.Transferable;
import java.util.List;

@WebService(name = "RemoteClipboard")
//@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface RemoteClipboardSEI {
  @WebMethod
  public boolean register(String user, String wsdl, List<String> nets);
  @WebMethod
  public boolean addContent(String user, CustomTransferable content);

  @WebMethod
  public boolean removeUser(String user);

  @WebMethod
  public List<User> getRemoteUsers(List<String> nets);
}
