package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.HashSet;


public class GenericProcessToECNumbersTable {

    HashMap<String, HashSet<String>> ContentTable;

    public GenericProcessToECNumbersTable() {
        ContentTable = new HashMap<String, HashSet<String>>();
    }

    public void insertRow(String GenericProcessId, String ECNumber) {
        HashSet<String> rowlist = this.ContentTable.get(GenericProcessId);
        if (rowlist == null) {
            rowlist = new HashSet<String>();
            this.ContentTable.put(GenericProcessId, rowlist);
        }
        rowlist.add(ECNumber);
    }

    public HashSet<String> getECNumberList(String GenericProcessId) {
        return this.ContentTable.get(GenericProcessId);
    }
}
