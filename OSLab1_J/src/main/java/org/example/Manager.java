package org.example;

import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Manager {
    private final Lock lock = new ReentrantLock();

    private final Map<Float, FunctionResult> fResults = new HashMap<>();
    private final Map<Float, FunctionResult> gResults = new HashMap<>();

    private final long timeout;
    private final int maxRetriesCount;

    private CallbackFunction callback;

    public Manager(long timeout, int maxRetriesCount) {
        this.timeout = timeout;
        this.maxRetriesCount = maxRetriesCount;
    }

    public Promise<FunctionResult, FunctionResult, Exception> runFunction(float x, char function, Map<Float, FunctionResult> results) {
        DeferredObject<FunctionResult, FunctionResult, Exception> object = new DeferredObject<>();
        Promise<FunctionResult, FunctionResult, Exception> promise = object.promise();

        if (results.containsKey(x)) {
            object.resolve(results.get(x));
            return promise;
        }

        new Thread(() -> {
            FunctionResult result;
            int retries = -1;

            do {
                try {
                    String command = "java -jar src/main/resources/OSLab1.jar " + x + " " + function;
                    Process process = Runtime.getRuntime().exec(command);

                    if (process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
                        lock.lock();
                        InputStream sysIn = System.in;

                        File file = new File(function + "out.txt");
                        InputStream stream = new FileInputStream(file);
                        System.setIn(stream);

                        Scanner scanner = new Scanner(System.in);
                        String line = scanner.nextLine();
                        result = (FunctionResult) Utils.fromString(line);

                        System.setIn(sysIn);
                        lock.unlock();
                    } else {
                        process.destroy();
                        result = new FunctionResult(0, FunctionResultStatus.TIMEOUT);
                    }

                    results.put(x, result);
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ++retries;
            } while (result.getStatus() != FunctionResultStatus.SUCCESSFUL && result.getStatus() != FunctionResultStatus.SOFT_FAULT && retries < maxRetriesCount);

            result.setRetries(retries);
            object.resolve(result);
        }).start();

        return promise;
    }

    public ExecutionResult runFunctions(float x) throws InterruptedException {
        AtomicReference<FunctionResult> fResult = new AtomicReference<>();
        AtomicReference<FunctionResult> gResult = new AtomicReference<>();

        Promise<FunctionResult, FunctionResult, Exception> fPromise = runFunction(x, 'f', fResults);
        fPromise.done(result -> {
            fResult.set(result);
            callback.callback('f', result);
        });

        Promise<FunctionResult, FunctionResult, Exception> gPromise = runFunction(x, 'g', gResults);
        gPromise.done(result -> {
            gResult.set(result);
            callback.callback('g', result);
        });

        fPromise.waitSafely();
        gPromise.waitSafely();

        return new ExecutionResult(fResult.get(), gResult.get());
    }

    public void setCallback(CallbackFunction callback) {
        this.callback = callback;
    }
}
