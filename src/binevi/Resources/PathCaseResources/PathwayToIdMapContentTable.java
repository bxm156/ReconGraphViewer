package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.HashSet;

public class PathwayToIdMapContentTable {

    HashMap<String, HashMap<String,HashSet<String>>> ContentTable;

    public PathwayToIdMapContentTable() {
        ContentTable = new HashMap<String, HashMap<String,HashSet<String>>>();
    }

    public void insertRow(String fromPathwayId, String toPathwayId, String moleculeid, boolean incominglink) {
        if (incominglink)
        {
            String temp = toPathwayId;
            toPathwayId = fromPathwayId;
            fromPathwayId = temp;
        }

        HashMap<String,HashSet<String>> pathwaylist = this.ContentTable.get(fromPathwayId);
        if (pathwaylist == null) {
            pathwaylist = new HashMap<String,HashSet<String>>();
            this.ContentTable.put(fromPathwayId, pathwaylist);
        }

        HashSet<String> moleculelist = pathwaylist.get(toPathwayId);
        if (moleculelist == null) {
            moleculelist = new HashSet<String>();
            pathwaylist.put(toPathwayId, moleculelist);
        }

        moleculelist.add(moleculeid);
    }

    public HashMap<String,HashSet<String>> getItemIdList(String PathwayId) {
        return this.ContentTable.get(PathwayId);
    }
}
