/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex
 */
public class GBMMU {
//  General Memory Map
//
//  0000-3FFF   16KB ROM Bank 00     (in cartridge, fixed at bank 00)

    private short ROMBank00[];
//  4000-7FFF   16KB ROM Bank 01..NN (in cartridge, switchable bank number)
    private short ROMBank01[];
//  8000-9FFF   8KB Video RAM (VRAM) (switchable bank 0-1 in CGB Mode)
    private short videoRAM[];
//  A000-BFFF   8KB External RAM     (in cartridge, switchable bank, if any)
    private short externalRAM[];
//  C000-CFFF   4KB Work RAM Bank 0 (WRAM)
    private short workRAMBank0[];
//  D000-DFFF   4KB Work RAM Bank 1 (WRAM)  (switchable bank 1-7 in CGB Mode)
    private short workRAMBank1[];
//  E000-FDFF   Same as C000-DDFF (ECHO)    (typically not used)
//  FE00-FE9F   Sprite Attribute Table (OAM)
    private short SAT[];
//  FEA0-FEFF   Not Usable
//  FF00-FF7F   I/O Ports
    private short IOPorts[];
//  FF80-FFFE   High RAM (HRAM)
    private short highRAM[];
//  FFFF        Interrupt Enable Register
    private short IER;
    //memory locations
    private final int ROMBANK00_START = 0x0000;
    private final int ROMBANK01_START = 0x4000;
    private final int VIDEORAM_START = 0x8000;
    private final int EXTERNALRAM_START = 0xA000;
    private final int WORKBANKRAM0_START = 0xC000;
    private final int WORKBANKRAM1_START = 0xD000;
    private final int WORKBANKRAM0GHOST_START = 0xE000;
    private final int SAT_START = 0xFE00;
    private final int NOTUSABLE_START = 0xFEA0;
    private final int IOPORTS_START = 0xFF00;
    private final int HIGHRAM_START = 0xFF80;
    private final int IER_START = 0xFFFF;

    public GBMMU() {
        ROMBank00 = new short[ROMBANK01_START - ROMBANK00_START];
        ROMBank01 = new short[VIDEORAM_START - ROMBANK01_START];
        videoRAM = new short[EXTERNALRAM_START - VIDEORAM_START];
        externalRAM = new short[WORKBANKRAM0_START - EXTERNALRAM_START];
        workRAMBank0 = new short[WORKBANKRAM1_START - WORKBANKRAM0_START];
        workRAMBank1 = new short[WORKBANKRAM0GHOST_START - WORKBANKRAM1_START];
        SAT = new short[NOTUSABLE_START - SAT_START];
        IOPorts = new short[HIGHRAM_START - IOPORTS_START];
        highRAM = new short[IER_START - HIGHRAM_START];
    }

    public short readAddress(int address) {
        if (address < ROMBANK01_START) {
            return ROMBank00[address];
        } else if (address < VIDEORAM_START) {
            return ROMBank01[address - ROMBANK01_START];
        } else if (address < EXTERNALRAM_START) {
            return videoRAM[address - VIDEORAM_START];
        } else if (address < WORKBANKRAM0_START) {
            return externalRAM[address - EXTERNALRAM_START];
        } else if (address < WORKBANKRAM1_START) {
            return workRAMBank0[address - WORKBANKRAM0_START];
        } else if (address < WORKBANKRAM0GHOST_START) {
            return workRAMBank1[address - WORKBANKRAM1_START];
        } else if (address < SAT_START && address < 0xDDFF) {
            return workRAMBank0[address - WORKBANKRAM0GHOST_START];
        } else if (address < SAT_START && address >= 0xDDFF) {
            GBDebug.log("GBMMU.readAddress - Attempted to read non-mirrored memory at " + Integer.toHexString(address));
            return 0x00;
        } else if (address < NOTUSABLE_START) {
            return SAT[address - SAT_START];
        } else if (address < IOPORTS_START) {
            GBDebug.log("GBMMU.readAddress - Attempted to read unusable memory at " + Integer.toHexString(address));
            return 0x00;
        } else if (address < HIGHRAM_START) {
            return IOPorts[address - IOPORTS_START];
        } else if (address < IER_START) {
            return highRAM[address - HIGHRAM_START];
        } else if (address == IER_START) {
            return IER;
        } else {
            GBDebug.log("GBMMU.readAddress - Attempted to read out-of-bounds memory at " + Integer.toHexString(address));
            return 0;
        }
    }

    public void writeAddress(int address, short value) {
        if (address < ROMBANK01_START) {
            ROMBank00[address] = value;
        } else if (address < VIDEORAM_START) {
            ROMBank01[address - ROMBANK01_START] = value;
        } else if (address < EXTERNALRAM_START) {
            videoRAM[address - VIDEORAM_START] = value;
        } else if (address < WORKBANKRAM0_START) {
            externalRAM[address - EXTERNALRAM_START] = value;
        } else if (address < WORKBANKRAM1_START) {
            workRAMBank0[address - WORKBANKRAM0_START] = value;
        } else if (address < WORKBANKRAM0GHOST_START) {
            workRAMBank1[address - WORKBANKRAM1_START] = value;
        } else if (address < SAT_START && address < 0xDDFF) {
            workRAMBank0[address - WORKBANKRAM0GHOST_START] = value;
        } else if (address < SAT_START && address >= 0xDDFF) {
            GBDebug.log("GBMMU.writeAddress - Attempted to write non-mirrored memory at " + Integer.toHexString(address));
        } else if (address < NOTUSABLE_START) {
            SAT[address - SAT_START] = value;
        } else if (address < IOPORTS_START) {
            GBDebug.log("GBMMU.writeAddress - Attempted to write unusable memory at " + Integer.toHexString(address));
        } else if (address < HIGHRAM_START) {
            IOPorts[address - IOPORTS_START] = value;
        } else if (address < IER_START) {
            highRAM[address - HIGHRAM_START] = value;
        } else if (address == IER_START) {
            IER = value;
        } else {
            GBDebug.log("GBMMU.writeAddress - Attempted to write out-of-bounds memory at " + Integer.toHexString(address));
        }
    }
}
