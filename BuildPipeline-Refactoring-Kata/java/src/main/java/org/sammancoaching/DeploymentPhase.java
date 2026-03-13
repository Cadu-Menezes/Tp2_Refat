package org.sammancoaching;

import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

class DeploymentPhase {
    private static final String SUCCESS = "success";

    private final Logger log;

    DeploymentPhase(Logger log) {
        this.log = log;
    }

    boolean execute(Project project) {
        if (SUCCESS.equals(project.deploy())) {
            log.info("Deployment successful");
            return true;
        }

        log.error("Deployment failed");
        return false;
    }
}
