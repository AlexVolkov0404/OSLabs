package org.example;

import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.Random;

public class Functions {
    static Map<String, Funcition> functions = new HashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File(args[1] + "out.txt");
        file.createNewFile();

        PrintStream stream = new PrintStream(new FileOutputStream(file));
        System.setOut(stream);

        functions.put("f", (value) -> {
            Random random = new Random(System.nanoTime());
            Thread.sleep(500 + random.nextInt(0, 1000));
            return new FunctionResult(value * value, FunctionResultStatus.SUCCESSFUL);
        });
        functions.put("g", (value) -> new FunctionResult(value * value * value, FunctionResultStatus.SUCCESSFUL));

        float x = Float.parseFloat(args[0]);
        FunctionResult result = functions.get(args[1]).run(x);

        System.out.println(Utils.toString(result));
    }
}
