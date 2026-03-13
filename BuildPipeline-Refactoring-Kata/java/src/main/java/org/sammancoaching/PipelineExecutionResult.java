package org.sammancoaching;

class PipelineExecutionResult {
    private final boolean testsPassed;
    private final boolean deploymentSuccessful;

    PipelineExecutionResult(boolean testsPassed, boolean deploymentSuccessful) {
        this.testsPassed = testsPassed;
        this.deploymentSuccessful = deploymentSuccessful;
    }

    boolean testsPassed() {
        return testsPassed;
    }

    boolean deploymentSuccessful() {
        return deploymentSuccessful;
    }
}
