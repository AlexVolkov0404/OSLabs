package org.example;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Manager manager = new Manager(Long.parseLong(args[0]), Integer.parseInt(args[1]));
        manager.setCallback(getCallbackFunction());

        while (true) {
            System.out.println("Введіть (x), щоб вийти");
            System.out.println("Введіть число, щоб запустити функції");

            Scanner scanner = new Scanner(System.in);
            String string = scanner.nextLine();

            if (Objects.equals(string, "x")) {
                break;
            }

            if (Utils.isNumeric(string)) {
                float x = Float.parseFloat(string);
                ExecutionResult result = manager.runFunctions(x);

                if (result.getStatus() == ExecutionResultStatus.SUCCESSFUL) {
                    System.out.println("Виконання успішне");
                    System.out.println("Результат: " + result.getValue());
                } else if (result.getStatus() == ExecutionResultStatus.F_FAILED) {
                    System.out.println("F виконання НЕ успішне");
                } else if (result.getStatus() == ExecutionResultStatus.G_FAILED) {
                    System.out.println("G виконання НЕ успішне");
                } else {
                    System.out.println("Обидві функції виконалися НЕ успішно");
                }
            }
        }
    }

    private static CallbackFunction getCallbackFunction() {
        ReentrantLock lock = new ReentrantLock();
        return (function, result) -> {
            lock.lock();
            if (result.getStatus() == FunctionResultStatus.SUCCESSFUL) {
                System.out.println(Character.toString(function).toUpperCase() + " виконання успішне");
                System.out.println(Character.toString(function).toUpperCase() + " значення: " + result.getValue());
            } else {
                System.out.println(Character.toString(function).toUpperCase() + " виконання НЕ успішне");
            }
            System.out.println(Character.toString(function).toUpperCase() + " має повторів: " + result.getRetries());
            lock.unlock();
        };
    }


}