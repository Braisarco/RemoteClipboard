package es.uvigo.tfg.remoteClipboard.services;

import es.uvigo.tfg.remoteClipboard.client.ClientThreadPool;
import es.uvigo.tfg.remoteClipboard.net.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@XmlRootElement(name = "networks")
public class AppManager {
    private final User localUser;
    private List<User> remoteUsers;
    @XmlElement
    private Map<String, List<String>> nets;
    @XmlTransient
    private final String configurationFile = "./application/src/main/java/org/tfg/net/resources/netConfiguration.xml";
    private ClientThreadPool clientPool;

    public AppManager(String userName, ClientThreadPool clientTP) {
        try {
            this.localUser = new User(InetAddress.getLocalHost().getHostAddress(),userName);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.clientPool = clientTP;
        this.clientPool.start();
        this.remoteUsers = new ArrayList<>();
        this.nets = new HashMap<>();
    }

    public String getLocalUserName(){
        return this.localUser.getUsername();
    }

    public void setLocalUserName(String name){
        this.localUser.setUsername(name);
    }

    public void connectToNewNet(String ip, String netName){
        this.clientPool.executeClient(ip, netName, this);
    }

    public boolean addRemoteUser(String ip, String username, String netName) {
        boolean done = false;
        User auxiliarUser = new User(ip,username);

        if(!existUser(auxiliarUser)){
            remoteUsers.add(auxiliarUser);
        }else if (this.addUserToNet(ip , netName)){
            done = true;
            this.createClientCommunication(ip, netName);
        }
        return done;
    }

    private void createClientCommunication(String ip, String netName){
        this.clientPool.executeClient(ip, netName, this);
    }

    private boolean addUserToNet(String netName, String userName) {
        if (nets.containsKey(netName)) {
            if (!nets.get(netName).contains(userName)) {
                nets.get(netName).add(userName);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean existUser(User newUser){
        if (localUser.getUsername().equals(newUser.getUsername())){
            return true;
        }else{
            for (User user : remoteUsers){
                if (user.getUsername().equals(newUser.getUsername())){
                    return true;
                } else if (user.getIp().equals(user.getIp())) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deleteRemoteUser(String name) {
        if (remoteUsers.contains(name)) {
            remoteUsers.remove(name);
            return true;
        } else {
            System.err.println("MANAGER: The user does not exist so it can't be removed");
            return false;
        }
    }

    public boolean addContent(String ip, Transferable clipboardContent) {
        for (User user : remoteUsers) {
            if (user.getIp().equals(ip)) {
                user.addContent(clipboardContent);
                return true;
            }
        }
        return false;
    }

    public void addLocalContent(Transferable clipboardContent){
        this.localUser.addContent(clipboardContent);
    }

    public List<String> getUserContent(String userName) {
        if (localUser.getUsername().equals(userName)){
            return localUser.getClipboardStringContent();
        }else{
            for (User user : remoteUsers) {
                if (user.getUsername().equals(userName)) {
                    return user.getClipboardStringContent();
                }
            }
        }
        return null;
    }

    public boolean createNetwork(String netName) {
        if (nets.containsKey(netName)) {
            System.err.println("MANAGER: The net name already exists");
            return false;
        } else {
            nets.put(netName, new ArrayList<>());
            nets.get(netName).add(localUser.getUsername());
            return true;
        }
    }

    public boolean removeNetwork(String netName) {
        if (nets.containsKey(netName)) {
            nets.remove(netName);
            return true;
        } else {
            System.err.println("MANAGER: The net does not exist so it can't be removed");
            return false;
        }
    }

    public List<String> getAvaliableNets(){
        List<String> result = new ArrayList<>();

        if(nets.size() != 0){
            for (String net : nets.keySet()){
                result.add(net);
            }
        }else{
            result.add("No net avaliable");
        }

        return result;
    }

    public List<String> getNetUsersNames(String netName) {
        if (nets.containsKey(netName)) {
            return nets.get(netName);
        } else {
            return null;
        }
    }

    public List<User> getNetUsers(String netName){
        List<User> result = new ArrayList<>(remoteUsers);
        result.add(localUser);
        return result;
    }

    public boolean existsNet(String netName){
        return nets.containsKey(netName);
    }

    public boolean removeUserFromNet(String netName, String userName) {
        if (nets.containsKey(netName) && nets.get(netName).contains(userName)) {
            nets.get(netName).remove(userName);
            //Aqui qudaría eliminar o usuario en caso de que non pertenezca a ningunha outra rede
            return true;
        }
        return false;
    }

    public void save() {
        try {
            JAXBContext context = JAXBContext.newInstance(this.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(configurationFile));
        } catch (JAXBException e) {
            System.err.println("NETWORKMANAGER: Error while mergin or creating xml");
            e.printStackTrace();
        }
    }
}
