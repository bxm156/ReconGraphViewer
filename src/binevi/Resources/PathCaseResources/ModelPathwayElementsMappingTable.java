package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Aug 6, 2009
 * Time: 1:03:33 PM
 */
public class ModelPathwayElementsMappingTable {
    public  class SpecMoleTableEntry{
        public String SpeciesID;
        public String QualifierName;
        public String MolecularID;

        public SpecMoleTableEntry(String speciesID, String qualifierName,String molecularID) {
              this.SpeciesID = speciesID;
              this.QualifierName= qualifierName;
              this.MolecularID= molecularID;
       }
    }

    public class ReacProcTableEntry{
        public String ReactionID;
        public String QualifierName;
        public String ProcessID;
        public ReacProcTableEntry(String speciesID, String qualifierName,String molecularID) {
              this.ReactionID = speciesID;
              this.QualifierName= qualifierName;
              this.ProcessID= molecularID;
        }
    }

    ArrayList<SpecMoleTableEntry> SpeMoleContentTable;
    ArrayList<ReacProcTableEntry> ReacProcContentTable;

    public void insertSpeMoleRow(String speciesID, String qualifierName,String molecularID) {
            this.SpeMoleContentTable.add(new SpecMoleTableEntry(speciesID, qualifierName, molecularID));
    }

    public void insertReacProcRow(String speciesID, String qualifierName,String molecularID) {
            this.ReacProcContentTable.add(new ReacProcTableEntry(speciesID, qualifierName, molecularID));
    }

    public ModelPathwayElementsMappingTable(){
        SpeMoleContentTable=new ArrayList<SpecMoleTableEntry>();
        ReacProcContentTable=new ArrayList<ReacProcTableEntry>();
    }

    public ArrayList<SpecMoleTableEntry> getSpeMoleContentTable(){
        return this.SpeMoleContentTable;
    }

    public ArrayList<ReacProcTableEntry> getReacProcContentTable(){
        return this.ReacProcContentTable;
    }
}

