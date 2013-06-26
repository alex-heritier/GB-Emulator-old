/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBBadInstruction extends GBInterrupt {

    public GBBadInstruction(String info) {
        super(info);
    }

    @Override
    void trigger() {
        GBDebug.log("Invalid instruction " + info);
    }
}
