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
public class SpeciesTable {

    public ArrayList<String> getSpeciesIdList() {
       return new ArrayList<String>(ContentTable.keySet());
   }

    class SpeciesTableEntry{
            public String SpeciesID;
            public String SpeciesName;
            public String sbmlID;
            public String InitialAmount;
            public String SpeciesTypeId;
            public String InitialConcentration;
            public String SubstanceUnitsId;
            public boolean HasOnlySubstanceUnits;
            public boolean BoundaryCondition;
            public String Charge;
            public boolean Constant;
            public boolean IsCommon;

            public SpeciesTableEntry(String SpeciesID, String SpeciesName, String sbmlID,String InitialAmount,String SpeciesTypeId,String InitialConcentration,String SubstanceUnitsId,boolean HasOnlySubstanceUnits, boolean BoundaryCondition,String Charge,boolean Constant,boolean Common) {
                    this.SpeciesID = SpeciesID;
                    this.sbmlID=sbmlID;
                    this.SpeciesName = SpeciesName;
                    this.InitialAmount = InitialAmount;
                    this.SpeciesTypeId = SpeciesTypeId;
                    this.InitialConcentration = InitialConcentration;
                    this.SubstanceUnitsId = SubstanceUnitsId;
                    this.HasOnlySubstanceUnits = HasOnlySubstanceUnits;
                    this.BoundaryCondition = BoundaryCondition;
                    this.Charge = Charge;
                    this.Constant = Constant;
                    this.IsCommon= Common;
                 }
    }

      HashMap<String, SpeciesTableEntry> ContentTable;

    public SpeciesTable() {
        ContentTable = new HashMap<String, SpeciesTableEntry>();
    }

    public void insertRow(String SpeciesID, String SpeciesName, String sbmlID,String InitialAmount,String SpeciesTypeId,String InitialConcentration,String SubstanceUnitsId,boolean HasOnlySubstanceUnits, boolean BoundaryCondition,String Charge,boolean Constant,boolean Common) {
        this.ContentTable.put(SpeciesID, new SpeciesTableEntry(SpeciesID, SpeciesName,sbmlID,InitialAmount,SpeciesTypeId,InitialConcentration,SubstanceUnitsId,HasOnlySubstanceUnits, BoundaryCondition,Charge,Constant,Common));
    }

    public void updateRowWithRole(String SpeciesID,String sbmlID) {
        this.ContentTable.get(SpeciesID).sbmlID=sbmlID;                
    }

    public SpeciesTableEntry getRow(String SpeciesID) {
        return this.ContentTable.get(SpeciesID);
    }

    public String getNameById(String SpeciesID) {
        SpeciesTableEntry entry = getRow(SpeciesID);
        if (entry != null){
            if(!entry.SpeciesName.equalsIgnoreCase(""))
                return entry.SpeciesName;
            else return "Species Name is empty"; //entry.sbmlID
        }
        else{
            return "NULL";
        }
    }

    public String getSBMLIDById(String SpeciesID) {
        SpeciesTableEntry entry = getRow(SpeciesID);
        if (entry != null){
            if(!entry.sbmlID.equalsIgnoreCase("")) return entry.sbmlID;
            else return "Species SBML ID is empty"; //entry.sbmlID
        }
        else{
            return "NULL";
        }
    }

    public boolean getCommonById(String SpeciesID) {
            SpeciesTableEntry entry = getRow(SpeciesID);
            return entry != null && entry.IsCommon;
        }

}
