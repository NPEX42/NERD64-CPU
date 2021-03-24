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
## The Code

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
    public SimpleDictionary() {}
    public SimpleDictionary(HashMap<String, String> _dictionary) {
        dictionary = _dictionary;
    }
    
    public int GetInt(String key) {
    	return Integer.parseInt(dictionary.getOrDefault(key, "0"));
    }
    
    public void SetInt(String key, int value) {
    	dictionary.put(key, ""+value);
    }
    
    
}
```

The Instruction Set

```
# Memory / IO | 001X
LDA=0010
LDI=0011
LDX=0012
LDY=0013

```

