package org.example;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Manager manager = new Manager(Long.parseLong(args[0]), Integer.parseInt(args[1]));
        manager.setCallback(getCallbackFunction());

        while (true) {
            System.out.println("Enter (q) to exit");
            System.out.println("Enter number to run functions");

            Scanner scanner = new Scanner(System.in);
            String string = scanner.nextLine();

            if (Objects.equals(string, "q")) {
                break;
            }

            if (Utils.isNumeric(string)) {
                float x = Float.parseFloat(string);
                ExecutionResult result = manager.runFunctions(x);

                if (result.getStatus() == ExecutionResultStatus.SUCCESSFUL) {
                    System.out.println("Execution was successful");
                    System.out.println("Value: " + result.getValue());
                } else if (result.getStatus() == ExecutionResultStatus.F_FAILED) {
                    System.out.println("F execution was not successful");
                } else if (result.getStatus() == ExecutionResultStatus.G_FAILED) {
                    System.out.println("G execution was not successful");
                } else {
                    System.out.println("Both execution was not successful");
                }
            }
        }
    }

    private static CallbackFunction getCallbackFunction() {
        ReentrantLock lock = new ReentrantLock();
        return (function, result) -> {
            lock.lock();
            if (result.getStatus() == FunctionResultStatus.SUCCESSFUL) {
                System.out.println(Character.toString(function).toUpperCase() + " execution was successful");
                System.out.println(Character.toString(function).toUpperCase() + " value: " + result.getValue());
            } else {
                System.out.println(Character.toString(function).toUpperCase() + " execution not was successful");
            }
            System.out.println(Character.toString(function).toUpperCase() + " had retries: " + result.getRetries());
            lock.unlock();
        };
    }
}