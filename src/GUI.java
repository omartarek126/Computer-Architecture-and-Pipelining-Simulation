import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI extends JFrame {

	private Font customFont;
	private JLabel backgroundImg;
	private JLabel enterProgramLabel;
	private JButton runButton;
	private JLabel runButtonImg;
	private JTextArea output;
	private JScrollPane outputScrollable;
	private JTextArea inputProgram;
	private JScrollPane inputProgramScrollable;

	public GUI() {

		try {
			this.customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts//EvilEmpire-4BBVK.ttf"))
					.deriveFont(25f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(this.customFont);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);

		this.backgroundImg = new JLabel(new ImageIcon("Images/background.gif"));
		this.setContentPane(this.backgroundImg);

		this.enterProgramLabel = new JLabel("Enter Your Program in Assembly:");
		this.enterProgramLabel.setFont(this.customFont);
		this.enterProgramLabel.setSize(500, 500);
		this.enterProgramLabel.setForeground(Color.WHITE);
		this.enterProgramLabel.setBounds(790, 20, 400, 100);
		this.add(this.enterProgramLabel);

		this.inputProgram = new JTextArea();
		this.inputProgram.setFont(new Font("Book Antiqua", Font.BOLD, 20));
		this.inputProgramScrollable = new JScrollPane(this.inputProgram);
		this.inputProgramScrollable.setBounds(800, 90, 300, 200);
		this.inputProgram.setBackground(Color.BLACK);
		this.inputProgram.setForeground(Color.WHITE);
		this.inputProgram.setCaretColor(Color.WHITE);
		this.inputProgram.setMargin(new Insets(10, 10, 10, 10));
		this.inputProgram.setText("MOVI R1 2" + "\n" + "MOVI R2 5" + "\n" + "MOVI R3 12" + "\n" + "MOVI R4 16" + "\n"
				+ "MOVI R5 3" + "\n" + "MOVI R6 5" + "\n" + "MOVI R7 25" + "\n" + "MOVI R8 28" + "\n" + "MOVI R9 31"
				+ "\n" + "MOVI R10 30" + "\n" + "MOVI R11 17" + "\n" + "ADD R1 R2" + "\n" + "SUB R3 R4" + "\n"
				+ "MUL R5 R6" + "\n" + "ANDI R7 23" + "\n" + "EOR R8 R9" + "\n" + "SAL R10 2" + "\n" + "SAR R11 4");
		this.add(this.inputProgramScrollable);

		this.runButton = new JButton();
		this.runButton.setOpaque(false);
		this.runButton.setContentAreaFilled(false);
		this.runButton.setBorderPainted(false);
		this.runButton.setBounds(900, 320, 110, 49);
		this.runButton.setFocusable(false);
		this.runButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(this.runButton);

		this.runButtonImg = new JLabel(new ImageIcon("Images/runButtonImg.png"));
		this.runButtonImg.setBounds(900, 320, 110, 49);
		this.add(this.runButtonImg);

		this.output = new JTextArea();
		this.output.setEditable(false);
		this.output.setFont(new Font("Book Antiqua", Font.BOLD, 25));
		this.output.setForeground(Color.WHITE);
		this.outputScrollable = new JScrollPane(this.output);
		this.outputScrollable.setBounds(210, 400, 1500, 500);
		this.output.setBackground(Color.BLACK);
		this.output.setFocusable(false);
		this.output.setMargin(new Insets(10, 10, 10, 10));
		this.add(this.outputScrollable);

		this.revalidate();
		this.repaint();
	}

	public JTextArea getInputProgram() {
		return this.inputProgram;
	}

	public JTextArea getOutput() {
		return this.output;
	}

	public void setInputProgram(JTextArea inputProgram) {
		this.inputProgram = inputProgram;
	}

	public JButton getRunButton() {
		return this.runButton;
	}

	public void setRunButton(JButton runButton) {
		this.runButton = runButton;
	}

}
