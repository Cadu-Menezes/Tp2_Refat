package org.sammancoaching;

import org.sammancoaching.dependencies.Project;

class PipelineRunner {
    private final TestPhase testPhase;
    private final DeploymentPhase deploymentPhase;
    private final EmailSummaryPhase emailSummaryPhase;

    PipelineRunner(TestPhase testPhase, DeploymentPhase deploymentPhase, EmailSummaryPhase emailSummaryPhase) {
        this.testPhase = testPhase;
        this.deploymentPhase = deploymentPhase;
        this.emailSummaryPhase = emailSummaryPhase;
    }

    void run(Project project) {
        boolean testsPassed = testPhase.execute(project);
        boolean deploymentSuccessful = testsPassed && deploymentPhase.execute(project);

        PipelineExecutionResult executionResult = new PipelineExecutionResult(testsPassed, deploymentSuccessful);
        emailSummaryPhase.execute(executionResult);
    }
}
