import java.util.HashMap;
import java.util.Map;

public class SchedulerProcessConfiguration {
    private final Map<String, Integer> files = new HashMap<>();
    private int defaultProcessPriority;

    int getDefaultProcessPriority() {
        return defaultProcessPriority;
    }

    void setDefaultProcessPriority(int defaultProcessPriority) {
        this.defaultProcessPriority = defaultProcessPriority;
    }

    Map<String, Integer> getFiles() {
        return files;
    }

    void addFile(String path, Integer groupId) {
        files.put(path, groupId);
    }
}
