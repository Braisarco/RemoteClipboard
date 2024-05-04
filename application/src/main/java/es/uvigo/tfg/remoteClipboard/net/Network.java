package es.uvigo.tfg.remoteClipboard.net;

import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@XmlRootElement(name = "network")
public class Network {
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "id")
    private Long id;
    @XmlElement
    private List<User> users;
    @XmlTransient
    private ClipboardManager clipboardManager;

    public Network(Network newNetwork, ClipboardManager cbManager) {
        Random idGenerator = new Random();
        id = idGenerator.nextLong();
        name = newNetwork.getName();
        users = newNetwork.getUsers();
        clipboardManager = cbManager;
    }

    public Network() {

    }

    public Network(String newName, ClipboardManager cbManager) {
        Random idGenerator = new Random();
        id = idGenerator.nextLong();
        name = newName;
        users = new ArrayList<>();
        clipboardManager = cbManager;
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void updateNetwork() {
        for (User usr : users) {
            usr.setClipboardContent(clipboardManager.getRemoteUserStringContent(usr));
        }
    }


    //A method to return all the names of the users of a certain network
    //May contain an empty list
    public List<String> getUsersNames() {
        List<String> result = new ArrayList<>();
        for (User usr : users) {
            result.add(usr.getUsername());
        }
        return result;
    }

    //Returns true if the network contains the specified user
    public boolean containsUser(User usr) {
        for (User usrIterator : users) {
            if (usrIterator.equals(usr)) {
                return true;
            }
        }
        return false;
    }

    public void addContent(User usr, String content) {
        for (User usrIterator : users) {
            if (usrIterator.equals(usr)) {
                usrIterator.addContent(content);
                break;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Network && ((Network) obj).id == this.id && ((Network) obj).name == this.name) {
            return true;
        } else {
            return false;
        }
    }
}
