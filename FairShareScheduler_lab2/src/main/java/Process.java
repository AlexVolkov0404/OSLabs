import lombok.Getter;
import lombok.Setter;

public class Process {
    enum Status {
        GO,
        BLOCK,
        EXIT,
        ERROR
    }

    @Getter
    private final int id;

    @Getter @Setter
    private int blockTime;
    @Getter
    private final int groupId;

    @Getter
    private final int baseAddress;
    @Getter
    private final int addressSize;
    @Getter  @Setter
    private int instructionAddress;

    private final int priority;
    private int utilization;

    Process(int id, int groupId, int baseAddress, int addressSize, int priority) {
        this.id = id;

        this.blockTime = 0;
        this.groupId = groupId;

        this.addressSize = addressSize;
        this.baseAddress = baseAddress;
        this.instructionAddress = 0;

        this.priority = priority;
        this.utilization = 0;
    }

    int calculatePriority(int groupUtilization) {
        return priority + (utilization + groupUtilization) / 4;
    }

    void decrementBlockTime() {
        if(this.blockTime > 0) {
            this.blockTime--;
        }
    }

    public void incrementUtilization() {
        this.utilization++;
    }

    boolean isBlocked() {
        return this.blockTime > 0;
    }
}
