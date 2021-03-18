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
  - presents values in Memory
- RegisterView
  - presents values in registers
- AssemblyView  
  - Presents the currently running Program

----
#### Package - np.vms.nerd64.utils
- TextLoader
  - Loads Text files from Disk
- TextSaver
  - Saves Text Files onto the disk
- LineParser
  - Converts Lines of text into Opcodes & Operands using a Dictionary



  

