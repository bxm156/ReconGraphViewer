package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;

public class PathwayToGenericProcessesTable {

    HashMap<String, ArrayList<String>> ContentTable;

    public PathwayToGenericProcessesTable() {
        ContentTable = new HashMap<String, ArrayList<String>>();
    }

    public void insertRow(String PathwayId, String GenericProcessId) {
        ArrayList<String> rowlist = this.ContentTable.get(PathwayId);
        if (rowlist == null) {
            rowlist = new ArrayList<String>();
            this.ContentTable.put(PathwayId, rowlist);
        }
        rowlist.add(GenericProcessId);
    }

    public ArrayList<String> getGenericProcessIdList(String PathwayId) {
        return this.ContentTable.get(PathwayId);
    }
}
