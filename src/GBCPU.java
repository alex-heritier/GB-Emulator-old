
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
public class GBCPU {

    private GBMMU MMU;
    private GBCartridge cart;
    private int SP, PC;
    private short A, B, C, D, E, H, L;
    private boolean zero, subtract, halfCarry, carry;
    private boolean IER;
    private boolean DAA;
    private GBInterrupt interrupt;
    private long instructionCount, errorCount;

    public GBCPU(GBMMU mmu) {
        MMU = mmu;
        cart = null;
        PC = 0x100; //hard-coded entry point in cartidge
        SP = A = B = C = D = E = H = L = 0;
        zero = subtract = halfCarry = carry = false;
        IER = true;
        DAA = false;
        interrupt = GBInterrupt.INT_NONE;
        instructionCount = errorCount = 0;
    }

    private void loadROM() {
        short[] data = cart.substring(0, 0x4000);
        for (int i = 0; i < data.length; i++) {
            MMU.writeAddress(i + 0x100, data[i]);
        }
    }

    public void run(String gameName) {
        cart = new GBCartridge(gameName);
        loadROM();
        while (true) {
            if (IER) {
                interrupt.trigger();
            }
            interrupt = GBInterrupt.INT_NONE;
            short instruction = MMU.readAddress(PC);
            PC++;
            processInstruction(instruction);
            if (PC > 0xFFFF) {
                interrupt.trigger();
                System.out.println(coreDump());
                System.out.println("Instructions executed: " + instructionCount);
                System.out.println("Errors produced: " + errorCount);
                System.out.printf("%d instructions recognized\n", instructionCount - errorCount);
                System.out.printf("%.2f%% failure rate\n", (double) errorCount / (double) instructionCount * 100);
                System.exit(0);
            }
        }
    }

    private void processInstruction(short instruction) {
        int word;
        short byte1;
        short byte2;
        boolean bool;
        instructionCount++;
        switch (instruction) {
            //8 Bit Transfer Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//MOV		A,A		LD		A,A		7F		A <- A
//MOV		A,B		LD		A,B		78		A <- B
//MOV		A,C		LD		A,C		79		A <- C
//MOV		A,D		LD		A,D		7A		A <- D
//MOV		A,E		LD		A,E		7B		A <- E
//MOV		A,H		LD		A,H		7C		A <- H
//MOV		A,L		LD		A,L		7D		A <- L
//MOV		A,M		LD		A,(HL)		7E		A <- (HL)
//LDAX		B		LD		A,(BC)		0A		A <- (BC)
//LDAX		D		LD		A,(DE)		1A		A <- (DE)
            case 0x7F:  //LD A, A
                break;  //nothing happens
            case 0x78:  //LD A, B
                A = B;
                break;
            case 0x79:  //LD A, C
                A = C;
                break;
            case 0x7A:  //LD A, D
                A = D;
                break;
            case 0x7B:  //LD A, E
                A = E;
                break;
            case 0x7C:  //LD A, H
                A = H;
                break;
            case 0x7D:  //LD A, L
                A = L;
                break;
            case 0x7E:  //LD A, (HL);
                word = makeWord(H, L);
                A = MMU.readAddress(word);
                break;
            case 0x0A:  //LD A, (BC)
                word = makeWord(B, C);
                A = MMU.readAddress(word);
                break;
            case 0x1A:  //LD A, (DE)
                word = makeWord(D, E);
                A = MMU.readAddress(word);
                break;
//LDA		word		LD		A,(word)    3Aword		A <- (word)
//MOV		B,A		LD		B,A		47		B <- A
//MOV		B,B		LD		B,B		40		B <- B
//MOV		B,C		LD		B,C		41		B <- C
//MOV		B,D		LD		B,D		42		B <- D
//MOV		B,E		LD		B,E		43		B <- E
//MOV		B,H		LD		B,H		44		B <- H
//MOV		B,L		LD		B,L		45		B <- L
//MOV		B,M		LD		B,(HL)		46		B <- (HL)
            case 0x3A:  //LDD A, (HL)
                word = H << 8;
                word += L;
                byte1 = MMU.readAddress(word);
                MMU.writeAddress(A, byte1);
                L = (short) (L == 0 ? 0 : L - 1);
                break;
            case 0x47:  //LD B, A
                B = A;
                break;
            case 0x40:  //LD B, B
                break;
            case 0x41:  //LD B, C
                B = C;
                break;
            case 0x42:  //LD B, D
                B = D;
                break;
            case 0x43:  //LD B, E
                B = E;
                break;
            case 0x44:  //LD B, H
                B = H;
                break;
            case 0x45:  //LD B, L
                B = L;
                break;
            case 0x46:  //LD B, (HL)
                word = makeWord(H, L);
                B = MMU.readAddress(word);
                break;
//MOV		C,A		LD		C,A		4F		C <- A
//MOV		C,B		LD		C,B		48		C <- B
//MOV		C,C		LD		C,C		49		C <- C
//MOV		C,D		LD		C,D		4A		C <- D
//MOV		C,E		LD		C,E		4B		C <- E
//MOV		C,H		LD		C,H		4C		C <- H
//MOV		C,L		LD		C,L		4D		C <- L
//MOV		C,M		LD		C,(HL)		4E		C <- (HL)
            case 0x4F:  //LD C, A
                C = A;
                break;
            case 0x48:  //LD C, B
                C = B;
                break;
            case 0x49:  //LD C, C
                break;
            case 0x4A:  //LD C, D
                C = D;
                break;
            case 0x4B:  //LD C, E
                C = E;
                break;
            case 0x4C:  //LD C, H
                C = H;
                break;
            case 0x4D:  //LD C, L
                C = L;
                break;
            case 0x4E:  //LD C, (HL)
                word = makeWord(H, L);
                C = MMU.readAddress(word);
                break;
//MOV		D,A		LD		D,A		57		D <- A
//MOV		D,B		LD		D,B		50		D <- B
//MOV		D,C		LD		D,C		51		D <- C
//MOV		D,D		LD		D,D		52		D <- D
//MOV		D,E		LD		D,E		53		D <- E
//MOV		D,H		LD		D,H		54		D <- H
//MOV		D,L		LD		D,L		55		D <- L
//MOV		D,M		LD		D,(HL)		56		D <- (HL)
            case 0x57:  //LD D, A
                D = A;
                break;
            case 0x50:  //LD D, B
                D = B;
                break;
            case 0x51:  //LD D, C
                D = C;
                break;
            case 0x52:  //LD D, D
                break;
            case 0x53:  //LD D, E
                D = E;
                break;
            case 0x54:  //LD D, H
                D = H;
                break;
            case 0x55:  //LD D, L
                D = L;
                break;
            case 0x56:  //LD D, (HL)
                word = makeWord(H, L);
                D = MMU.readAddress(word);
                break;
//MOV		E,A		LD		E,A		5F		E <- A
//MOV		E,B		LD		E,B		58		E <- B
//MOV		E,C		LD		E,C		59		E <- C
//MOV		E,D		LD		E,D		5A		E <- D
//MOV		E,E		LD		E,E		5B		E <- E
//MOV		E,H		LD		E,H		5C		E <- H
//MOV		E,L		LD		E,L		5D		E <- L
//MOV		E,M		LD		E,(HL)		5E		E <- (HL)
            case 0x5F:  //LD E, A
                E = A;
                break;
            case 0x58:  //LD E, B
                E = B;
                break;
            case 0x59:  //LD E, C
                E = C;
                break;
            case 0x5A:  //LD E, D
                E = D;
                break;
            case 0x5B:  //LD E, E
                break;
            case 0x5C:  //LD E, H
                E = H;
                break;
            case 0x5D:  //LD E, L
                E = L;
                break;
            case 0x5E:  //LD E, (HL)
                word = makeWord(H, L);
                E = MMU.readAddress(word);
                break;
//MOV		H,A		LD		H,A		67		H <- A
//MOV		H,B		LD		H,B		60		H <- B
//MOV		H,C		LD		H,C		61		H <- C
//MOV		H,D		LD		H,D		62		H <- D
//MOV		H,E		LD		H,E		63		H <- E
//MOV		H,H		LD		H,H		64		H <- H
//MOV		H,L		LD		H,L		65		H <- L
//MOV		H,M		LD		H,(HL)		66		H <- (HL)
            case 0x67:  //LD H, A
                H = A;
                break;
            case 0x60:  //LD H, B
                H = B;
                break;
            case 0x61:  //LD H, C
                H = C;
                break;
            case 0x62:  //LD H, D
                H = D;
                break;
            case 0x63:  //LD H, E
                H = E;
                break;
            case 0x64:  //LD H, H
                break;
            case 0x65:  //LD H, L
                H = L;
                break;
            case 0x66:  //LD H, (HL)
                word = makeWord(H, L);
                H = MMU.readAddress(word);
                break;
//MOV		L,A		LD		L,A		6F		L <- A
//MOV		L,B		LD		L,B		68		L <- B
//MOV		L,C		LD		L,C		69		L <- C
//MOV		L,D		LD		L,D		6A		L <- D
//MOV		L,E		LD		L,E		6B		L <- E
//MOV		L,H		LD		L,H		6C		L <- H
//MOV		L,L		LD		L,L		6D		L <- L
//MOV		L,M		LD		L,(HL)		6E		L <- (HL)
            case 0x6F:  //LD L, A
                L = A;
                break;
            case 0x68:  //LD L, B
                L = B;
                break;
            case 0x69:  //LD L, C
                L = C;
                break;
            case 0x6A:  //LD L, D
                L = D;
                break;
            case 0x6B:  //LD L, E
                L = E;
                break;
            case 0x6C:  //LD L, H
                L = H;
                break;
            case 0x6D:  //LD L, L
                break;
            case 0x6E:  //LD L, (HL)
                word = makeWord(H, L);
                L = MMU.readAddress(word);
                break;
//MOV		M,A		LD		(HL),A		77		(HL) <- A
//MOV		M,B		LD		(HL),B		70		(HL) <- B
//MOV		M,C		LD		(HL),C		71		(HL) <- C
//MOV		M,D		LD		(HL),D		72		(HL) <- D
//MOV		M,E		LD		(HL),E		73		(HL) <- E
//MOV		M,H		LD		(HL),H		74		(HL) <- H
//MOV		M,L		LD		(HL),L		75		(HL) <- L
            case 0x77:  //LD (HL), A
                word = makeWord(H, L);
                MMU.writeAddress(word, A);
                break;
            case 0x70:  //LD (HL), B
                word = makeWord(H, L);
                MMU.writeAddress(word, B);
            case 0x71:  //LD (HL), C
                word = makeWord(H, L);
                MMU.writeAddress(word, C);
                break;
            case 0x72:  //LD (HL), D
                word = makeWord(H, L);
                MMU.writeAddress(word, D);
                break;
            case 0x73:  //LD (HL), E
                word = makeWord(H, L);
                MMU.writeAddress(word, E);
                break;
            case 0x74:  //LD (HL), H
                word = makeWord(H, L);
                MMU.writeAddress(word, H);
                break;
            case 0x75:  //LD (HL), L
                word = makeWord(H, L);
                MMU.writeAddress(word, L);
                break;
//MVI		A,byte		LD		A,byte		3Ebyte		A <- byte
//MVI		B,byte		LD		B,byte		06byte		B <- byte
//MVI		C,byte		LD		C,byte		0Ebyte		C <- byte
//MVI		D,byte		LD		D,byte		16byte		D <- byte
//MVI		E,byte		LD		E,byte		1Ebyte		E <- byte
//MVI		H,byte		LD		H,byte		26byte		H <- byte
//MVI		L,byte		LD		L,byte		2Ebyte		L <- byte
            case 0x3E:  //LD A, byte
                A = MMU.readAddress(PC);
                PC++;
                break;
            case 0x06:  //LD B, byte
                B = MMU.readAddress(PC);
                PC++;
                break;
            case 0x0E:  //LD C, byte
                C = MMU.readAddress(PC);
                PC++;
                break;
            case 0x16:  //LD D, byte
                D = MMU.readAddress(PC);
                PC++;
                break;
            case 0x1E:  //LD E, byte
                E = MMU.readAddress(PC);
                PC++;
                break;
            case 0x26:  //LD H, byte
                H = MMU.readAddress(PC);
                PC++;
                break;
            case 0x2E:  //LD L, byte
                L = MMU.readAddress(PC);
                PC++;
                break;
//MVI		M,byte		LD		(HL),byte		36byte		(HL) <- byte
//STAX		B		LD		(BC),A		02		(BC) <- A
//STAX		D		LD		(DE),A		12		(DE) <- A
//STA		word		LD		(word),A		32word		(word) <- A
            case 0x36:  //LD (HL), byte
                word = makeWord(H, L);
                byte1 = MMU.readAddress(PC);
                MMU.writeAddress(word, byte1);
                PC++;
                break;
            case 0x02:  //LD (BC), A
                word = makeWord(B, C);
                MMU.writeAddress(word, A);
                break;
            case 0x12:  //LD (DE), A
                word = makeWord(D, E);
                MMU.writeAddress(word, A);
                break;
            case 0x32:  //LD (word), A
                byte1 = MMU.readAddress(PC);
                byte2 = MMU.readAddress(PC + 1);
                word = makeWord(byte1, byte2);
                PC += 2;
                MMU.writeAddress(word, A);
                break;
//16 Bit Transfer Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//LXI		B,word		LD		BC,word		01word		BC <- word
//LXI		D,word		LD		DE,word		11word		DE <- word
//LXI		H,word		LD		HL,word		21word		HL <- word
//LXI		SP,word		LD		SP,word		31word		SP <- word
//LHLD		word		LD		HL,(word)		2Aword		HL <- (word)
//SHLD		word		LD		(word),HL		22word		(word) <- HL
//SPHL		LD		SP,HL		F9		SP <- HL
            case 0x01:  //LD BC, word
                B = MMU.readAddress(PC);
                PC++;
                C = MMU.readAddress(PC);
                PC++;
                break;
            case 0x11:  //LD DE, word
                D = MMU.readAddress(PC);
                PC++;
                E = MMU.readAddress(PC);
                PC++;
                break;
            case 0x21:  //LD HL, word
                H = MMU.readAddress(PC);
                PC++;
                L = MMU.readAddress(PC);
                PC++;
                break;
            case 0x31:  //LD SP, word
                byte1 = MMU.readAddress(PC);
                byte2 = MMU.readAddress(PC + 1);
                SP = makeWord(byte1, byte2);
                PC += 2;
                break;
            case 0x2A:  //LDI A, (HL)
                word = makeWord(H, L);
                A = MMU.readAddress(word);
                L++;
                break;
            case 0x22:  //LDI (HL), A
                word = makeWord(H, L);
                MMU.writeAddress(word, A);
                L++;
                break;
            case 0xF9:  //LD SP, HL
                word = makeWord(H, L);
                SP = word;
                break;
//Register Exchange Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//---		EX		AF,AF'		08		AF <-> AF'
//---		EXX		D9		BC/DE/HL <-> BC'/DE'/HL'
            case 0x08:  //LD (word), SP
                word = MMU.readAddress(PC);
                PC++;
                MMU.writeAddress(word, (short) (SP >> 8));    //write high byte
                word = MMU.readAddress(PC);
                PC++;
                MMU.writeAddress(word, (short) (SP & 0x00FF));    //write low byte
                break;
//Add Byte Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//ADD		A		ADD		A,A		87		A <- A + A
//ADD		B		ADD		A,B		80		A <- A + B
//ADD		C		ADD		A,C		81		A <- A + C
//ADD		D		ADD		A,D		82		A <- A + D
//ADD		E		ADD		A,E		83		A <- A + E
//ADD		H		ADD		A,H		84		A <- A + H
//ADD		L		ADD		A,L		85		A <- A + L
//ADD		M		ADD		A,(HL)		86		A <- A + (HL)
//ADI		byte		ADD		A,byte		C6byte		A <- A + byte
            case 0x87:  //ADD A, A
                A = add(A, A);
                break;
            case 0x80:  //ADD A, B
                A = add(A, B);
                break;
            case 0x81:  //ADD A, C
                A = add(A, C);
                subtract = false;
                break;
            case 0x82:  //ADD A, D
                A = add(A, D);
                subtract = false;
                break;
            case 0x83:  //ADD A, E
                A = add(A, E);
                subtract = false;
                break;
            case 0x84:  //ADD A, H
                A = add(A, H);
                subtract = false;
                break;
            case 0x85:  //ADD A, L
                A = add(A, L);
                subtract = false;
                break;
            case 0x86:  //ADD A, (HL)
                word = MMU.readAddress(makeWord(H, L));
                A = add(A, (short) word);
                subtract = false;
                break;
            case 0xC6:  //ADD A, byte
                word = MMU.readAddress(PC);
                PC++;
                A = add(A, (short) word);
                break;
//Add Byte with Carry-In Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//ADC		A		ADC		A,A		8F		A <- A + A + Carry
//ADC		B		ADC		A,B		88		A <- A + B + Carry
//ADC		C		ADC		A,C		89		A <- A + C + Carry
//ADC		D		ADC		A,D		8A		A <- A + D + Carry
//ADC		E		ADC		A,E		8B		A <- A + E + Carry
//ADC		H		ADC		A,H		8C		A <- A + H + Carry
//ADC		L		ADC		A,L		8D		A <- A + L + Carry
//ADC		M		ADC		A,(HL)		8E		A <- A + (HL) + Carry
//ACI		byte		ADC		A,byte		CEbyte		A <- A + byte + Carry
            case 0x8F:  //ADC A, A
                A = adc(A, A);
                break;
            case 0x88:  //ADC A, B
                A = adc(A, B);
                break;
            case 0x89:  //ADC A, C
                A = adc(A, C);
                break;
            case 0x8A:  //ADC A, D
                A = adc(A, D);
                break;
            case 0x8B:  //ADC A, E
                A = adc(A, E);
                break;
            case 0x8C:  //ADC A, H
                A = adc(A, H);
                break;
            case 0x8D:  //ADC A, L
                A = adc(A, L);
                break;
            case 0x8E:  //ADC A, (HL)
                byte1 = MMU.readAddress(makeWord(H, L));
                A = adc(A, byte1);
                break;
            case 0xCE:  //ADC A, byte
                byte1 = MMU.readAddress(PC);
                PC++;
                A = adc(A, byte1);
                break;
//Subtract Byte Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//SUB		A		SUB		A		97		A <- A - A
//SUB		B		SUB		B		90		A <- A - B
//SUB		C		SUB		C		91		A <- A - C
//SUB		D		SUB		D		92		A <- A - D
//SUB		E		SUB		E		93		A <- A - E
//SUB		H		SUB		H		94		A <- A - H
//SUB		L		SUB		L		95		A <- A - L
//SUB		M		SUB		(HL)		96		A <- A - (HL)
//SUI		byte		SUB		byte		D6byte		A <- A - byte
            case 0x97:  //SUB A
                sub(A);
                break;
            case 0x90:  //SUB B
                sub(B);
                break;
            case 0x91:  //SUB C
                sub(C);
                break;
            case 0x92:  //SUB D
                sub(D);
                break;
            case 0x93:  //SUB E
                sub(E);
                break;
            case 0x94:  //SUB H
                sub(H);
                break;
            case 0x95:  //SUB L
                sub(L);
                break;
            case 0x96:  //SUB (HL)
                byte1 = MMU.readAddress(makeWord(H, L));
                sub(byte1);
                break;
            case 0xD6:  //SUB byte
                byte1 = MMU.readAddress(PC);
                PC++;
                sub(byte1);
                break;
//Subtract Byte With Borrow-In Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//SBB		A		SBC		A		9F		A <- A - A - Carry
//SBB		B		SBC		B		98		A <- A - B - Carry
//SBB		C		SBC		C		99		A <- A - C - Carry
//SBB		D		SBC		D		9A		A <- A - D - Carry
//SBB		E		SBC		E		9B		A <- A - E - Carry
//SBB		H		SBC		H		9C		A <- A - H - Carry
//SBB		L		SBC		L		9D		A <- A - L - Carry
//SBB		M		SBC		(HL)		9E		A <- A - (HL) - Carry
//SBI		byte		SBC		byte		DEbyte		A <- A - byte - Carry
            case 0x9F:  //SBC A
                sbc(A);
                break;
            case 0x98:  //SBC B
                sbc(B);
                break;
            case 0x99:  //SBC C
                sbc(C);
                break;
            case 0x9A:  //SBC D
                sbc(D);
                break;
            case 0x9B:  //SBC E
                sbc(E);
                break;
            case 0x9C:  //SBC H
                sbc(H);
                break;
            case 0x9D:  //SBC L
                sbc(L);
                break;
            case 0x9E:  //SBC (HL)
                byte1 = MMU.readAddress(makeWord(H, L));
                sbc(byte1);
                break;
            case 0xDE:  //SBC byte
                byte1 = MMU.readAddress(PC);
                PC++;
                sbc(byte1);
                break;
//Double Byte Add Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//DAD		B		ADD		HL,BC		09		HL <- HL + BC
//DAD		D		ADD		HL,DE		19		HL <- HL + DE
//DAD		H		ADD		HL,HL		29		HL <- HL + HL
//DAD		SP		ADD		HL,SP		39		HL <- HL + SP
            case 0x09:  //ADD HL, BC
                word = wordAdd(makeWord(H, L), makeWord(B, C));
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
            case 0x19:  //ADD HL, DE
                word = wordAdd(makeWord(H, L), makeWord(D, E));
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
            case 0x29:  //ADD HL, HL
                word = wordAdd(makeWord(H, L), makeWord(H, L));
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
            case 0x39:  //ADD HL, SP
                word = wordAdd(makeWord(H, L), SP);
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
//Control Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//DI		DI		F3		IFF <- 0
//EI		EI		FB		IFF <- 1
//NOP		NOP		00		No Operation
//HLT		HLT		76		NOP;PC <- PC-1
            case 0xF3:  //DI
                IER = false;
                break;
            case 0xFB:  //EI
                IER = true;
                break;
            case 0x00:  //NOP
                break;
            case 0x76:  //HLT
                //PC--;
                GBDebug.log("GBCPU.processInstruction - @case 0x76: looping not implemented yet");
                break;
//Increment Byte Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//INR		A		INC		A		3C		A <- A + 1
//INR		B		INC		B		04		B <- B + 1
//INR		C		INC		C		0C		C <- C + 1
//INR		D		INC		D		14		D <- D + 1
//INR		E		INC		E		1C		E <- E + 1
//INR		H		INC		H		24		H <- H + 1
//INR		L		INC		L		2C		L <- L + 1
//INR		M		INC		(HL)		34		(HL) <- (HL) + 1
            case 0x3C:  //INC A
                A = inc(A);
                break;
            case 0x04:  //INC B
                B = inc(B);
                break;
            case 0x0C:  //INC C
                C = inc(C);
                break;
            case 0x14:  //INC D
                D = inc(D);
                break;
            case 0x1C:  //INC E
                E = inc(E);
                break;
            case 0x24:  //INC H
                H = inc(H);
                break;
            case 0x2C:  //INC L
                L = inc(L);
                break;
            case 0x34:  //INC (HL)
                word = makeWord(H, L);
                byte1 = inc(MMU.readAddress(word));
                MMU.writeAddress(makeWord(H, L), byte1);
//Decrement Byte Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//DCR		A		DEC		A		3D		A <- A - 1
//DCR		B		DEC		B		05		B <- B - 1
//DCR		C		DEC		C		0D		C <- C - 1
//DCR		D		DEC		D		15		D <- D - 1
//DCR		E		DEC		E		1D		E <- E - 1
//DCR		H		DEC		H		25		H <- H - 1
//DCR		L		DEC		L		2D		L <- L - 1
//DCR		M		DEC		(HL)		35		(HL) <- (HL) - 1
            case 0x3D:  //DEC A
                A = dec(A);
                break;
            case 0x05:  //DEC B
                B = dec(B);
                break;
            case 0x0D:  //DEC C
                C = dec(C);
                break;
            case 0x15:  //DEC D
                D = dec(D);
                break;
            case 0x1D:  //DEC E
                E = dec(E);
                break;
            case 0x25:  //DEC H
                H = dec(H);
                break;
            case 0x2D:  //DEC L
                L = dec(L);
                break;
            case 0x35:  //DEC (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                byte1 = inc(byte1);
                MMU.writeAddress(word, byte1);
//Increment Register Pair Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//INX		B		INC		BC		03		BC <- BC + 1
//INX		D		INC		DE		13		DE <- DE + 1
//INX		H		INC		HL		23		HL <- HL + 1
//INX		SP		INC		SP		33		SP <- SP + 1
            case 0x03:  //INC BC
                word = wordInc(makeWord(B, C));
                B = (short) (word >> 8);
                C = (short) (word & 0x00FF);
                break;
            case 0x13:  //INC DE
                word = wordInc(makeWord(D, E));
                D = (short) (word >> 8);
                E = (short) (word & 0x00FF);
                break;
            case 0x23:  //INC HL
                word = wordInc(makeWord(H, L));
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
            case 0x33:  //INC SP
                SP = wordInc(SP);
                break;
//Decrement Register Pair Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//DCX		B		DEC		BC		0B		BC <- BC - 1
//DCX		D		DEC		DE		1B		DE <- DE - 1
//DCX		H		DEC		HL		2B		HL <- HL - 1
//DCX		SP		DEC		SP		3B		SP <- SP - 1
            case 0x0B:  //DEC BC
                word = wordDec(makeWord(B, C));
                B = (short) (word >> 8);
                C = (short) (word & 0x00FF);
                break;
            case 0x1B:  //DEC DE
                word = wordDec(makeWord(D, E));
                D = (short) (word >> 8);
                E = (short) (word & 0x00FF);
                break;
            case 0x2B:  //DEC HL
                word = wordDec(makeWord(H, L));
                H = (short) (word >> 8);
                L = (short) (word & 0x00FF);
                break;
            case 0x3B:  //DEC SP
                SP = wordDec(SP);
                break;
//Special Accumulator and Flag Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//DAA		DAA		27		---	
//CMA		CPL		2F		A <- NOT A
//STC		SCF		37		CF (Carry Flag) <- 1
//CMC		CCF		3F		CF (Carry Flag) <- NOT CF
            case 0x27:  //DAA
                DAA = true;
                break;
            case 0x2F:  //CPL
                A = (short) (A ^ 0xFF); //negate A
                halfCarry = true;
                subtract = true;
                break;
            case 0x37:  //SCF
                carry = true;
                halfCarry = false;
                subtract = false;
                break;
            case 0x3F:  //CCF
                carry = !carry;
                halfCarry = !halfCarry;
                subtract = false;
                break;
//Rotate Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//RLC		RLCA		07		---	
//RRC		RRCA		0F		---	
//RAL		RLA		17		---	
//RAR		RRA		1F		---	
//---		RLC		A		CB07		---	
//---		RLC		B		CB00		---	
//---		RLC		C		CB01		---	
//---		RLC		D		CB02		---	
//---		RLC		E		CB03		---	
//---		RLC		H		CB04		---	
//---		RLC		L		CB05		---	
//---		RLC		(HL)		CB06		---		
//---		RL		A		CB17		---	
//---		RL		B		CB10		---	
//---		RL		C		CB11		---	
//---		RL		D		CB12		---		
//---		RL		E		CB13		---	
//---		RL		H		CB14		---	
//---		RL		L		CB15		---	
//---		RL		(HL)		CB16		---	
//---		RRC		A		CB0F		---	
//---		RRC		B		CB08		---	
//---		RRC		C		CB09		---	
//---		RRC		D		CB0A		---	
//---		RRC		E		CB0B		---	
//---		RRC		H		CB0C		---	
//---		RRC		L		CB0D		---	
//---		RRC		(HL)		CB0E		---		
//---		RL		A		CB1F		---	
//---		RL		B		CB18		---	
//---		RL		C		CB19		---	
//---		RL		D		CB1A		---	
//---		RL		E		CB1B		---	
//---		RL		H		CB1C		---	
//---		RL		L		CB1D		---	
//---		RL		(HL)		CB1E		---	
            case 0x07:  //RLCA
                bool = zero;
                A = rlc(A);
                zero = bool;
                break;
            case 0x0F:  //RRCA
                bool = zero;
                A = rrc(A);
                zero = bool;
                break;
            case 0x17:  //RLA
                bool = zero;
                A = rl(A);
                zero = bool;
                break;
            case 0x1F:  //RRA
                bool = zero;
                A = rr(A);
                zero = bool;
                break;
//Logical Byte Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//ANA		A		AND		A		A7		A <- A AND A
//ANA		B		AND		B		A0		A <- A AND B
//ANA		C		AND		C		A1		A <- A AND C
//ANA		D		AND		D		A2		A <- A AND D
//ANA		E		AND		E		A3		A <- A AND E
//ANA		H		AND		H		A4		A <- A AND H
//ANA		L		AND		L		A5		A <- A AND L
//ANA		M		AND		(HL)		A6		A <- A AND (HL)
//ANI		byte		AND		byte		E6byte		A <- A AND byte
            case 0xA7:  //AND A
                and(A);
                break;
            case 0xA0:  //AND B
                and(B);
                break;
            case 0xA1:  //AND C
                and(C);
                break;
            case 0xA2:  //AND D
                and(D);
                break;
            case 0xA3:  //AND E
                and(E);
                break;
            case 0xA4:  //AND H
                and(H);
                break;
            case 0xA5:  //AND L
                and(L);
                break;
            case 0xA6:  //AND (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                and(byte1);
                break;
            case 0xE6:  //AND byte
                byte1 = MMU.readAddress(PC);
                PC++;
                and(byte1);
                break;
//XRA		A		XOR		A		AF		A <- A XOR A
//XRA		B		XOR		B		A8		A <- A XOR B
//XRA		C		XOR		C		A9		A <- A XOR C
//XRA		D		XOR		D		AA		A <- A XOR D
//XRA		E		XOR		E		AB		A <- A XOR E
//XRA		H		XOR		H		AC		A <- A XOR H
//XRA		L		XOR		L		AD		A <- A XOR L
//XRA		M		XOR		(HL)		AE		A <- A XOR (HL)
//XRI		byte		XOR		byte		EEbyte		A <- A XOR byte\\
            case 0xAF:  //XOR A
                xor(A);
                break;
            case 0xA8:  //XOR B
                xor(B);
                break;
            case 0xA9:  //XOR C
                xor(C);
                break;
            case 0xAA:  //XOR D
                xor(D);
                break;
            case 0xAB:  //XOR E
                xor(E);
                break;
            case 0xAC:  //XOR H
                xor(H);
                break;
            case 0xAD:  //XOR L
                xor(L);
                break;
            case 0xAE:  //XOR (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                xor(byte1);
                break;
            case 0xEE:  //XOR byte
                byte1 = MMU.readAddress(PC);
                PC++;
                xor(byte1);
                break;
//ORA		A		OR		A		B7		A <- A OR A
//ORA		B		OR		B		B0		A <- A OR B
//ORA		C		OR		C		B1		A <- A OR C
//ORA		D		OR		D		B2		A <- A OR D
//ORA		E		OR		E		B3		A <- A OR E
//ORA		H		OR		H		B4		A <- A OR H
//ORA		L		OR		L		B5		A <- A OR L
//ORA		M		OR		(HL)		B6		A <- A OR (HL)
//ORI		byte		OR		byte		F6byte		A <- A OR byte
            case 0xB7:  //OR A
                or(A);
                break;
            case 0xB0:  //OR B
                or(B);
                break;
            case 0xB1:  //OR C
                or(C);
                break;
            case 0xB2:  //OR D
                or(D);
                break;
            case 0xB3:  //OR E
                or(E);
                break;
            case 0xB4:  //OR H
                or(H);
                break;
            case 0xB5:  //OR L
                or(L);
                break;
            case 0xB6:  //OR (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                or(byte1);
                break;
            case 0xF6:  //OR byte
                byte1 = MMU.readAddress(PC);
                PC++;
                or(byte1);
                break;
//CMP		A		CP		A		BF		A - A
//CMP		B		CP		B		B8		A - B
//CMP		C		CP		C		B9		A - C
//CMP		D		CP		D		BA		A - D
//CMP		E		CP		E		BB		A - E
//CMP		H		CP		H		BC		A - H
//CMP		L		CP		L		BD		A - L
//CMP		M		CP		(HL)		BE		A - (HL)
//CPI		byte		CP		byte		FEbyte		A - byte
            case 0xBF:  //CP A
                cp(A);
                break;
            case 0xB8:  //CP B
                cp(B);
                break;
            case 0xB9:  //CP C
                cp(C);
                break;
            case 0xBA:  //CP D
                cp(D);
                break;
            case 0xBB:  //CP E
                cp(E);
                break;
            case 0xBC:  //CP H
                cp(H);
                break;
            case 0xBD:  //CP L
                cp(L);
                break;
            case 0xBE:  //CP (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                cp(byte1);
                break;
            case 0xFE:  //CP byte
                byte1 = MMU.readAddress(PC);
                PC++;
                cp(byte1);
                break;
//Branch Control/Program Counter Load Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//JMP		word		JP		word		C3address		PC <- word
//JNZ		word		JP		NZ,word		C2address 	If NZ, PC <- word
//JZ		word		JP		Z,word		CAaddress		If Z, PC <- word
//JNC		word		JP		NC,word		D2address		If NC, PC <- word
//JC		word		JP		C,word		DAaddress		If C, PC <- word
//JPO		word		JP		PO,word		E2address		If PO, PC <- word
//JPE		word		JP		PE,word		EAaddress		If PE, PC <- word
//JP		word		JP		P,word		F2address		If P, PC <- word
//JM		word		JP		M,word		FAaddress		If M, PC <- word
            case 0xC3:  //JP word
                jp();
                break;
            case 0xC2:  //JP NZ, word
                if (!zero) {
                    jp();
                }
                break;
            case 0xCA:  //JP Z, word
                if (zero) {
                    jp();
                }
                break;
            case 0xD2:  //JP NC, word
                if (!carry) {
                    jp();
                }
                break;
            case 0xDA:  //JP C, word
                if (carry) {
                    jp();
                }
                break;
//PCHL		JP		(HL)		E9		PC <- HL
//---		JR		index		18index		PC <- PC + index
//---		JR		NZ,index		20index		If NZ, PC <- PC + index
//---		JR		Z,index		28index		If Z, PC <- PC + index
//---		JR		NC,index		30index		If NC, PC <- PC + index
//---		JR		C,index		38index		If C, PC <- PC + index
            case 0xE9:  //JP (HL)
                word = makeWord(H, L);
                byte1 = MMU.readAddress(word);
                PC = byte1;
                break;
            case 0x18:  //JR index
                jr();
                break;
            case 0x20:  //JR NZ, index
                if (!zero) {
                    jr();
                }
                break;
            case 0x28:  //JR Z, index
                if (zero) {
                    jr();
                }
                break;
            case 0x30:  //JR NC, index
                if (!carry) {
                    jr();
                }
                break;
            case 0x38:  //JR C, index
                if (carry) {
                    jr();
                }
                break;
//---		DJNZ		index		10index		B <- B - 1; while B > 0, PC <- PC + index
//CALL		word		CALL		word		CDaddress	(SP-1) <- PCh;(SP-2) <- PCl; SP <- SP - 2;PC <- word
//CNZ		word		CALL		NZ,word		C4address		If NZ, CALL word
//CZ		word		CALL		Z,word		CCaddress		If Z, CALL word
//CNC		word		CALL		NC,word		D4address		If NC, CALL word
//CC		word		CALL		C,word		DCaddress		If C, CALL word
//CPO		word		CALL		PO,word		E4address		If PO, CALL word
//CP		word		CALL		P,word		F4address		If P, CALL word
                
//RET		RET		C9		PCl <- (SP);PCh <- (SP+1); SP <- (SP+2)
//RNZ		RET		NZ		C0		If NZ, RET
//RZ		RET		Z		C8		If Z, RET
//RNC		RET		NC		D0		If NC, RET
//RC		RET		C		D8		If C, RET
//RPO		RET		PO		E0		If PO, RET
//RPE		RET		PE		E8		If PE, RET
//RP		RET		P		F0		If P, RET
//RM		RET		M		F8		If M, RET
//---		RETI		ED4D		Return from Interrupt
//---		RETN		ED45		IFF1 <- IFF2;RETI
//RST		0		RST		0		C7		CALL 0
//RST		1		RST		8		CF		CALL 8
//RST		2		RST		10H		D7		CALL 10H
//RST		3		RST		18H		DF		CALL 18H
//RST		4		RST		20H		E7		CALL 20H
//RST		5		RST		28H		EF		CALL 28H
//RST		6		RST		30H		F7		CALL 30H
//RST		7		RST		38H		FF		CALL 38H
//Stack Operation Instructions
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//PUSH		B		PUSH		BC		C5		(SP-2) <- C; (SP-1) <- B; SP <- SP - 2
//PUSH		D		PUSH		DE		D5		(SP-2) <- E; (SP-1) <- D; SP <- SP - 2
//PUSH		H		PUSH		HL		E5		(SP-2) <- L; (SP-1) <- H; SP <- SP - 2
//PUSH		PSW		PUSH		AF		F5		(SP-2) <- Flags; (SP-1) <- A; SP <- SP - 2
//POP		B		POP		BC		C1		B <- (SP+1); C <- (SP); SP <- SP + 2
//POP		D		POP		DE		D1		D <- (SP+1); E <- (SP); SP <- SP + 2
//POP		H		POP		HL		E1		H <- (SP+1); L <- (SP); SP <- SP + 2
//POP		PSW		POP		AF		F1		A <- (SP+1); Flags <- (SP); SP <- SP + 2
//Bit Manipulation Instructions (Z80 Only)
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//---		BIT		0,A		CB47		Z flag <- NOT Bit 0
//---		BIT		0,B		CB40		Z flag <- NOT Bit 0
//---		BIT		0,C		CB41		Z flag <- NOT Bit 0
//---		BIT		0,D		CB42		Z flag <- NOT Bit 0
//---		BIT		0,E		CB43		Z flag <- NOT Bit 0
//---		BIT		0,H		CB44		Z flag <- NOT Bit 0
//---		BIT		0,L		CB45		Z flag <- NOT Bit 0
//---		BIT		0,(HL)		CB46		Z flag <- NOT Bit 0
//---		BIT		1,A		CB4F		Z flag <- NOT Bit 1
//---		BIT		1,B		CB48		Z flag <- NOT Bit 1
//---		BIT		1,C		CB49		Z flag <- NOT Bit 1
//---		BIT		1,D		CB4A		Z flag <- NOT Bit 1
//---		BIT		1,E		CB4B		Z flag <- NOT Bit 1
//---		BIT		1,H		CB4C		Z flag <- NOT Bit 1
//---		BIT		1,L		CB4D		Z flag <- NOT Bit 1
//---		BIT		1,(HL)		CB4E		Z flag <- NOT Bit 1
//---		BIT		2,A		CB57		Z flag <- NOT Bit 2
//---		BIT		2,B		CB50		Z flag <- NOT Bit 2
//---		BIT		2,C		CB51		Z flag <- NOT Bit 2
//---		BIT		2,D		CB52		Z flag <- NOT Bit 2
//---		BIT		2,E		CB53		Z flag <- NOT Bit 2
//---		BIT		2,H		CB54		Z flag <- NOT Bit 2
//---		BIT		2,L		CB55		Z flag <- NOT Bit 2
//---		BIT		2,(HL)		CB56		Z flag <- NOT Bit 2
//---		BIT		3,A		CB5F		Z flag <- NOT Bit 3
//---		BIT		3,B		CB58		Z flag <- NOT Bit 3
//---		BIT		3,C		CB59		Z flag <- NOT Bit 3
//---		BIT		3,D		CB5A		Z flag <- NOT Bit 3
//---		BIT		3,E		CB5B		Z flag <- NOT Bit 3
//---		BIT		3,H		CB5C		Z flag <- NOT Bit 3
//---		BIT		3,L		CB5D		Z flag <- NOT Bit 3
//---		BIT		3,(HL)		CB5E		Z flag <- NOT Bit 3
//---		BIT		4,A		CB67		Z flag <- NOT Bit 4
//---		BIT		4,B		CB60		Z flag <- NOT Bit 4
//---		BIT		4,C		CB61		Z flag <- NOT Bit 4
//---		BIT		4,D		CB62		Z flag <- NOT Bit 4
//---		BIT		4,E		CB63		Z flag <- NOT Bit 4
//---		BIT		4,H		CB64		Z flag <- NOT Bit 4
//---		BIT		4,L		CB65		Z flag <- NOT Bit 4
//---		BIT		4,(HL)		CB66		Z flag <- NOT Bit 4
//---		BIT		5,A		CB6F		Z flag <- NOT Bit 5
//---		BIT		5,B		CB68		Z flag <- NOT Bit 5
//---		BIT		5,C		CB69		Z flag <- NOT Bit 5
//---		BIT		5,D		CB6A		Z flag <- NOT Bit 5
//---		BIT		5,E		CB6B		Z flag <- NOT Bit 5
//---		BIT		5,H		CB6C		Z flag <- NOT Bit 5
//---		BIT		5,L		CB6D		Z flag <- NOT Bit 5
//---		BIT		5,(HL)		CB6E		Z flag <- NOT Bit 5
//---		BIT		6,A		CB77		Z flag <- NOT Bit 6
//---		BIT		6,B		CB70		Z flag <- NOT Bit 6
//---		BIT		6,C		CB71		Z flag <- NOT Bit 6
//---		BIT		6,D		CB72		Z flag <- NOT Bit 6
//---		BIT		6,E		CB73		Z flag <- NOT Bit 6
//---		BIT		6,H		CB74		Z flag <- NOT Bit 6
//---		BIT		6,L		CB75		Z flag <- NOT Bit 6
//---		BIT		6,(HL)		CB76		Z flag <- NOT Bit 6
//---		BIT		7,A		CB7F		Z flag <- NOT Bit 7
//---		BIT		7,B		CB78		Z flag <- NOT Bit 7
//---		BIT		7,C		CB79		Z flag <- NOT Bit 7
//---		BIT		7,D		CB7A		Z flag <- NOT Bit 7
//---		BIT		7,E		CB7B		Z flag <- NOT Bit 7
//---		BIT		7,H		CB7C		Z flag <- NOT Bit 7
//---		BIT		7,L		CB7D		Z flag <- NOT Bit 7
//---		BIT		7,(HL)		CB7E		Z flag <- NOT Bit 7
//---		RES		0,A		CB87		Bit 0 <- 0
//---		RES		0,B		CB80		Bit 0 <- 0
//---		RES		0,C		CB81		Bit 0 <- 0
//---		RES		0,D		CB82		Bit 0 <- 0
//---		RES		0,E		CB83		Bit 0 <- 0
//---		RES		0,H		CB84		Bit 0 <- 0
//---		RES		0,L		CB85		Bit 0 <- 0
//---		RES		0,(HL)		CB86		Bit 0 <- 0
//---		RES		1,A		CB8F		Bit 1 <- 0
//---		RES		1,B		CB88		Bit 1 <- 0
//---		RES		1,C		CB89		Bit 1 <- 0
//---		RES		1,D		CB8A		Bit 1 <- 0
//---		RES		1,E		CB8B		Bit 1 <- 0
//---		RES		1,H		CB8C		Bit 1 <- 0
//---		RES		1,L		CB8D		Bit 1 <- 0
//---		RES		1,(HL)		CB8E		Bit 1 <- 0
//---		RES		2,A		CB97		Bit 2 <- 0
//---		RES		2,B		CB90		Bit 2 <- 0
//---		RES		2,C		CB91		Bit 2 <- 0
//---		RES		2,D		CB92		Bit 2 <- 0
//---		RES		2,E		CB93		Bit 2 <- 0
//---		RES		2,H		CB94		Bit 2 <- 0
//---		RES		2,L		CB95		Bit 2 <- 0
//---		RES		2,(HL)		CB96		Bit 2 <- 0
//---		RES		3,A		CB9F		Bit 3 <- 0
//---		RES		3,B		CB98		Bit 3 <- 0
//---		RES		3,C		CB99		Bit 3 <- 0
//---		RES		3,D		CB9A		Bit 3 <- 0
//---		RES		3,E		CB9B		Bit 3 <- 0
//---		RES		3,H		CB9C		Bit 3 <- 0
//---		RES		3,L		CB9D		Bit 3 <- 0
//---		RES		3,(HL)		CB9E		Bit 3 <- 0
//---		RES		4,A		CBA7		Bit 4 <- 0
//---		RES		4,B		CBA0		Bit 4 <- 0
//---		RES		4,C		CBA1		Bit 4 <- 0
//---		RES		4,D		CBA2		Bit 4 <- 0
//---		RES		4,E		CBA3		Bit 4 <- 0
//---		RES		4,H		CBA4		Bit 4 <- 0
//---		RES		4,L		CBA5		Bit 4 <- 0
//---		RES		4,(HL)		CBA6		Bit 4 <- 0
//---		RES		5,A		CBAF		Bit 5 <- 0
//---		RES		5,B		CBA8		Bit 5 <- 0
//---		RES		5,C		CBA9		Bit 5 <- 0
//---		RES		5,D		CBAA		Bit 5 <- 0
//---		RES		5,E		CBAB		Bit 5 <- 0
//---		RES		5,H		CBAC		Bit 5 <- 0
//---		RES		5,L		CBAD		Bit 5 <- 0
//---		RES		5,(HL)		CBAE		Bit 5 <- 0
//---		RES		6,A		CBB7		Bit 6 <- 0
//---		RES		6,B		CBB0		Bit 6 <- 0
//---		RES		6,C		CBB1		Bit 6 <- 0
//---		RES		6,D		CBB2		Bit 6 <- 0
//---		RES		6,E		CBB3		Bit 6 <- 0
//---		RES		6,H		CBB4		Bit 6 <- 0
//---		RES		6,L		CBB5		Bit 6 <- 0
//---		RES		6,(HL)		CBB6		Bit 6 <- 0
//---		RES		7,A		CBBF		Bit 7 <- 0
//---		RES		7,B		CBB8		Bit 7 <- 0
//---		RES		7,C		CBB9		Bit 7 <- 0
//---		RES		7,D		CBBA		Bit 7 <- 0
//---		RES		7,E		CBBB		Bit 7 <- 0
//---		RES		7,H		CBBC		Bit 7 <- 0
//---		RES		7,L		CBBD		Bit 7 <- 0
//---		RES		7,(HL)		CBBE		Bit 7 <- 0
//---		SET		0,A		CBC7		Bit 0 <- 1
//---		SET		0,B		CBC0		Bit 0 <- 1
//---		SET		0,C		CBC1		Bit 0 <- 1
//---		SET		0,D		CBC2		Bit 0 <- 1
//---		SET		0,E		CBC3		Bit 0 <- 1
//---		SET		0,H		CBC4		Bit 0 <- 1
//---		SET		0,L		CBC5		Bit 0 <- 1
//---		SET		0,(HL)		CBC6		Bit 0 <- 1
//---		SET		1,A		CBCF		Bit 1 <- 1
//---		SET		1,B		CBC8		Bit 1 <- 1
//---		SET		1,C		CBC9		Bit 1 <- 1
//---		SET		1,D		CBCA		Bit 1 <- 1
//---		SET		1,E		CBCB		Bit 1 <- 1
//---		SET		1,H		CBCC		Bit 1 <- 1
//---		SET		1,L		CBCD		Bit 1 <- 1
//---		SET		1,(HL)		CBCE		Bit 1 <- 1
//---		SET		2,A		CBD7		Bit 2 <- 1
//---		SET		2,B		CBD0		Bit 2 <- 1
//---		SET		2,C		CBD1		Bit 2 <- 1
//---		SET		2,D		CBD2		Bit 2 <- 1
//---		SET		2,E		CBD3		Bit 2 <- 1
//---		SET		2,H		CBD4		Bit 2 <- 1
//---		SET		2,L		CBD5		Bit 2 <- 1
//---		SET		2,(HL)		CBD6		Bit 2 <- 1
//---		SET		3,A		CBDF		Bit 3 <- 1
//---		SET		3,B		CBD8		Bit 3 <- 1
//---		SET		3,C		CBD9		Bit 3 <- 1
//---		SET		3,D		CBDA		Bit 3 <- 1
//---		SET		3,E		CBDB		Bit 3 <- 1
//---		SET		3,H		CBDC		Bit 3 <- 1
//---		SET		3,L		CBDD		Bit 3 <- 1
//---		SET		3,(HL)		CBDE		Bit 3 <- 1
//---		SET		4,A		CBE7		Bit 4 <- 1
//---		SET		4,B		CBE0		Bit 4 <- 1
//---		SET		4,C		CBE1		Bit 4 <- 1
//---		SET		4,D		CBE2		Bit 4 <- 1
//---		SET		4,E		CBE3		Bit 4 <- 1
//---		SET		4,H		CBE4		Bit 4 <- 1
//---		SET		4,L		CBE5		Bit 4 <- 1
//---		SET		4,(HL)		CBE6		Bit 4 <- 1
//---		SET		5,A		CBEF		Bit 5 <- 1
//---		SET		5,B		CBE8		Bit 5 <- 1
//---		SET		5,C		CBE9		Bit 5 <- 1
//---		SET		5,D		CBEA		Bit 5 <- 1
//---		SET		5,E		CBEB		Bit 5 <- 1
//---		SET		5,H		CBEC		Bit 5 <- 1
//---		SET		5,L		CBED		Bit 5 <- 1
//---		SET		5,(HL)		CBEE		Bit 5 <- 1
//---		SET		6,A		CBF7		Bit 6 <- 1
//---		SET		6,B		CBF0		Bit 6 <- 1
//---		SET		6,C		CBF1		Bit 6 <- 1
//---		SET		6,D		CBF2		Bit 6 <- 1
//---		SET		6,E		CBF3		Bit 6 <- 1
//---		SET		6,H		CBF4		Bit 6 <- 1
//---		SET		6,L		CBF5		Bit 6 <- 1
//---		SET		6,(HL)		CBF6		Bit 6 <- 1
//---		SET		7,A		CBFF		Bit 7 <- 1
//---		SET		7,B		CBF8		Bit 7 <- 1
//---		SET		7,C		CBF9		Bit 7 <- 1
//---		SET		7,D		CBFA		Bit 7 <- 1
//---		SET		7,E		CBFB		Bit 7 <- 1
//---		SET		7,H		CBFC		Bit 7 <- 1
//---		SET		7,L		CBFD		Bit 7 <- 1
//---		SET		7,(HL)		CBFE		Bit 7 <- 1
//Bit Shift Instructions (Z80 Only)
//8080 Mnemonic 	Z80 Mnemonic 	Machine Code 	Operation
//---		SLA		A		CB27		---	
//---		SLA		B		CB20		---	
//---		SLA		C		CB21		---	
//---		SLA		D		CB22		---	
//---		SLA		E		CB23		---	
//---		SLA		H		CB24		---	
//---		SLA		L		CB25		---	
//---		SLA		(HL)		CB26		---	
//---		SRA		A		CB2F		---	
//---		SRA		B		CB28		---	
//---		SRA		C		CB29		---	
//---		SRA		D		CB2A		---	
//---		SRA		E		CB2B		---	
//---		SRA		H		CB2C		---	
//---		SRA		L		CB2D		---	
//---		SRA		(HL)		CB2E		---	
//---		SRL		A		CB3F		---	
//---		SRL		B		CB38		---	
//---		SRL		C		CB39		---	
//---		SRL		D		CB3A		---	
//---		SRL		E		CB3B		---	
//---		SRL		H		CB3C		---	
//---		SRL		L		CB3D		---	
//---		SRL		(HL)		CB3E		---	
            default:
                ++errorCount;
                interrupt = new GBBadInstruction(String.format("0x%02X", instruction)
                        + " at address 0x" + String.format("%04X", PC - 1));
        }
    }

    private int makeWord(int hb, int lb) {
        int word = hb << 8;
        word += lb;
        return word;
    }

    private int wordAdd(int lword, int rword) {
        halfCarry = lword < 0x10FF && lword + rword >= 0x10FF;
        carry = lword + rword > 0xFFFF;
        lword += rword;
        lword %= 0x10000;
        subtract = false;

        return lword;
    }

    private int wordInc(int registerValue) {
        registerValue = ++registerValue % 0x10000;

        return registerValue;
    }

    private int wordDec(int registerValue) {
        registerValue = (--registerValue + 0x10000) % 0x10000;

        return registerValue;
    }

    private short add(short lbyte, short rbyte) {
        halfCarry = lbyte < 0x10 && lbyte + rbyte >= 0x10;  //lbyte uses at most 4 bits and lbyte + rbyte use at least 5 bits
        carry = lbyte + rbyte > 0xFF;   //lbyte + rbyte more than 8 bits
        lbyte += rbyte;
        lbyte %= 0x100; //overflows lbyte if necessary
        zero = lbyte == 0;
        subtract = false;

        return lbyte;
    }

    private short adc(short lbyte, short rbyte) {
        halfCarry = lbyte < 0x10 && lbyte + rbyte + (halfCarry == true ? 1 : 0) >= 0x10;
        carry = lbyte + rbyte + (halfCarry == true ? 1 : 0) > 0xFF;
        lbyte += rbyte + (halfCarry == true ? 1 : 0);
        lbyte %= 0x100;
        zero = lbyte == 0;
        subtract = false;

        return lbyte;
    }

    private void sub(short registerValue) {
        halfCarry = A > 0x0F && A - registerValue <= 0x0F;
        carry = A < registerValue;
        zero = A == registerValue;
        subtract = true;
        A = (short) ((0x100 + (A - registerValue)) % 0x100);
    }

    private void sbc(short registerValue) {
        halfCarry = A > 0x0F && A - registerValue - (halfCarry == true ? 1 : 0) <= 0x0F;
        carry = A < registerValue + (halfCarry == true ? 1 : 0);
        zero = A == registerValue + (halfCarry == true ? 1 : 0);
        subtract = true;
        A = (short) ((0x100 + (A - registerValue - (halfCarry == true ? 1 : 0))) % 0x100);
    }

    private short inc(short registerValue) {
        halfCarry = registerValue <= 0x0F && registerValue + 1 > 0x0F;
        subtract = false;
        zero = registerValue == 0xFF;
        registerValue = (short) (++registerValue % 0x100);

        return registerValue;
    }

    private short dec(short registerValue) {
        halfCarry = registerValue > 0x0F && registerValue - 1 <= 0x0F;
        subtract = true;
        zero = registerValue == 0x01;
        registerValue = (short) ((--registerValue + 0x100) % 0x100);

        return registerValue;
    }

    private short rl(short registerValue) {
        byte highBit = (byte) (registerValue >> 7); //right shift to get just the high bit
        registerValue <<= 1;    //left shift register
        registerValue &= 0xFF;  //mask out any bits that would cause register to be > 0xFF
        registerValue += carry == true ? 1 : 0; //put carry into low bit
        carry = highBit == 1;   //put high bit into carry
        zero = registerValue == 0;
        halfCarry = false;
        subtract = false;

        return registerValue;
    }

    private short rlc(short registerValue) {
        byte highBit = (byte) (registerValue >> 7);
        registerValue <<= 1;
        registerValue &= 0xFF;
        carry = highBit == 1;
        registerValue += carry == true ? 1 : 0;
        zero = registerValue == 0;
        halfCarry = false;
        subtract = false;

        return registerValue;
    }

    private short rr(short registerValue) {
        byte lowBit = (byte) (registerValue & 0x01);    //mask top 7 bits to get low bit
        registerValue >>= 1;    //right shift register
        registerValue &= 0x7F;  //mask high bit just in case
        registerValue |= (carry == true ? 1 : 0) << 7;  //put carry into high bit
        carry = lowBit == 1;    //put low bit into carry
        zero = registerValue == 0;
        halfCarry = false;
        subtract = false;

        return registerValue;
    }

    private short rrc(short registerValue) {
        byte lowBit = (byte) (registerValue & 0x01);
        registerValue >>= 1;
        registerValue &= 0x7F;
        registerValue |= lowBit << 7;
        carry = lowBit == 1;
        zero = registerValue == 0;
        halfCarry = false;
        subtract = false;

        return registerValue;
    }

    private void and(short registerValue) {
        carry = false;
        subtract = false;
        A &= registerValue;
        zero = A == 0;
    }

    private void xor(short registerValue) {
        carry = false;
        subtract = false;
        A ^= registerValue;
        zero = A == 0;
    }

    private void or(short registerValue) {
        carry = false;
        subtract = false;
        A |= registerValue;
        zero = A == 0;
    }

    private void cp(short registerValue) {
        short temp = A;
        sub(registerValue);
        A = temp;
    }

    private void jp() {
        short byte1 = MMU.readAddress(PC);
        PC++;
        short byte2 = MMU.readAddress(PC);
        PC++;
        int word = makeWord(byte1, byte2);
        PC = word;
    }

    private void jr() {
        byte byte1 = (byte) MMU.readAddress(PC);
        PC++;
        PC += byte1;
        GBDebug.log("GBCPU.jr - bug: may not be able to jump backwards");
    }

    private String coreDump() {
        String core = "SP: \t0x" + String.format("%04X", SP);
        core += "\nPC: \t0x" + String.format("%04X", PC);
        core += "\nA: \t0x" + String.format("%02X", A);
        core += "\nB: \t0x" + String.format("%02X", B);
        core += "\nC: \t0x" + String.format("%02X", C);
        core += "\nD: \t0x" + String.format("%02X", D);
        core += "\nE: \t0x" + String.format("%02X", E);
        core += "\nH: \t0x" + String.format("%02X", H);
        core += "\nL: \t0x" + String.format("%02X", L);
        short flags = 0x00;
        if (zero) {
            flags |= 0x01 << 7;
        }
        if (subtract) {
            flags |= 0x01 << 6;
        }
        if (halfCarry) {
            flags |= 0x01 << 5;
        }
        if (carry) {
            flags |= 0x01 << 4;
        }
        core += "\nF: \t0b" + byteToBinaryString(flags);
        return core;
    }

    private String byteToBinaryString(short data) {
        String bin = "";
        bin += (data & 0x80) >> 7;
        bin += (data & 0x40) >> 6;
        bin += (data & 0x20) >> 5;
        bin += (data & 0x10) >> 4;
        bin += "0000";
        return bin;
    }
}
