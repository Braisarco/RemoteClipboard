package es.uvigo.tfg.remoteClipboard.tmp.ws.service;

import es.uvigo.tfg.remoteClipboard.CustomTransferable;
import es.uvigo.tfg.remoteClipboard.tmp.ws.client.RemoteClipboardClient;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RegisterResult;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.RemoteServicesManager;
import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;

import javax.jws.WebService;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI")
public class RemoteClipboardSIB implements RemoteClipboardSEI {
  private User localUser;
  private List<User> remoteUsers;
  private Map<String, List<String>> nets;
  private RemoteServicesManager remoteServices;
  private RemoteClipboardClient client;

  public RemoteClipboardSIB() {
    this.client = null;
    try{
      this.localUser = new User(InetAddress.getLocalHost().getHostName(), "http://" +
              InetAddress.getLocalHost().getHostAddress() +":10101/remoteClipboard?wsdl");
    }catch(UnknownHostException e){
      e.printStackTrace();
    }
    this.remoteUsers = new ArrayList<>();
    this.nets = new HashMap<>();
  }
  public void setClient (RemoteClipboardClient cl){
    this.client = cl;
  }
  public void setRemoteServices (RemoteServicesManager services){
    this.remoteServices = services;
  }
  public String getLocalUsername(){
    return this.localUser.getUsername();
  }

  public boolean addLocalContent(CustomTransferable content){
    return this.localUser.addContent(content);
  }

  @Override
  public String getUsername(){
    return this.localUser.getUsername();
  }

  @Override
  public boolean register(String username, String wsdl, List<String> nets){
    User auxiliarUser = new User(username, wsdl);
    boolean result = false;
    if(this.userAlreadyExists(username) == -1){
      for (String net : nets){
        if (this.nets.containsKey(net)){
          if (!this.remoteUsers.contains(auxiliarUser)){
            auxiliarUser.addNet(net);
            this.remoteUsers.add(auxiliarUser);
            result = true;
          }
          this.nets.get(net).add(username);
          try{
            if (client != null){
              this.client.connect(wsdl,nets);
            }
          }catch(MalformedURLException e){
            e.printStackTrace();
          }
        }
      }
    }
    return result;
  }

  @Override
  public boolean removeUser(String username){
    boolean userRemoved = false;
    int userIndex = this.userAlreadyExists(username);

    if (userIndex >= 0){
      this.remoteUsers.remove(userIndex);
      this.remoteServices.removeService(username);
      userRemoved = true;
      this.nets.forEach((k,v)->{
        v.remove(username);
      });
    }
    return userRemoved;
  }
  @Override
  public boolean removeUserFromNet(String netName, String username){
    boolean userRemoved = false;
    int userIndex = this.userAlreadyExists(username);

    if (userIndex >= 0 && this.nets.containsKey(netName)){
      this.nets.get(netName).remove(username);
      if (this.remoteUsers.get(userIndex).getNets().isEmpty()){
        this.removeUser(username);
        this.remoteServices.removeService(username);
      }
    }

    return userRemoved;
  }

  @Override
  public boolean addUserToNet(String username, List<String> nets){
    boolean userAdded = false;
    int userIndex = userAlreadyExists(username);
    if (userIndex > 0){
      for (String net : nets){
        if (this.nets.containsKey(net)){
          this.nets.get(net).add(username);
          this.remoteUsers.get(userIndex).addNet(net);
          userAdded = true;
        }
      }
    }
    return userAdded;
  }

  @Override
  public List<User> getRemoteUsers(List<String> netNames){
    List<User> result = new ArrayList<>();
    for (String net : netNames){
      if (this.nets.containsKey(net)) {
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
      if (index >= 0) {
        return this.remoteUsers.get(index).getContentStr();
      }
    }
    return null;
  }

  private int userAlreadyExists(String username){
    int userExists = -1;
    if (this.localUser.getUsername().equals(username)) {
      userExists = -2;
    }else{
      for (User user : this.remoteUsers){
        if (user.getUsername().equals(username)){
          userExists = this.remoteUsers.indexOf(user);
          break;
        }
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
      this.localUser.addNet(netName);
      netCreated = true;
    }
    return netCreated;
  }

  public boolean removeNet(String netName){
    boolean netRemoved = false;
    int userIndex = 0;
    if (this.nets.containsKey(netName)){
      for (String user : this.nets.get(netName)){
        userIndex = this.userAlreadyExists(user);
        this.remoteServices.removeUserFromNet(user, this.localUser.getUsername(), netName);

        //TODO: Cada vez que se añada un usuario ou que se meta nunha rede haina que añadir si o si
        this.remoteUsers.get(userIndex).removeNet(netName);
        if (remoteUsers.get(userIndex).getNets().isEmpty()){
          this.remoteServices.removeService(user);
          this.remoteUsers.remove(userIndex);
        }
      }
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

  public void addSeveralNets(List<String> nets){
    List<String> emptyNet = new ArrayList<>();
    emptyNet.add(this.localUser.getUsername());
    for (String net : nets){
      if (!this.nets.containsKey(net)){
        this.nets.put(net, emptyNet);
      }
    }
  }

  @Override
  public boolean addContent(String username, CustomTransferable content){
    boolean contentAdded = false;
    int userIndex = this.userAlreadyExists(username);
    if (userIndex >= 0){
      contentAdded = this.remoteUsers.get(userIndex).addContent(content);
    }
    return contentAdded;
  }
}
