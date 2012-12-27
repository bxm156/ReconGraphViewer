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
public class CompartmentToReactionsTable {
    class CompartmentToReactionsTableEntry {
        public String ReactionId;
        ArrayList<String> ReactionEnzyme;

        public CompartmentToReactionsTableEntry(String SpeciesId,ArrayList<String> reactionEnzyme) {
            this.ReactionId = SpeciesId;
            this.ReactionEnzyme=reactionEnzyme;
        }
    }

    HashMap<String, ArrayList<CompartmentToReactionsTableEntry>> ContentTable;

    public CompartmentToReactionsTable() {
        ContentTable = new HashMap<String, ArrayList<CompartmentToReactionsTableEntry>>();
    }

    public void insertRow(String CompartmentId, String reacId,ArrayList<String> reactionEnzyme) {
        ArrayList<CompartmentToReactionsTableEntry> rowlist = this.ContentTable.get(CompartmentId);
        if (rowlist == null) {
            rowlist = new ArrayList<CompartmentToReactionsTableEntry>();
            this.ContentTable.put(CompartmentId, rowlist);
        }

        CompartmentToReactionsTableEntry newEntry = new CompartmentToReactionsTableEntry(reacId,reactionEnzyme);

        int index = -1;
        if (rowlist.size() > 0) {
            index = rowlist.indexOf(newEntry);
        }
        if (index < 0)
            rowlist.add(new CompartmentToReactionsTableEntry(reacId,reactionEnzyme));
    }                                                   
                                     
    public  ArrayList<String> getReactionEnzyme(String compId,String reacId){
        ArrayList<CompartmentToReactionsTableEntry> rowlist = this.ContentTable.get(compId);
                if (rowlist == null) {
                    return null;
                }
        for(CompartmentToReactionsTableEntry row: rowlist){
            if(row.ReactionId.equalsIgnoreCase(reacId)) return row.ReactionEnzyme;
            return null;
        }
        return null;
    }

     public ArrayList<String> getReactionsByComId(String CompartmentID) {
        ArrayList<CompartmentToReactionsTableEntry> rowlist=this.ContentTable.get(CompartmentID);
        ArrayList<String> speciesidlist = new ArrayList<String>();
        if (rowlist == null) return null;         
        for (CompartmentToReactionsTableEntry row : rowlist) {
            speciesidlist.add(row.ReactionId);
        }
        return speciesidlist;
    }

    public String getCompIdBySpecId(String specID) {
//        ArrayList<CompartmentToSpeciesTableEntry> rowlist=this.ContentTable.get(specID);
//        CompartmentToSpeciesTableEntry spec= new CompartmentToSpeciesTableEntry(specID);
        for(String comId:ContentTable.keySet()){
            for(int i=0;i<this.ContentTable.get(comId).size();i++){
                if (this.ContentTable.get(comId).get(i).ReactionId.equalsIgnoreCase(specID))return comId;
            }
        }
        return null;
    }
}