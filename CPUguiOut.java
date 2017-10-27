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
