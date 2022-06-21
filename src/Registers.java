
public class Registers {
	private byte[] generalPurposeR = new byte[64];
	private boolean[] statusRegister = new boolean[8];
	private short programCounter;

	public byte[] getGeneralPurposeR() {
		return this.generalPurposeR;
	}

	public void changeRegVal(int regNum, byte val) {
		if (regNum < 0 || regNum > 63) {
			System.out.println("Invalid register number");
			System.out.println("Program terminated!!");
			System.exit(-1);
		}
		this.generalPurposeR[regNum] = val;
	}

	public void printReg(int regNum) {
		if (regNum < 0 || regNum > 63) {
			System.out.println("Invalid register number");
			System.out.println("Program terminated!!");
			System.exit(-1);
		}
		System.out.println(Integer.toBinaryString(this.generalPurposeR[regNum]));
	}

	public char getC() {
		return this.statusRegister[3] ? '1' : '0';
	}

	public char getV() {
		return this.statusRegister[4] ? '1' : '0';
	}

	public char getN() {
		return this.statusRegister[5] ? '1' : '0';
	}

	public char getS() {
		return this.statusRegister[6] ? '1' : '0';
	}

	public char getZ() {
		return this.statusRegister[7] ? '1' : '0';
	}

	public void setC(boolean carryVal) {
		this.statusRegister[3] = carryVal;
	}

	public void setV(boolean overflowVal) {
		this.statusRegister[4] = overflowVal;
	}

	public void setN(boolean negativeVal) {
		this.statusRegister[5] = negativeVal;
	}

	public void setS(boolean signVal) {
		this.statusRegister[6] = signVal;
	}

	public void setZ(boolean zeroVal) {
		this.statusRegister[7] = zeroVal;
	}

	public String printStatusReg() {
		String res = "";
		for (int i = 0; i < this.statusRegister.length; i++) {
			if (this.statusRegister[i]) {
				res += '1';
			} else {
				res += '0';
			}
		}
		return res;
	}

	public short getProgramCounter() {
		return this.programCounter;

	}

	public void setProgramCounter(short programCounter) {
		this.programCounter = programCounter;
	}

}
