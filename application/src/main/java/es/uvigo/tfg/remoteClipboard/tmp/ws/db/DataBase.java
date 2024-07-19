package es.uvigo.tfg.remoteClipboard.tmp.ws.db;

import es.uvigo.tfg.remoteClipboard.tmp.ws.resources.User;
import es.uvigo.tfg.remoteClipboard.tmp.ws.service.RemoteClipboardSEI;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
    private User localUser;
    private List<User> remoteUsers;
    private Map<String, List<String>> nets;
    private List<RemoteClipboardSEI> remoteServices;

    public DataBase(String userName, String wsdl){
        this.localUser = new User(userName, wsdl);
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

    public List<User> getNetUsers(List<String> netNames){
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

    public boolean addUser(String username, String wsdl, List<String> nets){
        User auxiliarUser = new User(username, wsdl);
        boolean userAdded = false;
        //existe ese usuario?
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

    public boolean addContent(String username, Transferable content){
        boolean contentAdded = false;
        int userIndex = this.userAlreadyExists(username);
        if (userIndex != -1){
            contentAdded = this.remoteUsers.get(userIndex).addContent(content);
        }
        return contentAdded;
    }
}
