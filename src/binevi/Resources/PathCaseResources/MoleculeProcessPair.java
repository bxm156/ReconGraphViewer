package binevi.Resources.PathCaseResources;


public class MoleculeProcessPair {
    public String molecule;
    public String process;
    public boolean input;
    public boolean reversible;


    public MoleculeProcessPair(String string1, String string2,boolean input,boolean flag) {
        this.molecule = string1;
        this.process = string2;
        this.reversible = flag;
        this.input = input;
    }
}
