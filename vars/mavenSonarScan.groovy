#!/usr/bin/groovy

def call() {
	
	env.sonarProjectKey = (groupId + ":" + artifactId + ":" + branch).replace("/", ":")
	env.sonarProjectName = name + " (Branch: " + branch + ")"
	
	withSonarQubeEnv('Sonar') {
		sh "mvn sonar:sonar -DskipTests=true -Dsonar.projectKey=\"$sonarProjectKey\" -Dsonar.projectName=\"$sonarProjectName\""
	}

	timeout(time: 5, unit: 'MINUTES') {
		def qg = waitForQualityGate()
		if (qg.status != 'OK') {
			error "Pipeline aborted due to quality gate failure: ${qg.status}"
		}
	}
}
