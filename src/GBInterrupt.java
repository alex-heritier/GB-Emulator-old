/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public abstract class GBInterrupt {
    public final String info;
    public static final GBNoInterrupt INT_NONE = new GBNoInterrupt("INT_NONE");
    public static final GBBadInstruction INT_BADINSTRUCTION = new GBBadInstruction("INT_BADINSTRUCTION");
    public static final GBExit INT_EXIT = new GBExit("INT_EXIT");

    public GBInterrupt(String info) {
        this.info = info;
    }

    public boolean equals(GBInterrupt interrupt) {
        return this.getClass().getName().equals(interrupt.getClass().getName());
    }

    abstract void trigger();
}
