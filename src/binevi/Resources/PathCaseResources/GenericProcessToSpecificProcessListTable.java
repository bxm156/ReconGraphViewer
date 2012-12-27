package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class GenericProcessToSpecificProcessListTable {

    class GenericProcessToSpecificProcessListTableEntry {
        public String SpecificProcessId;
        public String OrganismGroupId;
        public String ECNumber;
        public String GeneProductMoleculeID;

        public GenericProcessToSpecificProcessListTableEntry(String SpecificProcessId, String OrganismGroupId, String ECNumber, String GeneProductMoleculeID) {
            this.SpecificProcessId = SpecificProcessId;
            this.OrganismGroupId = OrganismGroupId;
            this.ECNumber = ECNumber;
            this.GeneProductMoleculeID = GeneProductMoleculeID;
        }

        public boolean equals(Object other) {
            return other instanceof GenericProcessToSpecificProcessListTableEntry && ((GenericProcessToSpecificProcessListTableEntry) other).OrganismGroupId.equals(this.OrganismGroupId) && ((GenericProcessToSpecificProcessListTableEntry) other).SpecificProcessId.equals(this.SpecificProcessId) && ((GenericProcessToSpecificProcessListTableEntry) other).GeneProductMoleculeID.equals(this.GeneProductMoleculeID);
        }
    }

    HashMap<String, ArrayList<GenericProcessToSpecificProcessListTableEntry>> ContentTable;

    public GenericProcessToSpecificProcessListTable() {
        ContentTable = new HashMap<String, ArrayList<GenericProcessToSpecificProcessListTableEntry>>();
    }

    public void insertRow(String GenericProcessId, String SpecificProcessId, String OrganismGroupId, String ECNumber, String geneProductMoleculeID) {
        ArrayList<GenericProcessToSpecificProcessListTableEntry> rowlist = this.ContentTable.get(GenericProcessId);
        if (rowlist == null) {
            rowlist = new ArrayList<GenericProcessToSpecificProcessListTableEntry>();
            this.ContentTable.put(GenericProcessId, rowlist);
        }
        rowlist.add(new GenericProcessToSpecificProcessListTableEntry(SpecificProcessId, OrganismGroupId, ECNumber, geneProductMoleculeID));
    }

    public ArrayList<GenericProcessToSpecificProcessListTableEntry> getRows(String GenericProcessId) {
        return this.ContentTable.get(GenericProcessId);
    }

    public String getOrganismGroupId(String GenericProcessId, String SpecificProcessId) {
        ArrayList<GenericProcessToSpecificProcessListTableEntry> rowlist = this.ContentTable.get(GenericProcessId);
        if (rowlist == null) return null;
        for (GenericProcessToSpecificProcessListTableEntry row : rowlist) {
            if (row.SpecificProcessId.equals(SpecificProcessId)) {
                return row.OrganismGroupId;
            }
        }
        return null;
    }

    public String getSpecificProcessId(String GenericProcessId, String OrganismGroupId) {
        ArrayList<GenericProcessToSpecificProcessListTableEntry> rowlist = this.ContentTable.get(GenericProcessId);
        if (rowlist == null) return null;
        for (GenericProcessToSpecificProcessListTableEntry row : rowlist) {
            if (row.OrganismGroupId.equals(OrganismGroupId)) {
                return row.SpecificProcessId;
            }
        }
        return null;
    }

    public HashSet<String> getOrganismGroupIdList(String GenericProcessId) {
        ArrayList<GenericProcessToSpecificProcessListTableEntry> rowlist = this.ContentTable.get(GenericProcessId);
        HashSet<String> orgidlist = new HashSet<String>();
        if (rowlist == null){
            return  null;
        }
        for (GenericProcessToSpecificProcessListTableEntry row : rowlist) {
            orgidlist.add(row.OrganismGroupId);
        }
        return orgidlist;
    }

    public ArrayList<String> getSpecificProcessIdList(String GenericProcessId) {
        ArrayList<GenericProcessToSpecificProcessListTableEntry> rowlist = this.ContentTable.get(GenericProcessId);
        ArrayList<String> specificprocessidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (GenericProcessToSpecificProcessListTableEntry row : rowlist) {
            specificprocessidlist.add(row.SpecificProcessId);
        }
        return specificprocessidlist;
    }

}


