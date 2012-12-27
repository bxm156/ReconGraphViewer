package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessToMoleculesTable {

    class ProcessToMoleculesTableEntry {
        public String MoleculeId;
        public String MoleculeRole;

        public ProcessToMoleculesTableEntry(String MoleculeId, String MoleculeRole) {
            this.MoleculeId = MoleculeId;
            this.MoleculeRole = MoleculeRole;
        }

        public boolean equals(Object other) {
            return other instanceof ProcessToMoleculesTableEntry && ((ProcessToMoleculesTableEntry) other).MoleculeId.equals(this.MoleculeId) && ((ProcessToMoleculesTableEntry) other).MoleculeRole.equals(this.MoleculeRole);
        }
    }

    HashMap<String, ArrayList<ProcessToMoleculesTableEntry>> ContentTable;

    public ProcessToMoleculesTable() {
        ContentTable = new HashMap<String, ArrayList<ProcessToMoleculesTableEntry>>();
    }

    public void insertRow(String ProcessId, String MoleculeId, String MoleculeRole) {
        ArrayList<ProcessToMoleculesTableEntry> rowlist = this.ContentTable.get(ProcessId);
        if (rowlist == null) {
            rowlist = new ArrayList<ProcessToMoleculesTableEntry>();
            this.ContentTable.put(ProcessId, rowlist);
        }

        ProcessToMoleculesTableEntry newEntry = new ProcessToMoleculesTableEntry(MoleculeId, MoleculeRole);

        int index = -1;
        if (rowlist.size() > 0) {
            index = rowlist.indexOf(newEntry);
        }
        if (index < 0)
            rowlist.add(new ProcessToMoleculesTableEntry(MoleculeId, MoleculeRole));

    }

    public ArrayList<ProcessToMoleculesTableEntry> getRows(String ProcessId) {
        return this.ContentTable.get(ProcessId);
    }

    public String getMoleculeRole(String ProcessId, String MoleculeId) {
        ArrayList<ProcessToMoleculesTableEntry> rowlist = this.ContentTable.get(ProcessId);
        if (rowlist == null) return null;
        for (ProcessToMoleculesTableEntry row : rowlist) {
            if (row.MoleculeId.equals(MoleculeId)) {
                return row.MoleculeRole;
            }
        }
        return null;
    }

    public String getMoleculeId(String ProcessId, String MoleculeRole) {
        ArrayList<ProcessToMoleculesTableEntry> rowlist = this.ContentTable.get(ProcessId);
        if (rowlist == null) return null;
        for (ProcessToMoleculesTableEntry row : rowlist) {
            if (row.MoleculeRole.equals(MoleculeRole)) {
                return row.MoleculeId;
            }
        }
        return null;
    }

    public ArrayList<String> getMoleculeRoleList(String ProcessId) {
        ArrayList<ProcessToMoleculesTableEntry> rowlist = this.ContentTable.get(ProcessId);
        ArrayList<String> rolelist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (ProcessToMoleculesTableEntry row : rowlist) {
            rolelist.add(row.MoleculeRole);
        }
        return rolelist;
    }

    public ArrayList<String> getMoleculeIdList(String ProcessId) {
        ArrayList<ProcessToMoleculesTableEntry> rowlist = this.ContentTable.get(ProcessId);
        ArrayList<String> moleculeidlist = new ArrayList<String>();
        if (rowlist == null) return null;
        for (ProcessToMoleculesTableEntry row : rowlist) {
            moleculeidlist.add(row.MoleculeId);
        }
        return moleculeidlist;
    }


}
