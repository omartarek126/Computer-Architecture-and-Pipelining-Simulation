public class Processor {

	InstructionMemory instructionMemory;
	DataMemory dataMemory;
	Parser parser;
	Registers registers;
	String instrFetched = "";
	String toBeDecoded = "";
	String toBeExecuted = "";
	int[] toBeDecodedArr = new int[5];
	int[] decodedArr = new int[5];
	int loopingValue = 0;
	int clockCycle;
	byte prevValueReg;
	byte newValueReg;
	byte prevValueMem;
	byte newValueMem;
	int changedAddressValueMem;
	short prevPC;
	boolean flush;

	public Processor() {
		this.instructionMemory = new InstructionMemory();
		this.dataMemory = new DataMemory();
		this.parser = new Parser();
		this.registers = new Registers();
		this.clockCycle = 1;
	}

// NO GUI run
//	public static void main(String[] args) {
//		Processor processor = new Processor();
//		for (int i = 0; i < 3 + ((processor.instructionMemory.lastInsertedPointer) * 1); i++) {
//			String temp = processor.fetch();
//			processor.decode();
//			processor.execute();
//			processor.instrFetched = temp;
//			System.out.println(processor.printInfo());
//			processor.clockCycle++;
//			processor.toBeExecuted = processor.toBeDecoded;
//			processor.toBeDecoded = processor.instrFetched;
//		}
//		// Only blocks that are modified in data and instruction memory are printed
//		System.out.println(processor.printFinal());
//	}

	public String fetch() {
		if (this.instructionMemory.lastInsertedPointer >= this.registers.getProgramCounter()) {
			short currInst = this.instructionMemory.instrMem[this.registers.getProgramCounter()];
			String currInstBin = Integer.toBinaryString(currInst);
			this.registers.setProgramCounter((short) (this.registers.getProgramCounter() + 1));
			return this.instrFetched = currInstBin;
		}
		return "";
	}

	public void decode() {
		for (int j = 0; j < this.toBeDecodedArr.length; j++) {
			this.decodedArr[j] = this.toBeDecodedArr[j];
		}
		if (!this.toBeDecoded.equals("")) {
			while (this.toBeDecoded.length() < 16) {
				this.toBeDecoded = '0' + this.toBeDecoded;
			}
			if (this.toBeDecoded.length() > 16) {
				this.toBeDecoded = this.toBeDecoded.substring(16);
			}
			int opcode = Integer.parseInt(this.toBeDecoded.substring(0, 4), 2);
			int R1 = Integer.parseInt(this.toBeDecoded.substring(4, 10), 2);
			int R2 = Integer.parseInt(this.toBeDecoded.substring(10, 16), 2);
			byte valueR1 = this.registers.getGeneralPurposeR()[R1];
			byte valueR2 = this.registers.getGeneralPurposeR()[R2];
			String immediateStr = this.toBeDecoded.substring(10, 16);
			while (immediateStr.length() < 32) {
				immediateStr = immediateStr.charAt(0) + immediateStr;
			}
			int immediate = convertFromTwosComplement(immediateStr);
			this.toBeDecodedArr[0] = opcode;
			this.toBeDecodedArr[1] = R1;
			this.toBeDecodedArr[2] = valueR1;
			this.toBeDecodedArr[3] = valueR2;
			this.toBeDecodedArr[4] = immediate;
		}
	}

	public void execute() {
		if (!this.toBeExecuted.equals("")) {
			int opcode = this.decodedArr[0];
			int R1 = this.decodedArr[1];
			byte valueR1 = (byte) this.decodedArr[2];
			byte valueR2 = (byte) this.decodedArr[3];
			int immediate = this.decodedArr[4];
			byte result;
			switch (opcode) {
			case 0:
				result = this.add(valueR1, valueR2);
				this.writeBack(R1, result);
				break;
			case 1:
				result = this.subtract(valueR1, valueR2);
				this.writeBack(R1, result);
				break;
			case 2:
				result = this.multiply(valueR1, valueR2);
				this.writeBack(R1, result);
				break;
			case 3:
				this.moveImmediate(R1, (byte) immediate);
				break;
			case 4:
				this.branchIfEqualZero(valueR1, immediate);
				break;
			case 5:
				result = this.andImmediate(valueR1, immediate);
				this.writeBack(R1, result);
				break;
			case 6:
				result = this.exclusiveOr(valueR1, valueR2);
				this.writeBack(R1, result);
				break;
			case 7:
				this.branchRegister(valueR1, valueR2);
				break;
			case 8:
				result = this.shiftArithmeticLeft(valueR1, immediate);
				this.writeBack(R1, result);
				break;
			case 9:
				result = this.shiftArithmeticRight(valueR1, immediate);
				this.writeBack(R1, result);
				break;
			case 10:
				result = this.loadToRegister(immediate);
				this.writeBack(R1, result);
				break;
			case 11:
				this.storeFromRegister(valueR1, immediate);
				break;
			}
		}
	}

	public byte memory(String type, int address, byte regVal) {
		if (type.equals("Load"))
			return this.dataMemory.dataMem[address];
		this.changedAddressValueMem = address;
		this.prevValueMem = this.dataMemory.dataMem[address];
		this.dataMemory.usedIndices.add(address);
		this.newValueMem = regVal;
		this.dataMemory.dataMem[address] = regVal;
		return 0;

	}

	public void writeBack(int regNum, byte value) {
		this.prevValueReg = this.registers.getGeneralPurposeR()[regNum];
		this.registers.changeRegVal(regNum, value);
		this.newValueReg = value;
	}

	public byte add(byte param1, byte param2) {
		byte result = (byte) (param1 + param2);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);

		if ((param1 < 0 && param2 < 0) && result >= 0) {
			this.registers.setV(true);
		} else if ((param1 >= 0 && param2 >= 0) && result < 0) {
			this.registers.setV(true);
		} else {
			this.registers.setV(false);
		}

		boolean N = this.registers.getN() == '1' ? true : false;
		boolean V = this.registers.getV() == '1' ? true : false;
		this.registers.setS(N ^ V);

		int unsignedValR1 = param1 & 0x000000FF;
		int unsignedValR2 = param2 & 0x000000FF;
		int mask = 0b00000000000000000000000100000000;
		if (((unsignedValR1 + unsignedValR2) & mask) == mask) {
			this.registers.setC(true);
		} else {
			this.registers.setC(false);
		}

		return result;
	}

	public byte subtract(byte param1, byte param2) {
		byte result = (byte) (param1 - param2);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);

		if ((param1 >= 0 && param2 < 0) && result < 0) {
			this.registers.setV(true);
		} else if ((param1 < 0 && param2 >= 0) && result >= 0) {
			this.registers.setV(true);
		} else {
			this.registers.setV(false);
		}

		boolean N = this.registers.getN() == '1' ? true : false;
		boolean V = this.registers.getV() == '1' ? true : false;
		this.registers.setS(N ^ V);
		return result;
	}

	public byte multiply(byte param1, byte param2) {
		byte result = (byte) (param1 * param2);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);
		return result;
	}

	public void moveImmediate(int regNum, byte value) {
		this.writeBack(regNum, value);
	}

	public void branchIfEqualZero(byte regVal, int immediateVal) {
		if (regVal == 0) {
			this.prevPC = this.registers.getProgramCounter();
			this.flush = true;
			if (immediateVal < 0) {
				this.registers.setProgramCounter((short) (this.prevPC - 2 - (Math.abs(immediateVal) - 1)));
			} else {
				this.registers.setProgramCounter((short) (this.registers.getProgramCounter() + immediateVal - 2));
			}
			this.loopingValue += immediateVal - 2;
		}
	}

	public byte andImmediate(byte regVal, int immediateVal) {
		byte result = (byte) (regVal & immediateVal);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);
		return result;
	}

	public byte exclusiveOr(byte firstRegVal, byte secondRegVal) {
		byte result = (byte) (firstRegVal ^ secondRegVal);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);
		return result;
	}

	public void branchRegister(byte firstRegVal, byte secondRegVal) {
		// If || Means OR
//		this.prevPC = this.registers.getProgramCounter();
//		this.flush = true;
//		this.registers.setProgramCounter((short) (firstRegVal | secondRegVal));
//		this.loopingValue += (short) (firstRegVal | secondRegVal) - this.prevPC;

		// If || Means Concat
		String firstRegValBin = Integer.toBinaryString(firstRegVal);
		while (firstRegValBin.length() < 8) {
			firstRegValBin = '0' + firstRegValBin;
		}
		if (firstRegValBin.length() > 8) {
			firstRegValBin = firstRegValBin.substring(24);
		}
		String secondRegValBin = Integer.toBinaryString(secondRegVal);
		while (secondRegValBin.length() < 8) {
			secondRegValBin = '0' + secondRegValBin;
		}
		if (secondRegValBin.length() > 8) {
			secondRegValBin = secondRegValBin.substring(24);
		}
		String pcValBin = firstRegValBin + secondRegValBin;
		short newPC = (short) Integer.parseInt(pcValBin, 2);
		this.prevPC = this.registers.getProgramCounter();
		this.flush = true;
		this.registers.setProgramCounter(newPC);
		this.loopingValue += newPC - this.prevPC;
	}

	public byte shiftArithmeticLeft(byte regVal, int immediateVal) {
		byte result = (byte) (regVal << immediateVal);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);
		return result;
	}

	public byte shiftArithmeticRight(byte regVal, int immediateVal) {
		byte result = (byte) (regVal >> immediateVal);
		this.registers.setN(result < 0);
		this.registers.setZ(result == 0);
		return result;
	}

	public byte loadToRegister(int immediateVal) {
		return this.memory("Load", immediateVal, (byte) 0);
	}

	public void storeFromRegister(byte regVal, int immediateVal) {
		this.memory("Store", immediateVal, regVal);
	}

	public void flushPipeline() {
		this.toBeDecoded = "";
		this.instrFetched = "";
		for (int i = 0; i < this.toBeDecodedArr.length; i++) {
			this.toBeDecodedArr[i] = 0;
		}
		for (int i = 0; i < this.decodedArr.length; i++) {
			this.decodedArr[i] = 0;
		}
	}

	// ---------------------------------------------------------------------------------------------------------

	public String printInfo() {
		String pcStr = Integer.toBinaryString(this.registers.getProgramCounter() - 1);
		while (pcStr.length() < 16) {
			pcStr = "0" + pcStr;
		}
		String fetchSyso = "";
		String instrFetchingSyso = this.instrFetched;
		if (!this.instrFetched.equals("")) {
			while (instrFetchingSyso.length() < 16) {
				instrFetchingSyso = "0" + instrFetchingSyso;
			}
			if (instrFetchingSyso.length() > 16) {
				instrFetchingSyso = instrFetchingSyso.substring(16);
			}
			fetchSyso = "Fetching: " + instrFetchingSyso + " from instruction memory";
		}

		String decodeSyso = "";
		String instrDecodingSyso = this.toBeDecoded;

		String inputForNextCycle = "";

		if (!this.toBeDecoded.equals("")) {
			while (instrDecodingSyso.length() < 16) {
				instrDecodingSyso = "0" + instrDecodingSyso;
			}
			if (instrDecodingSyso.length() > 16) {
				instrDecodingSyso = instrDecodingSyso.substring(16);
			}
			inputForNextCycle = "Inputs For Next Cycle:- \n";
			decodeSyso = "Decoding: " + instrDecodingSyso + "\n";
			String opcode = instrDecodingSyso.substring(0, 4);
			String R1 = instrDecodingSyso.substring(4, 10);
			String R2orImm = instrDecodingSyso.substring(10, 16);
			int opcodeVal = this.toBeDecodedArr[0];
			byte valueR1 = (byte) this.toBeDecodedArr[2];
			byte valueR2 = (byte) this.toBeDecodedArr[3];
			int imm = this.toBeDecodedArr[4];

			decodeSyso += "Decoded Instruction For Next Cycle:- \n";
			decodeSyso += "opcode: " + opcode + " R1: " + R1 + " R2|Immediate: " + R2orImm + "\n";
			decodeSyso += "opcode value: " + opcodeVal + " R1 index: " + Integer.parseInt(R1, 2)
					+ " R2 index|Immediate value: " + Integer.parseInt(R2orImm, 2) + "|" + imm;

			String r2ORimm = "";

			String valR1Str = Integer.toBinaryString(valueR1);

			while (valR1Str.length() < 8) {
				valR1Str = '0' + valR1Str;
			}
			if (valR1Str.length() > 8) {
				valR1Str = valR1Str.substring(24);
			}
			inputForNextCycle += "R1: binary content = " + valR1Str + " content = " + valueR1 + " ";

			if (opcodeVal == 0 || opcodeVal == 1 || opcodeVal == 2 || opcodeVal == 6 || opcodeVal == 7) {
				r2ORimm = Integer.toBinaryString(valueR2);
				while (r2ORimm.length() < 8) {
					r2ORimm = '0' + r2ORimm;
				}
				if (r2ORimm.length() > 8) {
					r2ORimm = r2ORimm.substring(24);
				}
				inputForNextCycle += "R2: binary content = " + r2ORimm + " content = " + valueR2 + " ";
			} else {
				r2ORimm = Integer.toBinaryString(imm);
				if (imm < 0) {
					r2ORimm = r2ORimm.substring(26);
				} else {
					while (r2ORimm.length() < 6) {
						r2ORimm = '0' + r2ORimm;
					}
				}

				inputForNextCycle += "Immediate: binary content = " + r2ORimm + " content = " + imm;
			}
		}

		String executeSyso = "";
		String instrExecutingSyso = this.toBeExecuted;

		if (!this.toBeExecuted.equals("")) {
			while (instrExecutingSyso.length() < 16) {
				instrExecutingSyso = '0' + instrExecutingSyso;
			}
			String instrAssembly = "";
			String opcode = instrExecutingSyso.substring(0, 4);
			String r1 = instrExecutingSyso.substring(4, 10);
			String r2orImm = instrExecutingSyso.substring(10, 16);

			Boolean rtype = false;
			switch (opcode) {
			case "0000":
				instrAssembly += "ADD ";
				rtype = true;
				break;
			case "0001":
				instrAssembly += "SUB ";
				rtype = true;
				break;
			case "0010":
				instrAssembly += "MUL ";
				rtype = true;
				break;
			case "0011":
				instrAssembly += "MOVI ";
				break;
			case "0100":
				instrAssembly += "BEQZ ";
				break;
			case "0101":
				instrAssembly += "ANDI ";
				break;
			case "0110":
				instrAssembly += "EOR ";
				rtype = true;
				break;
			case "0111":
				instrAssembly += "BR ";
				rtype = true;
				break;
			case "1000":
				instrAssembly += "SAL ";
				break;
			case "1001":
				instrAssembly += "SAR ";
				break;
			case "1010":
				instrAssembly += "LDR ";
				break;
			case "1011":
				instrAssembly += "STR ";
				break;
			}

			instrAssembly += "R" + Integer.parseInt(r1, 2);

			if (rtype) {
				instrAssembly += " R" + Integer.parseInt(r2orImm, 2) + " ";
			} else {
				int immVal = convertFromTwosComplement(r2orImm);
				instrAssembly += " " + immVal + " ";
			}

			instrAssembly += instrExecutingSyso;

			executeSyso += "Executing: " + instrAssembly;

		}

		String regSyso = "";
		String memSyso = "";
		String instrExecutingSyso2 = this.toBeExecuted;
		if (!this.toBeExecuted.equals("")) {
			while (instrExecutingSyso2.length() < 16) {
				instrExecutingSyso2 = "0" + instrExecutingSyso2;
			}
			String R1 = instrExecutingSyso2.substring(4, 10);
			int opcodeVal = this.decodedArr[0];
			if (opcodeVal == 0 || opcodeVal == 1 || opcodeVal == 2 || opcodeVal == 3 || opcodeVal == 5 || opcodeVal == 6
					|| opcodeVal == 8 || opcodeVal == 9 || opcodeVal == 10) {
				String prevRegBin = Integer.toBinaryString(this.prevValueReg);
				while (prevRegBin.length() < 8) {
					prevRegBin = '0' + prevRegBin;
				}
				String newRegBin = Integer.toBinaryString(this.newValueReg);
				while (newRegBin.length() < 8) {
					newRegBin = '0' + newRegBin;
				}
				if (prevRegBin.length() > 8) {
					prevRegBin = prevRegBin.substring(24);
				}
				if (newRegBin.length() > 8) {
					newRegBin = newRegBin.substring(24);
				}
				regSyso += "Writing to R" + Integer.parseInt(R1, 2) + "\n";
				regSyso += "R" + Integer.parseInt(R1, 2) + " Content updated from binary content = " + prevRegBin
						+ " content = " + this.prevValueReg + " to " + "binary content = " + newRegBin + " content = "
						+ this.newValueReg + "\n";
			} else if (opcodeVal == 4 || opcodeVal == 7) {
				String prevPCBin = Integer.toBinaryString(this.prevPC);
				while (prevPCBin.length() < 8) {
					prevPCBin = '0' + prevPCBin;
				}
				String newPCBin = Integer.toBinaryString(this.registers.getProgramCounter());
				while (newPCBin.length() < 16) {
					newPCBin = '0' + newPCBin;
				}
				regSyso += "Writing to PC" + "\n";
				regSyso += "PC" + " Content updated from binary content = " + prevPCBin + " content = " + this.prevPC
						+ " to " + "binary content = " + newPCBin + " content = " + this.registers.getProgramCounter()
						+ "\n";
			} else {
				String prevRegBin = Integer.toBinaryString(this.prevValueMem);
				String newRegBin = Integer.toBinaryString(this.newValueMem);
				while (prevRegBin.length() < 8) {
					prevRegBin = '0' + prevRegBin;
				}
				while (newRegBin.length() < 8) {
					newRegBin = '0' + newRegBin;
				}
				if (prevRegBin.length() > 8) {
					prevRegBin = prevRegBin.substring(24);
				}
				if (newRegBin.length() > 8) {
					newRegBin = newRegBin.substring(24);
				}
				memSyso += "Memory segment " + this.changedAddressValueMem + " Content updated from binary content = "
						+ prevRegBin + " content = " + this.prevValueMem + " to " + "binary content = " + newRegBin
						+ " content = " + this.newValueMem + "\n";
			}
			if (opcodeVal == 0) {
				regSyso += "Carry Flag Updated to: " + this.registers.getC() + "\n";
			}
			if (opcodeVal == 0 || opcodeVal == 1) {
				regSyso += "Overflow Flag Updated to: " + this.registers.getV() + "\n";
			}
			if (opcodeVal == 0 || opcodeVal == 1 || opcodeVal == 2 || opcodeVal == 5 || opcodeVal == 6 || opcodeVal == 8
					|| opcodeVal == 9) {
				regSyso += "Negative Flag Updated to: " + this.registers.getN() + "\n";
			}
			if (opcodeVal == 0 || opcodeVal == 1) {
				regSyso += "Sign Flag Updated to: " + this.registers.getS() + "\n";
			}
			if (opcodeVal == 0 || opcodeVal == 1 || opcodeVal == 2 || opcodeVal == 5 || opcodeVal == 6 || opcodeVal == 8
					|| opcodeVal == 9) {
				regSyso += "Zero Flag Updated to: " + this.registers.getZ() + "\n";
			}
		}

		String pcNewBin = Integer.toBinaryString(this.registers.getProgramCounter());
		while (pcNewBin.length() < 16) {
			pcNewBin = "0" + pcNewBin;
		}
		String pcSyso = "Program Counter Updated to binary content = " + pcNewBin + " content = "
				+ this.registers.getProgramCounter();
		String print = "Cycle: " + this.clockCycle + "\n" + "Program Counter: binary content = " + pcStr + " content = "
				+ (this.registers.getProgramCounter() - 1) + "\n" + fetchSyso + "\n" + decodeSyso + "\n"
				+ inputForNextCycle + "\n" + executeSyso + "\n" + regSyso + "\n" + memSyso + "\n" + pcSyso;
		String printRemoveEmptyLines = print.replaceAll("(?m)^[ \t]*\r?\n", "");
		printRemoveEmptyLines = printRemoveEmptyLines + "\n" + "-------------------------------" + "\n";
		String info = printRemoveEmptyLines;
		return info;
	}

	public String printFinal() {
		String finalInfo = "";
		finalInfo += "\n" + "Registers:" + "\n";
		String pcStr = Integer.toBinaryString(this.registers.getProgramCounter());
		while (pcStr.length() < 16) {
			pcStr = "0" + pcStr;
		}
		finalInfo += "Program Counter: " + "binary content = " + pcStr + " content = "
				+ this.registers.getProgramCounter() + "\n";
		finalInfo += "Status Register: " + "binary content = " + this.registers.printStatusReg() + " content = "
				+ Integer.parseInt(this.registers.printStatusReg(), 2) + " C=" + this.registers.getC() + " V="
				+ this.registers.getV() + " N=" + this.registers.getN() + " S=" + this.registers.getS() + " Z="
				+ this.registers.getZ() + "\n";
		for (int i = 0; i < this.registers.getGeneralPurposeR().length; i++) {
			String regStr = Integer.toBinaryString(this.registers.getGeneralPurposeR()[i]);
			while (regStr.length() < 8) {
				regStr = "0" + regStr;
			}
			if (regStr.length() > 8) {
				regStr = regStr.substring(24);
			}
			finalInfo += "R" + i + ": " + "binary content = " + regStr + " content = "
					+ this.registers.getGeneralPurposeR()[i] + "\n";
		}
		finalInfo += "-------------------------" + "\n";
		finalInfo += "Instruction Memory Modifications:" + "\n";
		for (int i = 0; i < this.instructionMemory.lastInsertedPointer + 1; i++) {
			String instStr = Integer.toBinaryString(this.instructionMemory.instrMem[i]);
			while (instStr.length() < 16) {
				instStr = "0" + instStr;
			}
			if (instStr.length() > 16) {
				instStr = instStr.substring(16);
			}
			finalInfo += "Memory Block: " + i + " binary content = " + instStr + " content = "
					+ Integer.parseInt(instStr, 2) + "\n";
		}
		finalInfo += "-------------------------" + "\n";
		finalInfo += "Data Memory Modifications:" + "\n";
		if (this.dataMemory.usedIndices.size() == 0) {
			finalInfo += "No Modifications" + "\n";
		}
		for (int i = 0; i < this.dataMemory.usedIndices.size(); i++) {
			String dataStr = Integer.toBinaryString(this.dataMemory.dataMem[this.dataMemory.usedIndices.get(i)]);
			while (dataStr.length() < 8) {
				dataStr = "0" + dataStr;
			}
			if (dataStr.length() > 8) {
				dataStr = dataStr.substring(24);
			}
			finalInfo += "Memory Block: " + this.dataMemory.usedIndices.get(i) + " binary content = " + dataStr
					+ " content = " + this.dataMemory.dataMem[this.dataMemory.usedIndices.get(i)] + "\n";
		}
		finalInfo += "-------------------------" + "\n";
		finalInfo += "Program Completed!";
		return finalInfo;
	}

	public static int convertFromTwosComplement(String binary) {
		int decRes = 0;
		if (binary.charAt(0) == '0') {
			for (int i = 0; i < binary.length(); i++) {
				int idx = binary.length() - i - 1;
				decRes += ((int) (Math.pow(2, idx)) * Integer.parseInt("" + binary.charAt(i)));
			}

		} else {
			decRes = (int) (-1 * Math.pow((2), binary.length() - 1));
			int sumOfRest = 0;
			for (int i = 1; i < binary.length(); i++) {
				int idx = binary.length() - i - 1;
				sumOfRest += ((int) (Math.pow(2, idx)) * Integer.parseInt("" + binary.charAt(i)));
			}
			decRes += sumOfRest;
		}
		return decRes;
	}

}
