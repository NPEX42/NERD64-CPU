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
	   
