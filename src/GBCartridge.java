
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
public class GBCartridge {

    private short[] cartData;
    public final long length;
    public final int ROMBANK00_START = 0x0000;
    public final int ROMBANK01_START = 0x4000;
    public final int VIDEORAM_START = 0x8000;
    public final int EXTERNALRAM_START = 0xA000;
    public final int WORKBANKRAM0_START = 0xC000;
    public final int WORKBANKRAM1_START = 0xD000;
    public final int WORKBANKRAM0GHOST_START = 0xE000;
    public final int SAT_START = 0xFE00;
    public final int NOTUSABLE_START = 0xFEA0;
    public final int IOPORTS_START = 0xFF00;
    public final int HIGHRAM_START = 0xFF80;
    public final int IER_START = 0xFFFF;

    public GBCartridge(String cartname) {
        File cart = new File(cartname);
        length = cart.length();
        try {
            cartData = read(cart);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GBCartridge.class.getName()).log(Level.SEVERE, null, ex);
            GBDebug.log("GBCPU.loadROM - Could not find file: " + cart.getName());
        }
    }

    public GBCartridge(File cart) {
        length = cart.length();
        try {
            cartData = read(cart);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GBCartridge.class.getName()).log(Level.SEVERE, null, ex);
            GBDebug.log("GBCPU.loadROM - Could not find file: " + cart.getName());
        }
    }

    private short[] read(File cart) throws FileNotFoundException {
        short[] data = new short[(int)cart.length()];
        FileInputStream in;
        in = new FileInputStream(cart);
        try {
            for (int i = 0x0000, next = in.read(); i < length; i++, next = in.read()) {
                data[i] = (short)next;
                //System.out.print(Integer.toHexString(next).toUpperCase() + " ");
            }
        } catch (IOException ex) {
            Logger.getLogger(GBCartridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public short[] substring(int begin, int end) {   //begin inclusive, end exclusive
        if (cartData.length <= end || begin < 0) {
            GBDebug.log("GBCartridge.substring - invalid parameters: begin = " + begin + ", end = " + end);
            return null;
        }
        short[] data = new short[end - begin];
        for (int i = begin; i < end; i++) {
            data[i - begin] = cartData[i];
        }
        return data;
    }
}
