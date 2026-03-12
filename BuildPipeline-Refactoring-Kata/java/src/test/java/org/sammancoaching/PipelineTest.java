package org.sammancoaching;

import org.junit.jupiter.api.Test;
import org.sammancoaching.dependencies.Config;
import org.sammancoaching.dependencies.Emailer;
import org.sammancoaching.dependencies.Project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sammancoaching.dependencies.TestStatus.FAILING_TESTS;
import static org.sammancoaching.dependencies.TestStatus.NO_TESTS;
import static org.sammancoaching.dependencies.TestStatus.PASSING_TESTS;

class PipelineTest {

    @Test
    void sendsSuccessEmailWhenTestsPassAndProductionDeploymentSucceeds() {
    Emailer emailer = mock(Emailer.class);
    Config config = mock(Config.class);
    when(config.sendEmailSummary()).thenReturn(true);
    CapturingLogger logger = new CapturingLogger();
    Pipeline pipeline = new Pipeline(config, emailer, logger);

    Project project = Project.builder()
        .setTestStatus(PASSING_TESTS)
        .setDeploysSuccessfully(true)
        .build();

    pipeline.run(project);

    verify(emailer).send("Deployment completed successfully");
    assertThat(logger.getLoggedLines()).containsExactly(
        "INFO: Tests passed",
        "INFO: Deployment successful",
        "INFO: Sending email"
    );
    }

    @Test
    void sendsFailureEmailWhenTestsFailAndSkipsDeployment() {
    Emailer emailer = mock(Emailer.class);
    Config config = mock(Config.class);
    when(config.sendEmailSummary()).thenReturn(true);
    CapturingLogger logger = new CapturingLogger();
    Pipeline pipeline = new Pipeline(config, emailer, logger);

    Project project = Project.builder()
        .setTestStatus(FAILING_TESTS)
        .setDeploysSuccessfully(true)
        .build();

    pipeline.run(project);

    verify(emailer).send("Tests failed");
    assertThat(logger.getLoggedLines()).containsExactly(
        "ERROR: Tests failed",
        "INFO: Sending email"
    );
    }

    @Test
    void treatsProjectsWithoutTestsAsEligibleForDeployment() {
    Emailer emailer = mock(Emailer.class);
    Config config = mock(Config.class);
    when(config.sendEmailSummary()).thenReturn(true);
    CapturingLogger logger = new CapturingLogger();
    Pipeline pipeline = new Pipeline(config, emailer, logger);

    Project project = Project.builder()
        .setTestStatus(NO_TESTS)
        .setDeploysSuccessfully(false)
        .build();

    pipeline.run(project);

    verify(emailer).send("Deployment failed");
    assertThat(logger.getLoggedLines()).containsExactly(
        "INFO: No tests",
        "ERROR: Deployment failed",
        "INFO: Sending email"
    );
    }

    @Test
    void doesNotSendEmailWhenSummaryIsDisabled() {
    Emailer emailer = mock(Emailer.class);
    Config config = mock(Config.class);
    when(config.sendEmailSummary()).thenReturn(false);
    CapturingLogger logger = new CapturingLogger();
    Pipeline pipeline = new Pipeline(config, emailer, logger);

    Project project = Project.builder()
        .setTestStatus(PASSING_TESTS)
        .setDeploysSuccessfully(true)
        .build();

    pipeline.run(project);

    verify(emailer, never()).send(org.mockito.ArgumentMatchers.any());
    assertThat(logger.getLoggedLines()).containsExactly(
        "INFO: Tests passed",
        "INFO: Deployment successful",
        "INFO: Email disabled"
    );
    }
}
