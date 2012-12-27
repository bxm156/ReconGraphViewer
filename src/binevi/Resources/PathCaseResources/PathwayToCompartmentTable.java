package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 4:26:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PathwayToCompartmentTable {
    class PathwayToCompartmentTableEntry {
        public String CompartmentId;

        public PathwayToCompartmentTableEntry(String SpeciesId) {
            this.CompartmentId = SpeciesId;
        }
    }

    HashMap<String, ArrayList<PathwayToCompartmentTableEntry>> ContentTable;

    public PathwayToCompartmentTable() {
        ContentTable = new HashMap<String, ArrayList<PathwayToCompartmentTableEntry>>();
    }

    public void insertRow(String PathwayId, String CompartmentId) {
        ArrayList<PathwayToCompartmentTableEntry> rowlist = this.ContentTable.get(PathwayId);
        if (rowlist == null) {
            rowlist = new ArrayList<PathwayToCompartmentTableEntry>();
            this.ContentTable.put(PathwayId, rowlist);
        }

        PathwayToCompartmentTableEntry newEntry = new PathwayToCompartmentTableEntry(CompartmentId);

        int index = -1;
        if (rowlist.size() > 0) {
            index = rowlist.indexOf(newEntry);
        }
        if (index < 0)
            rowlist.add(new PathwayToCompartmentTableEntry(CompartmentId));
    }
                                                         
     public ArrayList<String> getCompartmentByPathwayId(String PathwayID) {
        ArrayList<PathwayToCompartmentTableEntry> rowlist=this.ContentTable.get(PathwayID);
        ArrayList<String> CompartmentIdlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (PathwayToCompartmentTableEntry row : rowlist) {
            CompartmentIdlist.add(row.CompartmentId);
        }
        return CompartmentIdlist;
    }

    public String getPathwayIdByCompId(String specID) {
//        ArrayList<PathwayToCompartmentTableEntry> rowlist=this.ContentTable.get(specID);
//        PathwayToCompartmentTableEntry spec= new PathwayToCompartmentTableEntry(specID);
        for(String comId:ContentTable.keySet()){
            for(int i=0;i<this.ContentTable.get(comId).size();i++){
                if (this.ContentTable.get(comId).get(i).CompartmentId.equalsIgnoreCase(specID))return comId;
            }
        }
        return null;
    }
}