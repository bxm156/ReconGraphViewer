package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PathwayToIdContentTable {

    HashMap<String, HashSet<String>> ContentTable;

    public PathwayToIdContentTable() {
        ContentTable = new HashMap<String, HashSet<String>>();
    }

    public void insertRow(String PathwayId, String id) {
        HashSet<String> rowlist = this.ContentTable.get(PathwayId);
        if (rowlist == null) {
            rowlist = new HashSet<String>();
            this.ContentTable.put(PathwayId, rowlist);
        }
        rowlist.add(id);
    }

    public HashSet<String> getItemIdList(String PathwayId) {
        return this.ContentTable.get(PathwayId);
    }
}
