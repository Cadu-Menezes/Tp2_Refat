package org.sammancoaching;

import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

class TestPhase {
    private static final String SUCCESS = "success";

    private final Logger log;

    TestPhase(Logger log) {
        this.log = log;
    }

    boolean execute(Project project) {
        if (!project.hasTests()) {
            log.info("No tests");
            return true;
        }

        if (SUCCESS.equals(project.runTests())) {
            log.info("Tests passed");
            return true;
        }

        log.error("Tests failed");
        return false;
    }
}
