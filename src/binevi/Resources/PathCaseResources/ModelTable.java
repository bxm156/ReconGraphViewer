package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 16, 2009
 * Time: 9:59:27 AM
 */
public class ModelTable {

    public ArrayList<String> getModelIdList() {
       return new ArrayList<String>(ContentTable.keySet());
   }

    class ModelTableEntry{
        public String ModelID;
        public String ModelName;

        public ModelTableEntry(String ModelID, String ModelName) {
                this.ModelID = ModelID;
                this.ModelName = ModelName;
             }
    }

    HashMap<String, ModelTableEntry> ContentTable;

    public ModelTable() {
        ContentTable = new HashMap<String, ModelTableEntry>();
    }

    public void insertRow(String ModelID, String ModelName) {
        this.ContentTable.put(ModelID, new ModelTableEntry(ModelID, ModelName));
    }

    public ModelTableEntry getRow(String ModelID) {
        return this.ContentTable.get(ModelID);
    }

    public String getNameById() {
        return getNameById((String)this.ContentTable.keySet().toArray()[0]);

    }
    public String getNameById(String ModelID) {
        ModelTableEntry entry = getRow(ModelID);
        if (entry != null)
            return entry.ModelName;
        else{
            return "NULL";
        }
    }
    
}
