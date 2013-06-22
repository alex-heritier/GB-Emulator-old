/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBInterrupt {
    public enum Interrupt {
        INT_NONE,
        INT_EXIT,
        INT_SEGFAULT,
        INT_BADINSTRUCTION
    }
    public final Interrupt type;
    public final String info;
    
    public static final GBInterrupt INT_NONE = new GBInterrupt(Interrupt.INT_NONE, "No interrupt");
    public static final GBInterrupt INT_EXIT = new GBInterrupt(Interrupt.INT_EXIT, "CPU has been told to shutdown");
    public static final GBInterrupt INT_SEGFAULT = new GBInterrupt(Interrupt.INT_SEGFAULT, "Invalid memory accessed");
    public static final GBInterrupt INT_BADINSTRUCTION = new GBInterrupt(Interrupt.INT_BADINSTRUCTION, "Invalid instruction");
    
    public GBInterrupt(Interrupt type, String info) {
        this.type = type;
        this.info = info;
    }
    
    public GBInterrupt(GBInterrupt interrupt, String info) {
        this.type = interrupt.type;
        this.info = info;
    }
    
    public boolean equals(GBInterrupt interrupt) {
        return type == interrupt.type;
    }
}
