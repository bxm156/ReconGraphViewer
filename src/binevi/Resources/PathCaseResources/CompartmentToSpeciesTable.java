package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 4:26:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompartmentToSpeciesTable {
    class CompartmentToSpeciesTableEntry {
       // public String CompartmentId;
        public String SpeciesId;

        public CompartmentToSpeciesTableEntry(String SpeciesId) {
           // this.CompartmentId = CompartmentId;
            this.SpeciesId = SpeciesId;
        }
    }

    HashMap<String, ArrayList<CompartmentToSpeciesTableEntry>> ContentTable;

    public CompartmentToSpeciesTable() {
        ContentTable = new HashMap<String, ArrayList<CompartmentToSpeciesTableEntry>>();
    }

    public void insertRow(String CompartmentId, String SpeciesId) {
        ArrayList<CompartmentToSpeciesTableEntry> rowlist = this.ContentTable.get(CompartmentId);
        if (rowlist == null) {
            rowlist = new ArrayList<CompartmentToSpeciesTableEntry>();
            this.ContentTable.put(CompartmentId, rowlist);
        }
        
        CompartmentToSpeciesTableEntry newEntry = new CompartmentToSpeciesTableEntry(SpeciesId);

        int index = -1;
        if (rowlist.size() > 0) {
            index = rowlist.indexOf(newEntry);
        }
        if (index < 0)
            rowlist.add(new CompartmentToSpeciesTableEntry(SpeciesId));
    }

     public ArrayList<String> getSpeciesByComId(String CompartmentID) {
        ArrayList<CompartmentToSpeciesTableEntry> rowlist=this.ContentTable.get(CompartmentID);
        ArrayList<String> speciesidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (CompartmentToSpeciesTableEntry row : rowlist) {
            speciesidlist.add(row.SpeciesId);
        }
        return speciesidlist;
    }

    public String getCompIdBySpecId(String specID) {
//        ArrayList<CompartmentToSpeciesTableEntry> rowlist=this.ContentTable.get(specID);
//        CompartmentToSpeciesTableEntry spec= new CompartmentToSpeciesTableEntry(specID);
        for(String comId:ContentTable.keySet()){
            for(int i=0;i<this.ContentTable.get(comId).size();i++){
                if (this.ContentTable.get(comId).get(i).SpeciesId.equalsIgnoreCase(specID))return comId;
            }
        }
        return null;
    }
}