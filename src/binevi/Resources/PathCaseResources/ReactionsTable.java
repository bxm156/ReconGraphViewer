package binevi.Resources.PathCaseResources;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 13, 2009
 * Time: 4:48:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReactionsTable {

    public ArrayList<String> getReactionIdList() {
       return new ArrayList<String>(ContentTable.keySet());
   }

    class ReactionsTableEntry{
       public String ReactionID;
       public String sbmlId;
       public String ReactionName;
       public boolean isReversible;
       public String KineticLawId;
       public boolean isFast;
       public String state;
       public ReactionsTableEntry(String ReactionID, String ReactionName,String sbmlId, boolean isReversible, String KineticLawId,boolean isFast) {
            this.ReactionID = ReactionID ;
            this.ReactionName = ReactionName;
            this.sbmlId=sbmlId;
            this.isReversible = isReversible;
            this.KineticLawId = KineticLawId;
            this.isFast = isFast;
            this.state="active";
       }
       public ReactionsTableEntry(String ReactionID, String ReactionName,String sbmlId, boolean isReversible, String KineticLawId,boolean isFast, String iState) {
            this.ReactionID = ReactionID ;
            this.ReactionName = ReactionName;
            this.sbmlId=sbmlId;
            this.isReversible = isReversible;
            this.KineticLawId = KineticLawId;
            this.isFast = isFast;
            this.state=iState;
       }
    }

    HashMap<String, ReactionsTableEntry> ContentTable;

    public ReactionsTable() {
        ContentTable = new HashMap<String, ReactionsTableEntry>();
    }

    public void insertRow(String ReactionID, String ReactionName,String sbmlId,boolean isReversible,String KineticLawId, boolean isFast) {
        this.ContentTable.put(ReactionID, new ReactionsTableEntry(ReactionID, ReactionName,sbmlId,isReversible,KineticLawId, isFast));
    }

    public void insertRow(String ReactionID, String ReactionName,String sbmlId,boolean isReversible,String KineticLawId, boolean isFast,String isActive) {
        this.ContentTable.put(ReactionID, new ReactionsTableEntry(ReactionID, ReactionName,sbmlId,isReversible,KineticLawId, isFast,isActive));
    }
    
    public void deleteRow(String ReactionID) {
    	this.ContentTable.remove(ReactionID);
    }

    public ReactionsTableEntry getRow(String ReactionID) {
        return this.ContentTable.get(ReactionID);
    }

    public String getNameById(String ReactionID) {
        ReactionsTableEntry entry = getRow(ReactionID);
        if (entry != null){
            if(!entry.ReactionName.equalsIgnoreCase(""))return entry.ReactionName;
            else return "Reaction Name is empty";
        }
        else{
            return "NULL";
        }
    }

    public String getSBMLIDById(String ReactionID) {
        ReactionsTableEntry entry = getRow(ReactionID);
        if (entry != null){
            if(!entry.sbmlId.equalsIgnoreCase(""))return entry.sbmlId;
            else return "Reaction SBML ID is empty";
        }
        else{
            return "NULL";
        }
    }

    public String getMathById(String ReactionID) {
        ReactionsTableEntry entry = getRow(ReactionID);
        if (entry != null){
            if(!entry.KineticLawId.equalsIgnoreCase(""))return entry.KineticLawId;
            else return "";
        }
        else{
            return "NULL";
        }
    }

    public boolean getIsReversibleById(String ReactionID) {
        ReactionsTableEntry entry = getRow(ReactionID);
        if (entry != null){
            return entry.isReversible;
        }
        else{
            return false;
        }
    }

    public boolean getIsTransportById(String ReactionID) {
         ReactionsTableEntry entry = getRow(ReactionID);
         if (entry != null){
             return entry.isFast;
         }
         else{
             return false;
         }
     }


}