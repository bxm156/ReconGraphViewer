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
public class ReactionToEnzymeTable {
    class ReactionToEnzymeTableEntry {
       // public String CompartmentId;
        public String SpeciesId;

        public ReactionToEnzymeTableEntry(String SpeciesId) {
           // this.CompartmentId = CompartmentId;
            this.SpeciesId = SpeciesId;
        }
    }

    HashMap<String, ArrayList<ReactionToEnzymeTableEntry>> ContentTable;

    public ReactionToEnzymeTable() {
        ContentTable = new HashMap<String, ArrayList<ReactionToEnzymeTableEntry>>();
    }

    public void insertRow(String reactionId, String enzymeId) {
        ArrayList<ReactionToEnzymeTableEntry> rowlist = this.ContentTable.get(reactionId);
        if (rowlist == null) {
            rowlist = new ArrayList<ReactionToEnzymeTableEntry>();
            this.ContentTable.put(reactionId, rowlist);
        }
        
        ReactionToEnzymeTableEntry newEntry = new ReactionToEnzymeTableEntry(enzymeId);

        int index = -1;
        if (rowlist.size() > 0) {
            index = rowlist.indexOf(newEntry);
        }
        if (index < 0)
            rowlist.add(new ReactionToEnzymeTableEntry(enzymeId));
    }

     public ArrayList<String> getSpeciesByComId(String CompartmentID) {
        ArrayList<ReactionToEnzymeTableEntry> rowlist=this.ContentTable.get(CompartmentID);
        ArrayList<String> speciesidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (ReactionToEnzymeTableEntry row : rowlist) {
            speciesidlist.add(row.SpeciesId);
        }
        return speciesidlist;
    }

    public String getCompIdBySpecId(String specID) {
//        ArrayList<ReactionToEnzymeTableEntry> rowlist=this.ContentTable.get(specID);
//        ReactionToEnzymeTableEntry spec= new ReactionToEnzymeTableEntry(specID);
        for(String comId:ContentTable.keySet()){
            for(int i=0;i<this.ContentTable.get(comId).size();i++){
                if (this.ContentTable.get(comId).get(i).SpeciesId.equalsIgnoreCase(specID))return comId;
            }
        }
        return null;
    }
}