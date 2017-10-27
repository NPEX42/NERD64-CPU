
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
