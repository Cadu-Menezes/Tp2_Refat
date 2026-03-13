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
        boolean testsPassed = runTests(project);
        boolean deploySuccessful = testsPassed && deploy(project);
        sendEmailSummary(testsPassed, deploySuccessful);
    }

    private boolean runTests(Project project) {
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

    private boolean deploy(Project project) {
        String deploymentResult = project.deploy();
        boolean deploymentSucceeded = isSuccessful(deploymentResult);
        if (deploymentSucceeded) {
            log.info("Deployment successful");
            return true;
        }

        log.error("Deployment failed");
        return false;
    }

    private void sendEmailSummary(boolean testsPassed, boolean deploySuccessful) {
        boolean emailSummaryEnabled = config.sendEmailSummary();
        if (!emailSummaryEnabled) {
            log.info("Email disabled");
            return;
        }

        log.info("Sending email");

        String summaryMessage = buildSummaryMessage(testsPassed, deploySuccessful);
        emailer.send(summaryMessage);
    }

    private boolean isSuccessful(String result) {
        return SUCCESS.equals(result);
    }

    private String buildSummaryMessage(boolean testsPassed, boolean deploySuccessful) {
        if (!testsPassed) {
            return "Tests failed";
        }
        if (deploySuccessful) {
            return "Deployment completed successfully";
        }
        return "Deployment failed";
    }
}
