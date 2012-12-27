package binevi.Resources.PathCaseResources;


public class MarkedStringPair {
    public String molecule;
    public String process;

    public static enum DIRECTION {IN, OUT, INOUT}
    public static enum SIGN {POSITIVE, NEGATIVE, NEUTRAL}

    public DIRECTION direction;
    public SIGN signature;

    public MarkedStringPair(String string1, String string2, DIRECTION dir) {
        this.molecule = string1;
        this.process = string2;
        this.direction = dir;
        this.signature = SIGN.NEUTRAL;
    }

    public MarkedStringPair(String string1, String string2, SIGN sig) {
        this.molecule = string1;
        this.process = string2;
        this.direction = DIRECTION.INOUT;
        this.signature = sig;
    }
}

