package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

public class Pipeline {
    
    private static final String SUCCESS = "success";
    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    public void run(Project project) {
        PipelineExecutionResult executionResult = executePipeline(project);
        sendEmailSummary(executionResult);
    }

    private PipelineExecutionResult executePipeline(Project project) {
        boolean testsPassed = evaluateTests(project);
        boolean deploymentSuccessful = testsPassed && executeProductionDeployment(project);
        return new PipelineExecutionResult(testsPassed, deploymentSuccessful);
    }

    private boolean evaluateTests(Project project) {
        boolean projectHasNoTests = !project.hasTests();
        if (projectHasNoTests) {
            log.info("No tests");
            return true;
        }

        String testResult = project.runTests();
        boolean testsPassed = isSuccessful(testResult);
        if (testsPassed) {
            log.info("Tests passed");
            return true;
        }

        log.error("Tests failed");
        return false;
    }

    private boolean executeProductionDeployment(Project project) {
        String deploymentResult = project.deploy();
        boolean deploymentSucceeded = isSuccessful(deploymentResult);
        if (deploymentSucceeded) {
            log.info("Deployment successful");
            return true;
        }

        log.error("Deployment failed");
        return false;
    }

    private void sendEmailSummary(PipelineExecutionResult executionResult) {
        boolean emailSummaryEnabled = config.sendEmailSummary();
        if (!emailSummaryEnabled) {
            log.info("Email disabled");
            return;
        }

        log.info("Sending email");

        String summaryMessage = buildSummaryMessage(executionResult);
        emailer.send(summaryMessage);
    }

    private boolean isSuccessful(String result) {
        return SUCCESS.equals(result);
    }

    private String buildSummaryMessage(PipelineExecutionResult executionResult) {
        if (!executionResult.testsPassed()) {
            return "Tests failed";
        }
        if (executionResult.deploymentSuccessful()) {
            return "Deployment completed successfully";
        }
        return "Deployment failed";
    }

    private static class PipelineExecutionResult {
        private final boolean testsPassed;
        private final boolean deploymentSuccessful;

        private PipelineExecutionResult(boolean testsPassed, boolean deploymentSuccessful) {
            this.testsPassed = testsPassed;
            this.deploymentSuccessful = deploymentSuccessful;
        }

        private boolean testsPassed() {
            return testsPassed;
        }

        private boolean deploymentSuccessful() {
            return deploymentSuccessful;
        }
    }
}
