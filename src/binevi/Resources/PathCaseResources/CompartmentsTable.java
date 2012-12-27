package binevi.Resources.PathCaseResources;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 11:35:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompartmentsTable {

     public ArrayList<String> getCompartmentIdList() {
        return new ArrayList<String>(ContentTable.keySet());
    }

    class CompartmentsTableEntry{
        public String CompartmentID;
        public String CompartmentName;
        public String sbmlID;
        public String Size;
        public String SpatialDimensions;
        public boolean isConstant;
        public String CompartmentType;
        public String OutsideID;

        public CompartmentsTableEntry(String CompartmentID, String CompartmentName,String sbmlID,String size,String SpatialDimensions, boolean isConstant, String CompartmentType,String OutsideID) {
                this.CompartmentID = CompartmentID;
                this.CompartmentName = CompartmentName;
                this.sbmlID=sbmlID;
                this.Size = size;
                this.SpatialDimensions = SpatialDimensions;
                this.isConstant = isConstant;
                this.CompartmentType = CompartmentType;
                this.OutsideID = OutsideID;
             }

//        public boolean equals(Object other) {
//            return other instanceof CompartmentsTableEntry && ((CompartmentsTableEntry) other).isMoleculeCommon == isMoleculeCommon && ((CompartmentsTableEntry) other).MoleculeName.equals(this.MoleculeName) && ((CompartmentsTableEntry) other).MoleculeEntityID.equals(this.MoleculeEntityID);
//        }
    }

    HashMap<String, CompartmentsTableEntry> ContentTable;

    public CompartmentsTable() {
        ContentTable = new HashMap<String, CompartmentsTableEntry>();
    }

    public void insertRow(String CompartmentID, String CompartmentName,String sbmlID,String size,String SpatialDimensions, boolean isConstant, String CompartmentType,String OutsideID) {
        this.ContentTable.put(CompartmentID, new CompartmentsTableEntry(CompartmentID, CompartmentName,sbmlID,size,SpatialDimensions, isConstant, CompartmentType,OutsideID));
    }

    public CompartmentsTableEntry getRow(String CompartmentID) {
        return this.ContentTable.get(CompartmentID);
    }

    public String getNameById(String CompartmentID) {
        CompartmentsTableEntry entry = getRow(CompartmentID);
        if (entry != null){
            if(!entry.CompartmentName.equals(""))
                return entry.CompartmentName;
            else return entry.sbmlID;
        }
        else{
            return "NULL";
        }
    }

    public String getSizeById(String CompartmentID) {
          CompartmentsTableEntry entry = getRow(CompartmentID);
          if (entry != null){
//              if(!entry.Size.equals(""))
                  return entry.Size;              
          }
          else{
              return "NULL";
          }
      }


    public String getOutsideById(String CompartmentID) {
        CompartmentsTableEntry entry = getRow(CompartmentID);
        if (entry != null)
            return entry.OutsideID;
        else{
            return "NULL";
        }
    }
}
