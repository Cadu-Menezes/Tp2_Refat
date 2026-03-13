package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;

class EmailSummaryPhase {
    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    EmailSummaryPhase(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    void execute(PipelineExecutionResult executionResult) {
        if (!config.sendEmailSummary()) {
            log.info("Email disabled");
            return;
        }

        log.info("Sending email");
        emailer.send(buildSummaryMessage(executionResult));
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
}
