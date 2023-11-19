import lombok.Getter;

public class Memory {
    private static final int MEM_SIZE = 65536;
    private static int[] memory = new int[MEM_SIZE];

    private static int processBaseAddress;
    private static int processBoundSize;

    @Getter
    private static int freeAddress = 0;

    public static void loadProcess(Process process) {
        processBaseAddress = process.getBaseAddress();
        processBoundSize = process.getAddressSize();
    }

    public static int readByte(int address) throws Exception {
        if (address < 0 || address >= processBoundSize) {
            System.out.println("Illegal address " + address);
            throw new Exception("Address Error");
        }

        return memory[processBaseAddress + address];
    }

    public static void put(int address, int value) {
        memory[address] = value;
    }

    public static int claim(int size) {
        freeAddress += size;
        return freeAddress - size;
    }
}
