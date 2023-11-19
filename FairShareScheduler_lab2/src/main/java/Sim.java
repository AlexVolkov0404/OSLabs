import java.io.File;
import java.util.Map;
import java.util.Scanner;

public class Sim {
    private static int CLOCK_PER_TIMER = 100;

    private static int nextProcessId = 0;

    private static Process loadedProcess = null;

    private static int CPU_instructionPointer;

    private static final FairShareScheduler scheduler = new FairShareScheduler();

    public static void main(String[] args) {
        SchedulerCliConfigurationParser argumentParser = new SchedulerCliConfigurationParser();
        SchedulerProcessConfiguration config = argumentParser.parseConfigurationFromArgs(args);

        loadPrograms(config);
        loadNextProcess();

        int timerLeft = CLOCK_PER_TIMER;

        while (scheduler.hasProcesses()){
            if (loadedProcess != null) {
                Process.Status status = runNextInstruction();

                scheduler.updateProcessUtilization(loadedProcess);

                switch (status) {
                    case GO: break;
                    case BLOCK:
                        int delay = Instructions.calculateBlockWait();

                        System.out.println("Process " + loadedProcess.getId() + " blocked for " + delay + " cycles.");

                        loadedProcess.setBlockTime(delay);
                        loadNextProcess();
                        break;

                    case EXIT:
                    case ERROR:
                        System.out.println("Process " + loadedProcess.getId() + " exiting");
                        scheduler.removeProcess(loadedProcess);
                        loadNextProcess();
                        break;
                }
            }

            timerLeft--;
            if (timerLeft <= 0) {
                loadNextProcess();
                timerLeft = CLOCK_PER_TIMER;
            }

            scheduler.updateBlockedProcesses();
        }

        System.out.println("Processing complete!");
    }

    private static Process.Status runNextInstruction() {
        try {
            int instruction = Memory.readByte(CPU_instructionPointer);
            CPU_instructionPointer++;

            if (instruction >= Instructions.BRANCH_MIN && instruction <= Instructions.BRANCH_MAX) {
                int destAddr = (Memory.readByte(CPU_instructionPointer) << 8) | Memory.readByte(CPU_instructionPointer + 1);
                CPU_instructionPointer += 2;

                if (Instructions.isBranching(instruction)) {
                    CPU_instructionPointer = destAddr;
                }

                return Process.Status.GO;
            }

            switch (instruction) {
                case Instructions.COMPUTE:  return Process.Status.GO;
                case Instructions.IO_BLOCK: return Process.Status.BLOCK;
                case Instructions.EXIT:     return Process.Status.EXIT;
                default:
                    System.out.println("Illegal instruction: " + instruction);
                    return Process.Status.ERROR;
            }
        } catch (Exception e) {
            return Process.Status.ERROR;
        }
    }

    private static void loadNextProcess() {
        if (loadedProcess != null) {
            loadedProcess.setInstructionAddress(CPU_instructionPointer);
        }

        loadedProcess = scheduler.getNextProcess();

        if(loadedProcess != null) {
            Memory.loadProcess(loadedProcess);
            CPU_instructionPointer = loadedProcess.getInstructionAddress();

            System.out.println("Process loaded: " + loadedProcess.getId());
        } else if(scheduler.hasProcesses()) {
            System.out.println("Waiting for processes to unblock.");
        }
    }

    private static void loadPrograms(SchedulerProcessConfiguration config) {
        for (Map.Entry<String, Integer> entry: config.getFiles().entrySet()) {
            loadProgram(entry.getKey(), entry.getValue(), config);
        }
    }

    private static void loadProgram(String path, int groupId, SchedulerProcessConfiguration config) {
        try(Scanner in = new Scanner(new File(path))) {
            int size = in.nextInt();

            int address = Memory.claim(size);
            for (int i = 0; i < size; i++) {
                Memory.put(address + i, in.nextInt());
            }

            Process process = new Process(nextProcessId, groupId, address, size, config.getDefaultProcessPriority());
            scheduler.addProcess(process);

            nextProcessId++;
        } catch (Exception e) {
            System.out.println("Error loading program " + path);
        }
    }
}
