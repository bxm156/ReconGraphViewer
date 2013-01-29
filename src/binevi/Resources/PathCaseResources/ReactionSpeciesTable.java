package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 5:11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReactionSpeciesTable {
    
    public ArrayList<String> getReactionSpeciesIdList() {
       return new ArrayList<String>(ContentTable.keySet());
   }

    class ReactionSpeciesTableEntry{
       public String ReactionSpeciesID;
       public String ReactionSpeciesName;
       public String SpeciesId;
       public String RoleId;
       public String Stoichiometry;
       public ReactionSpeciesTableEntry(String ReactionSpeciesID, String ReactionSpeciesName, String SpeciesId, String RoleId,String Stoichiometry) {
            this.ReactionSpeciesID = ReactionSpeciesID;
            this.ReactionSpeciesName = ReactionSpeciesName;
            this.SpeciesId = SpeciesId;
            this.RoleId = RoleId;
            this.Stoichiometry = Stoichiometry;
       }
    }

    HashMap<String, ReactionSpeciesTableEntry> ContentTable;

    public ReactionSpeciesTable() {
        ContentTable = new HashMap<String, ReactionSpeciesTableEntry>();
    }

    public void insertRow(String ReactionSpeciesID, String ReactionSpeciesName, String SpeciesId, String RoleId,String Stoichiometry) {
        this.ContentTable.put(ReactionSpeciesID, new ReactionSpeciesTableEntry(ReactionSpeciesID, ReactionSpeciesName, SpeciesId, RoleId,Stoichiometry));
    }
    
    public synchronized void deleteRow(String reactionSpeciesId) {
    	this.ContentTable.remove(reactionSpeciesId);
    }

    public ReactionSpeciesTableEntry getRow(String ReactionSpeciesID) {
        return this.ContentTable.get(ReactionSpeciesID);
    }

    public String getNameById(String ReactionSpeciesID) {
        ReactionSpeciesTableEntry entry = getRow(ReactionSpeciesID);
        if (entry != null)
            return entry.ReactionSpeciesName;
        else{
            return "NULL";
        }
    }

//    public String getRolenameById(String reacid,String  ReactionSpeciesID) {
//        ReactionSpeciesTableEntry entry = getRow(ReactionSpeciesID);
//        if (entry != null)
//            return entry.RoleId;
//        else{
//            return "NULL";
//        }
//    }

   public ArrayList<String> getSpeciesByReacSpecIds(ArrayList<String> reacspecIDs) {
       if(reacspecIDs==null) return null;
       ArrayList<String> speciesidlist = new ArrayList<String>();
       for (String row : reacspecIDs) {
           speciesidlist.add(getRow(row).SpeciesId);
       }
       return speciesidlist;
    }

    public String getSpeciesRoleBySpecId(ArrayList<String> reacspecIDs,String specid) {
        for (String row : reacspecIDs) {
            ReactionSpeciesTableEntry entry = getRow(row);
            if(entry.SpeciesId.equalsIgnoreCase(specid))return entry.RoleId;
        }
        return "NULL";
     }
}
