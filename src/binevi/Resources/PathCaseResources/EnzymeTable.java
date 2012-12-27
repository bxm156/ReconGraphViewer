package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 1:30:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnzymeTable {

    public ArrayList<String> getSpeciesIdList() {
       return new ArrayList<String>(ContentTable.keySet());
   }

    class SpeciesTableEntry{
            public String EnzymeID;
            public String EnzymeName;

            public SpeciesTableEntry(String SpeciesID, String SpeciesName) {
                    this.EnzymeID = SpeciesID;
                    this.EnzymeName = SpeciesName;
                 }
    }

      HashMap<String, SpeciesTableEntry> ContentTable;

    public EnzymeTable() {
        ContentTable = new HashMap<String, SpeciesTableEntry>();
    }                           

    public void insertRow(String SpeciesID, String SpeciesName) {
        this.ContentTable.put(SpeciesID, new SpeciesTableEntry(SpeciesID, SpeciesName));
    }

    public SpeciesTableEntry getRow(String SpeciesID) {
        return this.ContentTable.get(SpeciesID);
    }

    public String getNameById(String SpeciesID) {
        SpeciesTableEntry entry = getRow(SpeciesID);
        if (entry != null){
            if(!entry.EnzymeName.equalsIgnoreCase("")) return entry.EnzymeName;
            else return entry.EnzymeID;
        }
        else{
            return "NULL";
        }
    }
}