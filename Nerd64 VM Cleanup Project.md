# Nerd64 VM Cleanup Project
## Overview - Responsibilities
### Current Responsibilities
- CPUmain
  - Creates the UI and starts the program
- CPUGUI
  - Holds the processor Logic
  - Holds UI Logic
- CPUguiOut
  - Dead Class (No Function)

### New Responsibilities / Classes
----
#### Package - np.vms.nerd64.core
- Main
  - Launches The Program
- Nerd64
  - Business Logic
----
#### Package - np.vms.nerd64.ui
- MainWindow
  - Parent / Container for Other UI Components
- RamView
  - present values in Memory
- RegisterView
  - present values in registers
- AssemblyView  
  - Present the currently running Program

----
#### Package - np.vms.nerd64.utils
- TextLoader
  - Loads Text files from Disk
- TextSaver
  - Saves Text Files onto the disk
- LineParser
  - Convert Lines of text into Opcodes &
    Operands using a Dictionary & Standard Symbols ($, %, @, #)
  - Eg. LDA $0001 -> 0010 0001,
  - ADI $FF -> 0020 00FF
  - JMP @Loop (Loop = 10) -> 0030 00A0
- SimpleDictionaryLoader
  - Loads a textfile containing simple Key-Value pairs, one on each line, separated by the '='
    character
- SimpleDictionary
  - Contains Simple Key-Value Pairs
----
## The Old Code

### CPUGUI

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CPUGUI {
	private CPUguiOut outputWindow;
	private JFrame window;
	private JButton btnLoad;
	private JButton btnSave;
	private JButton btnRun;
	private JButton btnSend;
	private JTextArea txtaRAM;
	private JTextArea txtaINS;
	private JTextField txtInput;
	private JLabel lblARegState;
	private JLabel lblBRegState;
	private JLabel lblMRegState;
	private JLabel lblLRegState;
	private JPanel pnlOptions;
	private JPanel pnlDisplay;
	private JCheckBox cbDebug;
	private JScrollPane scpRam;
	private JScrollPane scpINS;
	private JPanel pnlInput;
	private JProgressBar pbMemFree;
	
	
	File file;
	private BufferedReader breader;
	
	private int i = 0;
	private int p = 0;
	private int inpInt = 0;
	private int RF = 0;
	
	private String inpStr = "";
	
	private short areg = 0;
	private short breg = 0;
	private short mreg = 0;
	private short lreg = 0;
	
	private boolean btnState;
	
	public static short[] ram = new short[8192];
	
	
	public void Create(int h, int w)
	{
		System.out.println(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		window = new JFrame("CPU emulator 3.3.0 BETA");
		pnlOptions = new JPanel();
		pnlDisplay = new JPanel();
		pnlInput = new JPanel();
		
		btnLoad = new JButton("Load");
		btnSave = new JButton("Save");
		btnRun = new JButton("Run");
		btnSend = new JButton("Send");
		
		btnLoad.addActionListener(new LoadListener());
		btnRun.addActionListener(new RunListener());
		btnSend.addActionListener(new SendListener());
		
		lblARegState = new JLabel("Register A: 0");
		lblBRegState = new JLabel("Register B: 0");
		lblMRegState = new JLabel("Register M: 0");
		lblLRegState = new JLabel("Register L: 0");
		
		txtaRAM = new JTextArea(16,10);
		txtaINS = new JTextArea(15, 15);
		txtInput = new JTextField(25);
		
		cbDebug = new JCheckBox("Debug");
		cbDebug.setSelected(true);
		
		scpINS = new JScrollPane(txtaINS);
		scpRam = new JScrollPane(txtaRAM);
		
		pbMemFree = new JProgressBar(0, ram.length);
		
		txtaRAM.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		txtaINS.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		txtaINS.setEditable(false);
		txtaRAM.setEditable(false);
		
		pnlOptions.add(btnLoad);
		pnlOptions.add(btnRun);
		pnlOptions.add(btnSave);
		pnlOptions.add(cbDebug);
		
		pnlDisplay.setLayout(new BoxLayout(pnlDisplay,BoxLayout.Y_AXIS));
		
		pnlDisplay.add(lblARegState);
		pnlDisplay.add(lblBRegState);
		pnlDisplay.add(lblMRegState);
		pnlDisplay.add(lblLRegState);
		pnlDisplay.add(pbMemFree);
		pbMemFree.setValue(256);
		pbMemFree.setStringPainted(true);
		pbMemFree.setString("Memory Free");
		pnlDisplay.add(scpRam);
		
		
		pnlInput.add(txtInput);
		pnlInput.add(btnSend);
		
		
		
		window.getContentPane().add(BorderLayout.NORTH, pnlOptions);
		window.getContentPane().add(BorderLayout.WEST, pnlDisplay);
		window.getContentPane().add(BorderLayout.SOUTH, pnlInput);
		window.getContentPane().add(scpINS);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setSize(w, h);
		
		window.setVisible(true);
		int i = 0;
		RamUsageUpdate();
	}
	
	public void UpdateRam()
	{
		p = 0;
		txtaRAM.setText("");
		while(p <= ram.length-1)
		{
			txtaRAM.append(String.format("%04x", p).toUpperCase()+": "+ram[p]+"\n");
			p += 1;
		}
	}
	
	public String parseData(long data)
	{
		short ins = (short) ((data & 0xFFFF00000000l) >> 32);
		short d1 = (short)((data & 0x0000FFFF0000l) >> 16);
		short d2 = (short) (data & 0x00000000FFFFl);
		String out = "";
		
		switch(ins)
		{
		case 0x0000:
			out = "NOP";
			break;
		case 0x0001:
			out = "HLT";
			break;
			
		case 0x0002:
			out = "LDA";
			break;
			
		case 0x0003:
			out = "STB";
			break;
			
		case 0x0004:
			out = "LDB";
			break;
			
		case 0x0005:
			out = "STB";
			break;
			
			
		case 0x0006:
			out = "LDM";
			break;
			
			
		case 0x0007:
			out = "STM";
			break;
			
			
		case 0x0008:
			out = "ADD";
			break;
			
			
		case 0x0009:
			out = "SUB";
			break;
			
		case 0x000A:
			out = "DIV";
			break;
		}
		
		System.out.println(ins+" "+d1+" "+d2);
		return out+" "+d1+" "+d2;
	}
	
	public void cpuPerform(String ins, int d, int d2)
	{
		
		
		
		switch(ins)
		{
		case "NOP":
			if(cbDebug.isSelected()){printIns(i, "NOP", "0000", d, d2);}
			else {printIns(i, "0000", d, d2);}
			break;
			
		case "HLT":
			if(cbDebug.isSelected()) {printIns(i, "HLT", "0001", d, d2);}
			else {printIns(i, "0001", d, d2);}
			break;
			
		case "LDA":
			if(cbDebug.isSelected()) {printIns(i, "LDA", "0002", d, d2);}
			else {printIns(i, "0002", d, d2);}
			areg = ram[d];
			break;
			
		case "STA":
			if(cbDebug.isSelected()) {printIns(i, "STA", "0003", d, d2);}
			else {printIns(i, "0003", d, d2);}
			ram[d] = areg;
			break;
			
		case "LDB":
			if(cbDebug.isSelected()) {printIns(i, "LDB", "0004", d, d2);}
			else {printIns(i, "0004", d, d2);}
			breg = ram[d];
			break;
			
		case "STB":
			if(cbDebug.isSelected()) {printIns(i, "STB", "0005", d, d2);}
			else {printIns(i, "0005", d, d2);}
			ram[d] = breg;
			break;
			
		case "LDM":
			if(cbDebug.isSelected()) {printIns(i, "LDM", "0006", d, d2);}
			else {printIns(i, "0006", d, d2);}
			mreg = ram[d];
			break;
			
		case "STM":
			if(cbDebug.isSelected()) {printIns(i, "STM", "0007", d, d2);}
			else {printIns(i, "0007", d, d2);}
			ram[d] = mreg;
			break;
			
		case "ADD":
			mreg += areg;
			if(cbDebug.isSelected()) {printIns(i, "ADD", "0008", d, d2);}
			else {printIns(i, "0008", d, d2);}
			break;
			
		case "SUB":
			mreg -= areg;
			if(cbDebug.isSelected()) {printIns(i, "SUB", "0009", d, d2);}
			else {printIns(i, "0009", d, d2);}
			break;
			
		case "DIV":
			mreg /= areg;
			if(cbDebug.isSelected()) {printIns(i, "DIV", "000A", d, d2);}
			else {printIns(i, "000A", d, d2);}
			break;
			
		case "MUL":
			mreg *= areg;
			if(cbDebug.isSelected()) {printIns(i, "MUL", "000B", d, d2);}
			else {printIns(i, "000B", d, d2);}
			break;
			
		case "DSP":
			if(cbDebug.isSelected()) {printIns(i, "DSP", "000C", d, d2);}
			else {printIns(i, "000C", d, d2);}
			
			/*
			outputWindow.println(String.format("%04d",ram[20])+" 0x"+String.format("%04x", ram[20]).toUpperCase()+" "+String.format("%c", ram[20]));
			outputWindow.println(String.format("%04d",ram[21])+" 0x"+String.format("%04x", ram[21]).toUpperCase()+" "+String.format("%c", ram[21]));
			outputWindow.println(String.format("%04d",ram[22])+" 0x"+String.format("%04x", ram[22]).toUpperCase()+" "+String.format("%c", ram[22]));
			outputWindow.println(String.format("%04d",ram[23])+" 0x"+String.format("%04x", ram[23]).toUpperCase()+" "+String.format("%c", ram[23]));
			outputWindow.println(String.format("%04d",ram[23])+" 0x"+String.format("%04x", ram[23]).toUpperCase()+" "+String.format("%c", ram[24]));
			outputWindow.println(String.format("%04d",ram[23])+" 0x"+String.format("%04x", ram[23]).toUpperCase()+" "+String.format("%c", ram[25]));
			outputWindow.println(String.format("%04d",ram[23])+" 0x"+String.format("%04x", ram[23]).toUpperCase()+" "+String.format("%c", ram[26]));
			outputWindow.println(String.format("%04d",ram[23])+" 0x"+String.format("%04x", ram[23]).toUpperCase()+" "+String.format("%c", ram[27]));
			outputWindow.print(String.format("%c", ram[20]));
			outputWindow.print(String.format("%c", ram[21]));
			outputWindow.print(String.format("%c", ram[22]));
			outputWindow.print(String.format("%c", ram[23]));
			outputWindow.print(String.format("%c", ram[24]));
			outputWindow.print(String.format("%c", ram[25]));
			outputWindow.print(String.format("%c", ram[26]));
			outputWindow.print(String.format("%c", ram[27]));
			*/
			printRam();
			break;
			
		case "INC":
			breg += 1;
			if(cbDebug.isSelected()) {printIns(i, "INC", "000D", d, d2);}
			else {printIns(i, "000D", d, d2);}
			break;
			
		case "DEC":
			breg -= 1;
			if(cbDebug.isSelected()) {printIns(i, "DEC", "000E", d, d2);}
			else {printIns(i, "000E", d, d2);}
			break;
			
		case "PUT":
			ram[d2] = (byte) d;
			if(cbDebug.isSelected()) {printIns(i, "PUT", "000F", d, d2);}
			else {printIns(i, "000F", d, d2);}
			break;
			
		case "DMP":
			outputWindow.RamDump(ram);
			if(cbDebug.isSelected()) {printIns(i, "DMP", "0010", d, d2);}
			else {printIns(i, "0010", d, d2);}
			break;
			
		case "AND":
			if(cbDebug.isSelected()) {printIns(i, "AND", "0011", d, d2);}
			else {printIns(i, "0011", d, d2);}
			lreg = (byte) (lreg & areg);
			break;
			
		case "OR":
			if(cbDebug.isSelected()) {printIns(i, " OR", "0012", d, d2);}
			else {printIns(i, "0012", d, d2);}
			lreg = (byte) (lreg | areg);
			break;
			
		case "LDL":
			if(cbDebug.isSelected()) {printIns(i, "LDL", "0013", d, d2);}
			else {printIns(i, "0013", d, d2);}
			lreg = ram[d];
			break;
			
		case "STL":
			if(cbDebug.isSelected()) {printIns(i, "STL", "0014", d, d2);}
			else {printIns(i, "0014", d, d2);}
			ram[d] = lreg;
			break;
			
		default:
			txtaINS.append("Unknown Command: "+txtInput.getText()+"\n");
			break;
		}
		
		i += 12;
		
		lblARegState.setText("Register A: "+areg);
		lblBRegState.setText("Register B: "+breg);
		lblMRegState.setText("Register M: "+mreg);
		lblLRegState.setText("Register L: "+lreg);
		
		UpdateRam();
		RamUsageUpdate();
		
	}
	
	public void printIns(int i,String Ins,String Opcode,int d1,int d2)
	{
		String s = 
				String.format("%04x: ",(short) i).toUpperCase()+
				Ins+
				" "+
				String.format("%4s", Integer.toString(d1)).replace(" ", "0")+
				" "+
				String.format("%4s", Integer.toString(d2)).replace(" ", "0")+
				" "+
				Opcode+
				String.format("%04x",(short)d1).toUpperCase()+
				String.format("%04x",(short)d2).toUpperCase();
				
		txtaINS.append(s+System.lineSeparator());
	}
	
	public void printIns(int i ,String Opcode, int d1, int d2)
	{
		String s =
		String.format("%04x: ",(short) i).toUpperCase()+
		Opcode+
		String.format("%04x",(short)d1).toUpperCase()+
		String.format("%04x",(short)d2).toUpperCase();
		
		txtaINS.append(s+System.lineSeparator());
		}
	
	private class LoadListener implements ActionListener

	{
		@Override
		public void actionPerformed(ActionEvent e) {
		 JFileChooser fc = new JFileChooser("Open A .nrda file");
		 fc.showOpenDialog(window);
		 file = fc.getSelectedFile();
		 if(file != null)
		 {
			 System.out.println(file.getAbsolutePath());
		 }
		 else
		 {
			 System.out.println("File Not Selected, Assigning went wrong or user canceled...");
		 }
		 
			
		}
	}
	
	private class RunListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			Run(file);
			
		}
		
	}
	
	public void Run(File f)
	{
		String line = "";
		String[] a = new String[2];
		txtaINS.setText("");
		i = 0;
		try
		{
		
			breader = new BufferedReader(new FileReader(f));
			while((line = breader.readLine()) != null)
			{
				a = line.split(" ");
				System.out.print(a[0]+" ");
				System.out.println(a[1]);
				
				cpuPerform(a[0], Integer.parseInt(a[1]), Integer.parseInt(a[2]));
				
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			
		}
	}
	
	public void cpuInterpret(String line)
	{
		String[] a = line.split(" ");
		System.out.print(a[0]+" ");
		System.out.println(a[1]);
		
		cpuPerform(a[0], Integer.parseInt(a[1]), Integer.parseInt(a[2]));
	}
	
	public boolean bitCheck(long val, byte place)
	{
		boolean b = false;
		long av = 2^place;
		val = val & av;
		if(val != 0)
		{
			b = true;
		}
		return b;
	}
	
	public void inpChk()
	{
		if((txtInput.getText()) != null)
		{
			inpStr = txtInput.getText();
		}
	}
	
	public void RamUsageUpdate()
	{
		int i = 0;
		RF = 0;
		while(i <= ram.length-1)
		{
			if(ram[i] != 0)
			{
				RF += 1;
			}
			i += 1;
		}
		pbMemFree.setString((ram.length-RF)+"/"+(ram.length)+" BYTES "+(String.format("%.2f",pbMemFree.getPercentComplete()*100)+"% "));
		pbMemFree.setValue(RF);
		
		txtaRAM.setCaretPosition(0);
	}
	
	
	public void printRam()
	{
		int i = 0;
		while(i <= ram.length-1)
		{
			if(ram[i] == 0x0000)
			{
				break;
			} else 
			{
				outputWindow.print(String.format("%c", ram[i]));
			}
			i++;
		}
	}
	
	public class SendListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			String inp = txtInput.getText();
			
			String[] arg = inp.split(" ");
			cpuPerform(arg[0], Integer.parseInt(arg[1]), Integer.parseInt(arg[2]));
			
		}
		
	}
	
}
	   

```

## CPUmain

```java

public class CPUmain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CPUGUI gui = new CPUGUI();
		gui.Create(500,500);
		gui.UpdateRam();
		/*
		gui.cpuPerform("NOP", 0, 0);
		gui.cpuPerform("HLT", 0, 0);
		gui.cpuPerform("LDA", 1, 0);
		gui.cpuPerform("STA", 0, 0);
		gui.cpuPerform("LDB", 2, 0);
		gui.cpuPerform("STB", 0, 0);
		gui.cpuPerform("LDM", 3, 0);
		gui.cpuPerform("STM", 0, 0);
		gui.cpuPerform("ADD", 0, 0);
		gui.cpuPerform("SUB", 0, 0);
		gui.cpuPerform("DIV", 0, 0);
		gui.cpuPerform("MUL", 0, 0);
		gui.cpuPerform("DSP", 0, 0);
		gui.cpuPerform("INC", 0, 0);
		gui.cpuPerform("DEC", 0, 0);
		gui.cpuPerform("PUT", 20, 10);
		*/
	
		gui.cpuInterpret((gui.parseData(0x000000000000l)));
		gui.cpuInterpret((gui.parseData(0x000100000000l)));
		gui.cpuInterpret((gui.parseData(0x000200000000l)));
		gui.cpuInterpret((gui.parseData(0x000300000000l)));
		gui.cpuInterpret((gui.parseData(0x000400000000l)));
	}

}
```

#### CPUguiOut

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
public class CPUguiOut {

	private JFrame window;
	private JTextArea output;
	private JScrollPane scOut;
	private JButton btnSave;
	
	private JFileChooser jfc;
	private File file;
	
	
	
	public void OutputGuiCreate()
	{
		window = new JFrame("CPU 3.3.0 Output BETA");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		output = new JTextArea();
		scOut = new JScrollPane(output,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		output.setEditable(false);
		output.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
		
		window.add(BorderLayout.CENTER,scOut);
		
		window.setSize(400, 800);
		window.setVisible(true);
		
	}
	
	public void print(String s)
	{
		output.append(s);
		
	}
	
	public void println(String s)
	{
		output.append(s+"\n");
	}
	
	public void print(int s)
	{
		output.append(s+"");
		
	}
	
	public void println(int s)
	{
		output.append(s+"\n");
	}
	
	public void RamDump(short[] ram)
	{
		try
		{
			jfc = new JFileChooser();
			jfc.showSaveDialog(null);
			file = jfc.getSelectedFile();
			
			if(file != null)
			{
				FileWriter fw = new FileWriter(file);
				int i = 0;
				while(i <= ram.length-1)
				{
					fw.write(String.format("%02x", i)+":"+String.format("%04d",ram[i])+" "+String.format("%c", ram[i])+System.lineSeparator());
					i += 1;
				}
				fw.close();
			}
			
			if(file == null)
			{
				System.out.println("something went wrong!");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public class SaveListener implements ActionListener
	{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try
			{
				jfc = new JFileChooser();
				jfc.showSaveDialog(null);
				file = jfc.getSelectedFile();
				
				FileWriter fw = new FileWriter(file);
				fw.write(output.getText());
				fw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	}
}

```



## The New Code

TextLoader

```java
public class TextLoader {
    public static String[] LoadTextFile(String path) throws RuntimeException {
        try {
            BufferedReader reader = new BufferedReader(
            	new FileReader(path)
            );
            StringBuffer buffer = new StringBuffer();
            String line;
            
            while((line = buffer.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
            
            return buffer.toString();
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }
    }
}
```

  SimpleDictionary

```java
public class SimpleDictionary {
    private HashMap<String, String> dictionary = new HashMap<>();
    
    public int GetInt(String key) {
      return Integer.parseInt(dictionary.getOrDefault(key, "0"));
    }
}
```



