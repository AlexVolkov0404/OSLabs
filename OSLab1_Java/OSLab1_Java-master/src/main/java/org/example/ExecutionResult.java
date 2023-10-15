package org.example;

public class ExecutionResult {
    private final float value;
    private final ExecutionResultStatus status;
    private final FunctionResult fResult;
    private final FunctionResult gResult;

    public ExecutionResult(FunctionResult fResult, FunctionResult gResult) {
        this.value = fResult.getValue() + gResult.getValue();

        if (fResult.getStatus() == FunctionResultStatus.SUCCESSFUL && gResult.getStatus() == FunctionResultStatus.SUCCESSFUL) {
            this.status = ExecutionResultStatus.SUCCESSFUL;
        } else if (fResult.getStatus() != FunctionResultStatus.SUCCESSFUL && gResult.getStatus() == FunctionResultStatus.SUCCESSFUL) {
            this.status = ExecutionResultStatus.F_FAILED;
        } else if (fResult.getStatus() == FunctionResultStatus.SUCCESSFUL && gResult.getStatus() != FunctionResultStatus.SUCCESSFUL) {
            this.status = ExecutionResultStatus.G_FAILED;
        } else {
            this.status = ExecutionResultStatus.BOTH_FAILED;
        }

        this.fResult = fResult;
        this.gResult = gResult;
    }

    ExecutionResultStatus getStatus() {
        return status;
    }

    public float getValue() {
        return value;
    }

    FunctionResult getFResult() {
        return fResult;
    }

    public FunctionResultStatus getFStatus() {
        return fResult.getStatus();
    }

    public float getFValue() {
        return fResult.getValue();
    }

    FunctionResult getGResult() {
        return gResult;
    }

    public FunctionResultStatus getGStatus() {
        return gResult.getStatus();
    }

    public float getGValue() {
         return gResult.getValue();
    }
}
