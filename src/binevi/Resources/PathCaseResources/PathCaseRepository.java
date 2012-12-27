package binevi.Resources.PathCaseResources;

import java.util.HashSet;
import java.util.ArrayList;


public class PathCaseRepository {
                                                                
    public GenericProcessTable genericProcessTable;
    public GenericProcessToECNumbersTable genericProcessToECNumbersTable;
    public GenericProcessToEnzmyesTable genericProcessToEnzmyesTable;
    public GenericProcessToSpecificProcessListTable genericProcessToCatalyzesTable;
    public MoleculesTable moleculesTable;
    public PathwayTable pathwayTable;
    public PathwayTable mappingPathwayTable;
    public PathwayToIdContentTable pathwayToGenericProcessesTable;
    public PathwayToIdMapContentTable pathwayToLinkingMoleculesTable;
    public PathwayToIdContentTable pathwayToOrganismGroupsTable;
    public ProcessToMoleculesTable genericProcessToMoleculesTable;
    public ProcessToMoleculesTable specificProcessToMoleculesTable;
    public GenomeTable genomeTable;
    public OrganismTable organismTable;
    //03/13/09
    public CompartmentsTable compartmentsTable;
    public SpeciesTable speciesTable;
    public ReactionsTable reactionsTable;
    public ReactionSpeciesTable reactionSpeciesTable;
    public CompartmentToSpeciesTable compartmentToSpeciesTable;
    public CompartmentToReactionsTable  compartmentToReactionsTable;
    public ModelTable modelTable;
    public ReactionToReactionSpeciesTable reactionToReactionSpeciesTable;
    public ReactionToEnzymeTable reactionToEnzymeTable;
    public EnzymeTable enzymeTable;
    public PathwayToCompartmentTable pathwayToCompartmentTable;
    public ModelPathwayElementsMappingTable modelPathwayElementsMappingTable;

    public HashSet<String> TissueNames;
    public ArrayList<String> Compartments;
    public PathwaysRelationTable pathwaysRelationTable;

    public PathCaseRepository() {
        reset();
    }

    public void copyTo(PathCaseRepository newR)
    {
        newR.reset();
        newR.genericProcessTable=this.genericProcessTable;
        newR.genericProcessToECNumbersTable=this.genericProcessToECNumbersTable;
        newR.genericProcessToEnzmyesTable=this.genericProcessToEnzmyesTable;
        newR.genericProcessToCatalyzesTable=this.genericProcessToCatalyzesTable;
        newR.moleculesTable=this.moleculesTable;
        newR.pathwayTable=this.pathwayTable;
        newR.mappingPathwayTable=this.mappingPathwayTable;
        newR.pathwayToGenericProcessesTable=this.pathwayToGenericProcessesTable;
        newR.pathwayToLinkingMoleculesTable=this.pathwayToLinkingMoleculesTable;
        newR.pathwayToOrganismGroupsTable=this.pathwayToOrganismGroupsTable;
        newR.genericProcessToMoleculesTable=this.genericProcessToMoleculesTable;
        newR.specificProcessToMoleculesTable=this.specificProcessToMoleculesTable;
        newR.genomeTable=this.genomeTable;
        newR.organismTable=this.organismTable;

        newR.compartmentsTable=this.compartmentsTable;
        newR.speciesTable=this.speciesTable;
        newR.reactionsTable=this.reactionsTable;
        newR.reactionSpeciesTable=this.reactionSpeciesTable;
        newR.compartmentToSpeciesTable=this.compartmentToSpeciesTable;
        newR.compartmentToReactionsTable=this.compartmentToReactionsTable;
        newR.modelTable=this.modelTable;
        newR.reactionToReactionSpeciesTable=this.reactionToReactionSpeciesTable;
        newR.reactionToEnzymeTable=this.reactionToEnzymeTable;
        newR.enzymeTable=this.enzymeTable;
        newR.pathwayToCompartmentTable=this.pathwayToCompartmentTable;
        newR.modelPathwayElementsMappingTable=this.modelPathwayElementsMappingTable;

        newR.TissueNames=this.TissueNames;
        newR.Compartments=this.Compartments;
        newR.pathwaysRelationTable = this.pathwaysRelationTable;
    }

    
    public void reset() {
        genericProcessTable = new GenericProcessTable();
        genericProcessToECNumbersTable = new GenericProcessToECNumbersTable();
        genericProcessToEnzmyesTable = new GenericProcessToEnzmyesTable();
        genericProcessToCatalyzesTable = new GenericProcessToSpecificProcessListTable();
        moleculesTable = new MoleculesTable();
        pathwayTable = new PathwayTable();
        mappingPathwayTable = new PathwayTable();
        pathwayToGenericProcessesTable = new PathwayToIdContentTable();
        pathwayToLinkingMoleculesTable = new PathwayToIdMapContentTable();
        pathwayToOrganismGroupsTable = new PathwayToIdContentTable();
        genericProcessToMoleculesTable = new ProcessToMoleculesTable();
        specificProcessToMoleculesTable = new ProcessToMoleculesTable();
        genomeTable = new GenomeTable();
        organismTable = new OrganismTable();

        compartmentsTable=new CompartmentsTable();
        speciesTable=new SpeciesTable();
        reactionsTable=new ReactionsTable();
        reactionSpeciesTable = new ReactionSpeciesTable();
        compartmentToSpeciesTable = new CompartmentToSpeciesTable();
        compartmentToReactionsTable =new CompartmentToReactionsTable();
        modelTable = new ModelTable();
        reactionToReactionSpeciesTable=new ReactionToReactionSpeciesTable();
        reactionToEnzymeTable = new ReactionToEnzymeTable();
        enzymeTable = new EnzymeTable();
        pathwayToCompartmentTable = new PathwayToCompartmentTable();

        TissueNames=new HashSet<String>();
        Compartments=new ArrayList<String>();
        modelPathwayElementsMappingTable=new ModelPathwayElementsMappingTable();
        pathwaysRelationTable= new PathwaysRelationTable();
    }

    public void resetPWrelated() {
        pathwayTable = new PathwayTable();
        moleculesTable = new MoleculesTable();
        genomeTable = new GenomeTable();
        organismTable = new OrganismTable();

        pathwayToGenericProcessesTable = new PathwayToIdContentTable();
        pathwayToLinkingMoleculesTable = new PathwayToIdMapContentTable();
        pathwayToOrganismGroupsTable = new PathwayToIdContentTable();

        specificProcessToMoleculesTable = new ProcessToMoleculesTable();

        genericProcessTable = new GenericProcessTable();
        genericProcessToECNumbersTable = new GenericProcessToECNumbersTable();
        genericProcessToEnzmyesTable = new GenericProcessToEnzmyesTable();
        genericProcessToCatalyzesTable = new GenericProcessToSpecificProcessListTable();
        genericProcessToMoleculesTable = new ProcessToMoleculesTable();

        modelPathwayElementsMappingTable=new ModelPathwayElementsMappingTable();
//         pathwaysRelationTable= new PathwaysRelationTable();
    }

}
