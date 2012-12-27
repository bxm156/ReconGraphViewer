package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;

public class OrganismTable {

    public static final String ROOTID = "unspecified";
    public static final String UNKNOWNID = "Unknown";

    HashMap<String, OrganismTableEntry> ContentTable;
    HashMap<String, ArrayList<String>> ParentToChildrenTable;


    public OrganismTable() {
        ContentTable = new HashMap<String, OrganismTableEntry>();
        ParentToChildrenTable = new HashMap<String, ArrayList<String>>();
    }

    public void insertRow(String parentId, String organismGroupId, String organismCommonName, String organismScientificName) {
        if (parentId == null)
            parentId = ROOTID;

        this.ContentTable.put(organismGroupId, new OrganismTableEntry(organismGroupId, parentId, organismCommonName, organismScientificName));
        ArrayList<String> childrenofsameparent = ParentToChildrenTable.get(parentId);
        if (childrenofsameparent == null) {
            childrenofsameparent = new ArrayList<String>();
            ParentToChildrenTable.put(parentId, childrenofsameparent);
        }
        childrenofsameparent.add(organismGroupId);
    }

    public OrganismTableEntry getRow(String organismGroupId) {
        return this.ContentTable.get(organismGroupId);
    }

    public String getCommonNameById(String organismGroupId) {
        return this.ContentTable.get(organismGroupId).commonName;
    }

    public String getScientificNameById(String organismGroupId) {
        return this.ContentTable.get(organismGroupId).scientificName;
    }

    public String getParentId(String organismGroupId) {
        if (organismGroupId != null) {
            OrganismTableEntry entry = this.ContentTable.get(organismGroupId);
            if (entry != null)
                return entry.parentOrganismId;
            else return null;
        } else return null;
    }

    public ArrayList<String> getChildren(String parentId) {
        if (parentId == null) parentId = ROOTID;
        return this.ParentToChildrenTable.get(parentId);
    }

    public void dump() {
        for (String key : ContentTable.keySet()) {
            OrganismTableEntry entry = ContentTable.get(key);
            System.out.println(key + " --> " + entry.organismId + "," + entry.parentOrganismId + "," + entry.commonName + "," + entry.scientificName);
        }
    }

    public class OrganismTableEntry {
        public String organismId;
        public String parentOrganismId;
        public String commonName;
        public String scientificName;

        public OrganismTableEntry(String organismId, String parentOrganismId, String commonName, String scientificName) {
            this.organismId = organismId;
            this.commonName = commonName;
            this.scientificName = scientificName;
            this.parentOrganismId = parentOrganismId;
        }

        public String getCombinedName() {
            String name = "Non-named Organism";
            if (!scientificName.equals("") && commonName.equals(""))
                name = scientificName;
            else if (scientificName.equals("") && !commonName.equals(""))
                name = commonName;
            else if (!scientificName.equals("") && !commonName.equals(""))
                name = commonName + " [" + scientificName + "]";

            return name;
        }

        public String getSimplifiedName() {
            String name = "Non-named Organism";
            if (!commonName.equals(""))
                name = commonName;
            else if (!scientificName.equals(""))
                name = scientificName;

            return capitalize(name);
        }

        public String capitalize(String s) {
                if (s.length() == 0) return s;
                return s.substring(0, 1).toUpperCase() + s.substring(1);
            }

    }

}
