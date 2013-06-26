/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBExit extends GBInterrupt {

    public GBExit(String info) {
        super(info);
    }

    @Override
    void trigger() {
        GBDebug.log("Exiting with info: " + info);
        System.exit(0);
    }
}
