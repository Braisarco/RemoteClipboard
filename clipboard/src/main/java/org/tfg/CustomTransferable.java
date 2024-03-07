package org.tfg;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

public class CustomTransferable implements Serializable, Transferable{
    private DataFlavor[] avaliableDataFlavours;
    private Object contentRepresentation;

    public CustomTransferable(Transferable transferable) throws UnsupportedFlavorException, IOException{
        this.avaliableDataFlavours = transferable.getTransferDataFlavors();
        this.contentRepresentation = getContent(transferable);
    }

    private Object getContent(Transferable transferable) throws IOException, UnsupportedFlavorException {
        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)){
            return transferable.getTransferData(DataFlavor.imageFlavor);
        } else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return transferable.getTransferData(DataFlavor.javaFileListFlavor);
        } else{
            return transferable.getTransferData(DataFlavor.stringFlavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return this.avaliableDataFlavours;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean isContainded = false;
        for(DataFlavor dataflavor: this.avaliableDataFlavours){
            if (dataflavor.equals(flavor)){
                isContainded = true;
                break;
            }
        }
        return isContainded;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.contentRepresentation;
    }

    public String getStringDataFlavours(){

        StringBuilder toret = new StringBuilder();
        for (DataFlavor dataFlavor : this.avaliableDataFlavours) {
            toret.append(dataFlavor.toString() + "\n");
        }
        return toret.toString();
    }
}

