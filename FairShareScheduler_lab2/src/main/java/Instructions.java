import java.util.Random;

public class Instructions {
    public static final int COMPUTE = 1;
    public static final int IO_BLOCK = 2;
    public static final int EXIT = 3;
    public static final int BRANCH_MIN = 128;
    public static final int BRANCH_MAX = 228;

    private static final Random random = new Random();

    public static boolean isBranching(int instruction) {
        int chance = instruction - BRANCH_MIN;
        return random.nextInt(100) <= chance;
    }

    public static int calculateBlockWait() {
        int IO_MIN_CYCLES = 50;
        int IO_DEVIATION_CYCLES = 100;
        return (int) (IO_MIN_CYCLES + IO_DEVIATION_CYCLES * Math.abs(random.nextGaussian()));
    }
}
