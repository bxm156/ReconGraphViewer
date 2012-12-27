package binevi.Resources.PathCaseResources;

import java.util.ArrayList;
import java.util.HashMap;


public class GenomeTable {

    public class ChromosomeEntry implements Comparable {
        public long length;
        public long centromerlocation;
        public String OrganismGroupId;
        public String ChromosomeName;

        public ChromosomeEntry(long length, long centromerlocation, String OrganismGroupId, String ChromosomeName) {
            this.length = length;
            this.centromerlocation = centromerlocation;
            this.OrganismGroupId = OrganismGroupId;
            this.ChromosomeName = ChromosomeName;
        }

        public int compareTo(Object o) {
            ChromosomeEntry other = (ChromosomeEntry) o;
            //return ChromosomeName.compareTo(other.ChromosomeName);
            String ch1 = ChromosomeName;
            String ch2 = other.ChromosomeName;

            Integer ch1i, ch2i;
            try{
                ch1i = Integer.parseInt(ch1);
            }
            catch (Exception e)
            {
                ch1i=null;
            }

            try{
                ch2i = Integer.parseInt(ch2);
            }
            catch (Exception e)
            {
                ch2i = null;
            }

            if (ch1i!=null && ch2i!=null)
                return ch1i.compareTo(ch2i);
            else if (ch1i!=null)
                return -1;
            else if (ch2i!=null)
                return 1;
            else
            {
                return (ch1.toLowerCase()).compareTo(ch2.toLowerCase());
            }

        }
    }

    public class GeneEntry {
        public int ChromosomeIndexID;
        public String name;
        public String geneId;
        public long genelocation;
        public String cytogeneticAddress;
        public String genericprocessId;

        public GeneEntry(int ChromosomeIndexID, String geneId, String name, long genelocation, String cytogeneticAddress, String genericprocessId) {
            this.ChromosomeIndexID = ChromosomeIndexID;
            this.name = name;
            this.geneId = geneId;
            this.genelocation = genelocation;
            this.cytogeneticAddress = cytogeneticAddress;
            this.genericprocessId = genericprocessId;
        }
    }

    private static int INDEXIDENTIFIER = 1234;

    public static int getNextUniqueIndexID() {
        return ++INDEXIDENTIFIER;
    }

    public HashMap<String, Integer> OrganismIdTOChromosomeCount;
    public HashMap<String, ArrayList<Integer>> OrganismIdTOChromosomeId;
    public HashMap<Integer, ChromosomeEntry> ChromosomeIdTOChromosomeEntry;
    public HashMap<Integer, ArrayList<Integer>> ChromosomeIdTOCGeneIdList;
    public HashMap<String, ArrayList<Integer>> GenericProcessIdTOCGeneIdList;
    public HashMap<Integer, GeneEntry> GeneIdTOGeneEntry;

    public GenomeTable() {
        OrganismIdTOChromosomeCount = new HashMap<String, Integer>();
        OrganismIdTOChromosomeId = new HashMap<String, ArrayList<Integer>>();
        ChromosomeIdTOChromosomeEntry = new HashMap<Integer, ChromosomeEntry>();
        ChromosomeIdTOCGeneIdList = new HashMap<Integer, ArrayList<Integer>>();
        GeneIdTOGeneEntry = new HashMap<Integer, GeneEntry>();
        GenericProcessIdTOCGeneIdList = new HashMap<String, ArrayList<Integer>>();
    }

    public void insertNewGenome(String organismGroupId, int numberofchromosomes) {
        OrganismIdTOChromosomeCount.put(organismGroupId, numberofchromosomes);
    }

    public int insertChromosomeToGenome(String OrganismGroupId, long length, long centromerlocation, String ChromosomeName) {
        ChromosomeEntry newChromosome = new ChromosomeEntry(length, centromerlocation, OrganismGroupId, ChromosomeName);
        int newChromosomeId = getNextUniqueIndexID();
        ChromosomeIdTOChromosomeEntry.put(newChromosomeId, newChromosome);
        ArrayList<Integer> chromosomesoforganism = OrganismIdTOChromosomeId.get(OrganismGroupId);
        if (chromosomesoforganism == null) {
            chromosomesoforganism = new ArrayList<Integer>();
            OrganismIdTOChromosomeId.put(OrganismGroupId, chromosomesoforganism);
        }
        chromosomesoforganism.add(newChromosomeId);
        return newChromosomeId;
    }

    public int insertGeneToChromosome(int ChromosomeIndexID, String geneid, String name, long genelocation, String cytogeneticAddress, String genericprocessId) {
        GeneEntry newGene = new GeneEntry(ChromosomeIndexID, geneid, name, genelocation, cytogeneticAddress, genericprocessId);
        int newGeneId = getNextUniqueIndexID();
        ArrayList<Integer> chromosomegenes = ChromosomeIdTOCGeneIdList.get(ChromosomeIndexID);
        if (chromosomegenes == null) {
            chromosomegenes = new ArrayList<Integer>();
            ChromosomeIdTOCGeneIdList.put(ChromosomeIndexID, chromosomegenes);
        }
        chromosomegenes.add(newGeneId);
        GeneIdTOGeneEntry.put(newGeneId, newGene);

        ArrayList<Integer> genericProcessGenes = GenericProcessIdTOCGeneIdList.get(genericprocessId);
        if (genericProcessGenes == null) {
            genericProcessGenes = new ArrayList<Integer>();
            GenericProcessIdTOCGeneIdList.put(genericprocessId, genericProcessGenes);
        }
        genericProcessGenes.add(newGeneId);

        return newGeneId;
    }

}
