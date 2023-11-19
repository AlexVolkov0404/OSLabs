import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FairShareScheduler {
    private final List<Process> processes = new ArrayList<>();
    private final Map<Integer, Integer> groupUtilization = new HashMap<>();

    void addProcess(Process process) {
        processes.add(process);
        groupUtilization.putIfAbsent(process.getGroupId(), 1);
    }

    void removeProcess(Process p) {
        //processes.remove(p.getId());
        processes.remove(p);
    }

    Process getNextProcess() {
        Process minProcess = null;
        int minPriority = Integer.MAX_VALUE;

        for (Process process: processes) {
            if(!process.isBlocked()) {
                int pPriority = process.calculatePriority(groupUtilization.get(process.getGroupId()));

                if (pPriority < minPriority) {
                    minProcess = process;
                    minPriority = pPriority;
                }
            }
        }

        return minProcess;
    }

    void updateProcessUtilization(Process p) {
        p.incrementUtilization();

        int utilization = groupUtilization.get(p.getGroupId()) + 1;
        groupUtilization.replace(p.getGroupId(), utilization);
    }

    void updateBlockedProcesses() {
        for (Process process : processes) {
            process.decrementBlockTime();
        }
    }

    boolean hasProcesses() {
        return !processes.isEmpty();
    }
}
