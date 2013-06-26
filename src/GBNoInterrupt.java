/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBNoInterrupt extends GBInterrupt {

    public GBNoInterrupt(String info) {
        super(info);
    }

    @Override
    void trigger() {
        GBDebug.log("Triggering GBNoInterrupt with info: " + info);
    }
}
