package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;


public class PathwaysRelationTable {

    class PathwaysRelationTableEntry {
        private String pathwayIDFirst;
        private String pathwayIDSecond;
        private ArrayList<String> metabolitePools= new ArrayList<String>();

        public PathwaysRelationTableEntry(String pathway1,String pathway2,ArrayList<String> metaPools) {
            this.pathwayIDFirst = pathway1;
            this.pathwayIDSecond = pathway2;
            this.metabolitePools = metaPools;
        }
    }

    HashMap<String, PathwaysRelationTableEntry> ContentTable;

    public PathwaysRelationTable() {
        ContentTable = new HashMap<String, PathwaysRelationTableEntry>();
    }

    public void insertRow(String pwRelationID, String pathway1,String pathway2,ArrayList<String> metaPools) {
        this.ContentTable.put(pwRelationID, new PathwaysRelationTableEntry(pathway1, pathway2, metaPools));
    }

    public PathwaysRelationTableEntry getRow(String PathwayRelationID) {
        return this.ContentTable.get(PathwayRelationID);
    }

    public String getFirstpwById(String pwRID) {
        return this.ContentTable.get(pwRID).pathwayIDFirst;
    }

    public String getSecondpwById(String pwRID) {
        return this.ContentTable.get(pwRID).pathwayIDSecond;
    }

    public ArrayList<String> getMetabolitePoolsById(String pwRID) {
        return this.ContentTable.get(pwRID).metabolitePools;
    }

}