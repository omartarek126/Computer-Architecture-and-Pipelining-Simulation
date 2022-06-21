import java.io.File;
import java.util.Scanner;

public class Parser {

	public void parseInstructions(InstructionMemory instructionMemory) {

		try {
			// NO GUI run
			// File file = new File("Instructions/instructions.txt");
			File file = new File("Instructions/instructionsGUI.txt");
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String str[] = line.split(" ");
				String currInst = "";
				char type = 'R';
				switch (str[0]) {
				case "ADD":
					currInst += "0000";
					break; // =0
				case "SUB":
					currInst += "0001";
					break; // =1
				case "MUL":
					currInst += "0010";
					break; // =2
				case "MOVI":
					currInst += "0011";
					type = 'I';
					break; // =3
				case "BEQZ":
					currInst += "0100";
					type = 'I';
					break; // =4
				case "ANDI":
					currInst += "0101";
					type = 'I';
					break; // =5
				case "EOR":
					currInst += "0110";
					break; // =6
				case "BR":
					currInst += "0111";
					break; // =7
				case "SAL":
					currInst += "1000";
					type = 'I';
					break; // =8
				case "SAR":
					currInst += "1001";
					type = 'I';
					break; // =9
				case "LDR":
					currInst += "1010";
					type = 'I';
					break; // =10
				case "STR":
					currInst += "1011";
					type = 'I';
					break; // =11
				}

				int regNum = Integer.parseInt(str[1].substring(1));
				String regNumBin = Integer.toBinaryString(regNum);
				if (regNum < 0 || regNum > 63) {
					System.out.println("Invalid register number");
					System.out.println("Program terminated!!");
					System.exit(-1);
				}
				while (regNumBin.length() < 6) {
					regNumBin = '0' + regNumBin;
				}
				currInst += regNumBin;

				if (type == 'R') {
					int regNum2 = Integer.parseInt(str[2].substring(1));
					String regNumBin2 = Integer.toBinaryString(regNum2);
					if (regNum2 < 0 || regNum2 > 63) {
						System.out.println("Invalid register number");
						System.out.println("Program terminated!!");
						System.exit(-1);
					}
					while (regNumBin2.length() < 6) {
						regNumBin2 = '0' + regNumBin2;
					}
					currInst += regNumBin2;
				} else {
					int immediate = Integer.parseInt(str[2]);
					String immediateBin = Integer.toBinaryString(immediate);
					if (immediate >= 0 && immediate < 32) {
						while (immediateBin.length() < 6) {
							immediateBin = '0' + immediateBin;
						}
					} else if (immediate < 0 && immediate >= -32) {
						immediateBin = immediateBin.substring(26);
					} else {
						immediateBin = immediateBin.substring(immediateBin.length() - 6);
					}
					currInst += immediateBin;
				}
				short currInstShort = (short) Integer.parseInt(currInst, 2);
				instructionMemory.instrMem[++instructionMemory.lastInsertedPointer] = currInstShort;
			}
			sc.close();
		} catch (Exception e) {
			System.out.println("Instruction file not found");
			System.out.println("Program terminated!!");
			System.exit(-1);
		}

	}
}
