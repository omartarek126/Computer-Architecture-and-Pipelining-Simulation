import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUIController implements ActionListener, ListSelectionListener {

	private GUI g;
	private String programInAssembly = "";
	private Processor processor;

	public GUIController() {
		this.g = new GUI();
		this.g.getRunButton().addActionListener(this);
		this.processor = new Processor();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.g.getRunButton()) {
			this.programInAssembly = this.g.getInputProgram().getText();
			if (this.programInAssembly.length() == 0) {
				JOptionPane.showMessageDialog(this.g, "Please Enter a Program!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					FileWriter fWriter = new FileWriter("Instructions/instructionsGUI.txt");
					fWriter.write(this.programInAssembly);
					fWriter.close();
					this.processor = new Processor();
					this.g.getOutput().setText("");
					this.processor.parser.parseInstructions(this.processor.instructionMemory);
					for (; this.processor.loopingValue < 3 + ((this.processor.instructionMemory.lastInsertedPointer)
							* 1); this.processor.loopingValue++) {
						String temp = this.processor.fetch();
						this.processor.decode();
						this.processor.execute();
						this.processor.instrFetched = temp;
						this.g.getOutput().setText(this.g.getOutput().getText() + this.processor.printInfo());
						if (this.processor.flush) {
							this.processor.flushPipeline();
							this.processor.flush = false;
						}
						this.processor.clockCycle++;
						this.processor.toBeExecuted = this.processor.toBeDecoded;
						this.processor.toBeDecoded = this.processor.instrFetched;
					}
					this.g.getOutput().setText(
							this.g.getOutput().getText().substring(0, this.g.getOutput().getText().length() - 2));
					// Only blocks that are modified in data and instruction memory are printed
					this.g.getOutput().setText(this.g.getOutput().getText() + this.processor.printFinal());
				} catch (IOException e2) {
					System.out.println(e2.getMessage());
				}
			}
		}

		this.g.revalidate();
		this.g.repaint();
	}

	public static void main(String[] args) {
		new GUIController();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}
}
