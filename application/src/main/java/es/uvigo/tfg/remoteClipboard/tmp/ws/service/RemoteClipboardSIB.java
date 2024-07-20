package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;

import javax.jws.WebService;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI")
public class RemoteClipboardSIB implements RemoteClipboardSEI {
  private User localUser;
  private List<User> remoteUsers;
  private Map<String, List<String>> nets;
  private List<RemoteClipboardSEI> remoteServices;

  public RemoteClipboardSIB(){
    this.localUser = new User("localUser", "wsdl");
    this.remoteUsers = new ArrayList<>();
    this.nets = new HashMap<>();
    this.remoteServices = new ArrayList<>();
  }

  public List<RemoteClipboardSEI> getRemoteServices(){
    return this.remoteServices;
  }

  public String getLocalUsername(){
    return this.localUser.getUsername();
  }

  public boolean addLocalContent(Transferable content){
    return this.localUser.addContent(content);
  }

  @Override
  public boolean register(String username, String wsdl, List<String> nets){
    User auxiliarUser = new User(username, wsdl);
    boolean userAdded = false;
    if(this.userAlreadyExists(username) == -1){
      for (String net : nets){
        if (this.nets.containsKey(net)){
          if (!this.remoteUsers.contains(auxiliarUser)){
            this.remoteUsers.add(auxiliarUser);
            userAdded = true;
          }
          this.nets.get(net).add(username);
        }
      }
    }
    return userAdded;
  }

  @Override
  public boolean removeUser(String username){
    boolean userRemoved = false;
    int userIndex = this.userAlreadyExists(username);

    if (userIndex != -1){
      this.remoteUsers.remove(userIndex);
      userRemoved = true;
      this.nets.forEach((k,v)->{
        v.remove(username);
      });
    }
    return userRemoved;
  }

  @Override
  public List<User> getRemoteUsers(List<String> netNames){
    List<User> result = new ArrayList<>();
    for (String net : netNames){
      if (nets.containsKey(net)) {
        result.add(this.localUser);
        for (User usr : this.remoteUsers){
          if (this.nets.get(net).contains(usr.getUsername()) && !result.contains(usr)){
            result.add(usr);
          }
        }
      }
    }
    return result;
  }

  public List<String> getUserContent(String username){
    if (username.equals(localUser.getUsername())){
      return this.localUser.getContentStr();
    } else{
      int index = userAlreadyExists(username);
      if (index != -1) {
        return this.remoteUsers.get(index).getContentStr();
      }
    }
    return null;
  }

  private int userAlreadyExists(String username){
    int userExists = -1;
    for (User user : this.remoteUsers){
      if (user.getUsername().equals(user)){
        this.remoteUsers.indexOf(user);
        break;
      }
    }
    return userExists;
  }

  public boolean createNet(String netName){
    boolean netCreated = false;
    List<String> emptyUsers = new ArrayList<>();
    if (!this.nets.containsKey(netName)){
      emptyUsers.add(this.localUser.getUsername());
      this.nets.put(netName, emptyUsers);
      netCreated = true;
    }
    return netCreated;
  }

  public boolean removeNet(String netName){
    boolean netRemoved = false;
    if (this.nets.containsKey(netName)){
      this.nets.remove(netName);
      netRemoved = true;
    }
    return netRemoved;
  }

  public List<String> getAvaliableNets(){
    return this.nets.keySet().stream().toList();
  }

  public List<String> getUsersOfNet(String netName){
    if (this.nets.containsKey(netName)){
      return this.nets.get(netName);
    }else{
      return null;
    }
  }

  @Override
  public boolean addContent(String username, Transferable content){
    boolean contentAdded = false;
    int userIndex = this.userAlreadyExists(username);
    if (userIndex != -1){
      contentAdded = this.remoteUsers.get(userIndex).addContent(content);
    }
    return contentAdded;
  }
}
