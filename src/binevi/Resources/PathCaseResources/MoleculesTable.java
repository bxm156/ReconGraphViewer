package binevi.Resources.PathCaseResources;

import java.util.HashMap;


public class MoleculesTable {

    class MoleculesTableEntry {
        public String MoleculeName;
        public boolean isMoleculeCommon;
        public String MoleculeEntityID;
        public String Tissue;

        public MoleculesTableEntry(String MoleculeEntityID, String MoleculeName, boolean isMoleculeCommon, String Tissue) {
            this.MoleculeName = MoleculeName;
            this.isMoleculeCommon = isMoleculeCommon;
            this.MoleculeEntityID = MoleculeEntityID;
            this.Tissue=Tissue;
        }

        public MoleculesTableEntry(String MoleculeEntityID, String MoleculeName, boolean isMoleculeCommon) {
            this.MoleculeName = MoleculeName;
            this.isMoleculeCommon = isMoleculeCommon;
            this.MoleculeEntityID = MoleculeEntityID;
            this.Tissue="";
        }


        public boolean equals(Object other) {
            return other instanceof MoleculesTableEntry && ((MoleculesTableEntry) other).isMoleculeCommon == isMoleculeCommon && ((MoleculesTableEntry) other).MoleculeName.equals(this.MoleculeName) && ((MoleculesTableEntry) other).MoleculeEntityID.equals(this.MoleculeEntityID);
        }
    }

    HashMap<String, MoleculesTableEntry> ContentTable;

    public MoleculesTable() {
        ContentTable = new HashMap<String, MoleculesTableEntry>();
    }

    public void insertRow(String MoleculeID, String MoleculeEntityID, String MoleculeName, boolean isMoleculeCommon) {
        this.ContentTable.put(MoleculeID, new MoleculesTableEntry(MoleculeEntityID, MoleculeName, isMoleculeCommon));
    }

    public MoleculesTableEntry getRow(String MoleculeID) {
        return this.ContentTable.get(MoleculeID);
    }

    public String getNameById(String MoleculeID) {
        MoleculesTableEntry entry = getRow(MoleculeID);
        if (entry != null)
            return entry.MoleculeName;
        else{
            return "NULL";
        }
    }

    public String getTissueById(String MoleculeID) {
        MoleculesTableEntry entry = this.ContentTable.get(MoleculeID);
        //return entry.MoleculeName;

        if (entry != null)
            return entry.Tissue;
        else{
            //System.out.println(MoleculeID);
            return "NULL";
        }
    }

    public boolean getCommonById(String MoleculeID) {
        MoleculesTableEntry entry = getRow(MoleculeID);
        return entry != null && entry.isMoleculeCommon;
    }

}
