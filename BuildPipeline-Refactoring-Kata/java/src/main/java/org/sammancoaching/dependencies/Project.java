package org.sammancoaching.dependencies;

import static org.sammancoaching.dependencies.TestStatus.NO_TESTS;
import static org.sammancoaching.dependencies.TestStatus.PASSING_TESTS;

public class Project {
    
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    private final DeploymentSettings deploymentSettings;
    private final TestSettings testSettings;

    public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    private Project(DeploymentSettings deploymentSettings, TestSettings testSettings) {
        this.deploymentSettings = deploymentSettings;
        this.testSettings = testSettings;
    }

    public boolean hasTests() {
        return testSettings.unitTestStatus != NO_TESTS;
    }

    public String runTests() {
        boolean testsArePassing = testSettings.unitTestStatus == PASSING_TESTS;
        return statusFor(testsArePassing);
    }

    public String deploy() {
        return deploy(DeploymentEnvironment.PRODUCTION);
    }

    public String deploy(DeploymentEnvironment environment) {
        boolean deployingToStaging = environment == DeploymentEnvironment.STAGING;
        boolean deploymentSucceeded = deployingToStaging
            ? deploymentSettings.stagingDeploymentSuccessful
            : deploymentSettings.productionDeploymentSuccessful;

        switch (environment) {
            case STAGING:
            case PRODUCTION:
                return statusFor(deploymentSucceeded);
            default:
                return FAILURE;
        }
    }

    public TestStatus runSmokeTests() {
        return testSettings.smokeTestStatus;
    }

    public static class ProjectBuilder {
        private boolean deploysSuccessfully;
        private TestStatus testStatus;
        private boolean deploysSuccessfullyToStaging = false;
        private TestStatus smokeTestStatus = NO_TESTS;

        public ProjectBuilder setTestStatus(TestStatus testStatus) {
            this.testStatus = testStatus;
            return this;
        }

        public ProjectBuilder setSmokeTestStatus(TestStatus smokeTestStatus) {
            this.smokeTestStatus = smokeTestStatus;
            return this;
        }

        public ProjectBuilder setDeploysSuccessfully(boolean deploysSuccessfully) {
            this.deploysSuccessfully = deploysSuccessfully;
            return this;
        }

        public ProjectBuilder setDeploysSuccessfullyToStaging(boolean deploysSuccessfully) {
            this.deploysSuccessfullyToStaging = deploysSuccessfully;
            return this;
        }

        public Project build() {
            DeploymentSettings deploymentSettings = new DeploymentSettings(deploysSuccessfully, deploysSuccessfullyToStaging);
            TestSettings testSettings = new TestSettings(testStatus, smokeTestStatus);
            return new Project(deploymentSettings, testSettings);
        }
    }

    private static class DeploymentSettings {
        private final boolean productionDeploymentSuccessful;
        private final boolean stagingDeploymentSuccessful;

        private DeploymentSettings(boolean productionDeploymentSuccessful, boolean stagingDeploymentSuccessful) {
            this.productionDeploymentSuccessful = productionDeploymentSuccessful;
            this.stagingDeploymentSuccessful = stagingDeploymentSuccessful;
        }
    }

    private static class TestSettings {
        private final TestStatus unitTestStatus;
        private final TestStatus smokeTestStatus;

        private TestSettings(TestStatus unitTestStatus, TestStatus smokeTestStatus) {
            this.unitTestStatus = unitTestStatus;
            this.smokeTestStatus = smokeTestStatus;
        }
    }

    private String statusFor(boolean operationSucceeded) {
        return operationSucceeded ? SUCCESS : FAILURE;
    }
}
