package org.example;

import java.io.Serializable;

public class FunctionResult implements Serializable {
    private final float value;
    private int retries;
    private final FunctionResultStatus status;

    public float getValue() {
        return value;
    }

    public FunctionResultStatus getStatus() {
        return status;
    }

    void setRetries(int retries) {
        this.retries = retries;
    }

    int getRetries() {
        return retries;
    }

    public FunctionResult(float value, FunctionResultStatus status) {
        this.value = value;
        this.status = status;
    }
}
