package es.uvigo.tfg.remoteClipboard.net;

import es.uvigo.tfg.remoteClipboard.services.ClipboardManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "networks")
public class NetworksManager {
    @XmlElement
    private List<Network> networks;
    @XmlTransient
    private File networkConfig;
    @XmlTransient
    private ClipboardManager clipboardManager;

    public NetworksManager(ClipboardManager cbManager) {
        clipboardManager = cbManager;
        networks = new ArrayList<>();
        networkConfig = new File("./application/src/main/java/org/tfg/net/resources/netConfiguration.xml");
        if (!networkConfig.exists()) {
            try {
                networkConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("NETWORKMANAGER: Error creating file");
            }
        } else {
            getAllFromExistingConfiguration();
        }
    }

    public NetworksManager(File netConfiguration) {
        networkConfig = netConfiguration;
    }

    public void addNetwork(Network newNetwork) {
        networks.add(newNetwork);
    }

    public void removeNetwork(Network toDelete){
        if(networks.contains(toDelete)){
            networks.remove(toDelete);
        }
    }

    public void updateNetwork(Network updatedNetwork){
        String name = updatedNetwork.getName();
        Long id = updatedNetwork.getId();
        for(int i = 0; i < networks.size(); i++){
            if(networks.get(i).getName().equals(name) && networks.get(i).getId().equals(id)){
                networks.set(i, updatedNetwork);
                break;
            }
        }
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void merge() {
        try {
            JAXBContext context = JAXBContext.newInstance(this.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, networkConfig);
        } catch (JAXBException e) {
            System.err.println("NETWORKMANAGER: Error while mergin or creating xml");
            e.printStackTrace();
        }
    }



    public void getAllFromExistingConfiguration() {
        try {
            JAXBContext context = JAXBContext.newInstance(this.getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();

            NetworksManager unMerged = (NetworksManager) unmarshaller.unmarshal(networkConfig);
            this.networks.clear();
            this.networks.addAll(unMerged.networks);

        } catch (JAXBException e) {
            System.err.println("NETWORKSMANAGER: Error getting the configuration");
        }
    }
}
