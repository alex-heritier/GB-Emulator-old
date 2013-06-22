/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBMain {
    public static void main(String args[]) {
        GBMMU mmu = new GBMMU();
        GBCPU cpu = new GBCPU(mmu);
        cpu.run("/Users/Alex/Downloads/Pokemon Blue.gb");
    }
}
