package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;


public class PathwayTable {

    class PathwayTableEntry {
        public String PathwayName;
        public boolean isExpanded;
        public boolean isLinking;

        public PathwayTableEntry(String PathwayName, boolean isExpanded, boolean isLinking) {
            this.PathwayName = PathwayName;
            this.isExpanded = isExpanded;
            this.isLinking = isLinking;
        }

        public boolean equals(Object other) {
            return other instanceof PathwayTableEntry && ((PathwayTableEntry) other).isExpanded == isExpanded && ((PathwayTableEntry) other).PathwayName.equals(this.PathwayName);
        }
    }

    HashMap<String, PathwayTableEntry> ContentTable;

    public PathwayTable() {
        ContentTable = new HashMap<String, PathwayTableEntry>();
    }

    public void insertRow(String pathwayID, String PathwayName, boolean isexpanded, boolean islinking) {
        this.ContentTable.put(pathwayID, new PathwayTableEntry(PathwayName, isexpanded, islinking));
    }

    public PathwayTableEntry getRow(String PathwayID) {
        return this.ContentTable.get(PathwayID);
    }

    public String getNameById(String PathwayID) {
        return this.ContentTable.get(PathwayID).PathwayName;
    }
        
    public boolean getExpandedById(String PathwayID) {
        return this.ContentTable.get(PathwayID).isExpanded;
    }

    public HashSet<String> getCollapsedPathways(boolean linkingIncluded) {

        HashSet<String> collapsedPathways = new HashSet<String>();
        for(String pwid:ContentTable.keySet())
        {
            PathwayTableEntry entry = ContentTable.get(pwid);
            if ((!entry.isExpanded && !entry.isLinking) || (!entry.isExpanded && linkingIncluded && entry.isLinking))
            {
                collapsedPathways.add(pwid);
            }
        }

        return collapsedPathways;
    }
                    
    
     public ArrayList<String> getAllPathways() {
        return new ArrayList<String>(ContentTable.keySet());
    }

    public HashSet<String> getLinkingPathways() {

        HashSet<String> linkingPathways = new HashSet<String>();
        for(String pwid:ContentTable.keySet())
        {
            PathwayTableEntry entry = ContentTable.get(pwid);
            if (entry.isLinking)
            {
                linkingPathways.add(pwid);
            }
        }

        return linkingPathways;
    }


}
