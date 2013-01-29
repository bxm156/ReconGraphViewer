package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 5:53:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReactionToReactionSpeciesTable {
    class ReactionToReactionSpeciesTableEntry {
            public String ReactionSpeciesId;
                    String metaPoolRole;

            public ReactionToReactionSpeciesTableEntry(String ReactionSpeciesId) {               
                this.ReactionSpeciesId = ReactionSpeciesId;
                this.metaPoolRole="";
            }
        
            public ReactionToReactionSpeciesTableEntry(String ReactionSpeciesId,String metapoolrole) {
                this.ReactionSpeciesId = ReactionSpeciesId;
                this.metaPoolRole=metapoolrole;
            }
        }

        HashMap<String, ArrayList<ReactionToReactionSpeciesTableEntry>> ContentTable;

        public ReactionToReactionSpeciesTable() {
            ContentTable = new HashMap<String, ArrayList<ReactionToReactionSpeciesTableEntry>>();
        }

        public void insertRow(String ReactionId, String ReactionSpeciesId) {
            ArrayList<ReactionToReactionSpeciesTableEntry> rowlist = this.ContentTable.get(ReactionId);
            if (rowlist == null) {
                rowlist = new ArrayList<ReactionToReactionSpeciesTableEntry>();
                this.ContentTable.put(ReactionId, rowlist);
            }

            ReactionToReactionSpeciesTableEntry newEntry = new ReactionToReactionSpeciesTableEntry(ReactionSpeciesId);

            int index = -1;
            if (rowlist.size() > 0) {
                index = rowlist.indexOf(newEntry);
            }
            if (index < 0)
                rowlist.add(new ReactionToReactionSpeciesTableEntry(ReactionSpeciesId));
        }
        
        public synchronized void deleteReactionSpecies(String reacionSpeciesId) {
        	Iterator<Entry<String, ArrayList<ReactionToReactionSpeciesTableEntry>>> it = ContentTable.entrySet().iterator();
        	while(it.hasNext()) {
        		Entry<String, ArrayList<ReactionToReactionSpeciesTableEntry>> pairs = it.next();
        		ArrayList<ReactionToReactionSpeciesTableEntry> rowlist = pairs.getValue();
        		Iterator<ReactionToReactionSpeciesTableEntry> it2= rowlist.iterator();
        		while (it2.hasNext()) {
        			ReactionToReactionSpeciesTableEntry e = it2.next();
        			if(e.ReactionSpeciesId.equalsIgnoreCase(reacionSpeciesId)) {
        				it2.remove();
        			}
        		}
        		if(rowlist.size() == 0) {
        			it.remove();
        		}
        	}
        }

      public ArrayList<String> getSpeciesByReacId(String ReacID) {
        ArrayList<ReactionToReactionSpeciesTableEntry> rowlist=this.ContentTable.get(ReacID);
        ArrayList<String> reactionspeciesidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (ReactionToReactionSpeciesTableEntry row : rowlist) {
            reactionspeciesidlist.add(row.ReactionSpeciesId);
        }
        return reactionspeciesidlist;
    }

    public String getSpeciesRole(String ReacID,String specID) {
        ArrayList<ReactionToReactionSpeciesTableEntry> rowlist=this.ContentTable.get(ReacID);
        ArrayList<String> reactionspeciesidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (ReactionToReactionSpeciesTableEntry row : rowlist) {
            if(row.ReactionSpeciesId.equalsIgnoreCase(specID)) return row.metaPoolRole;
        }
        return null;
    }
}

