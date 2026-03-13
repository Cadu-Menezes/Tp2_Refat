package org.sammancoaching;

import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Logger;
import org.sammancoaching.dependencies.Project;

public class Pipeline {
    private final PipelineRunner runner;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.runner = new PipelineRunner(
                new TestPhase(log),
                new DeploymentPhase(log),
                new EmailSummaryPhase(config, emailer, log)
        );
    }

    public void run(Project project) {
        runner.run(project);
    }
}
