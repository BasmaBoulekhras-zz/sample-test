#!/usr/bin/groovy

def call() {
	env.groupId = readMavenPom().getGroupId()
	env.artifactId = readMavenPom().getArtifactId()
	env.version = readMavenPom().getVersion()
	env.name = readMavenPom().getName()
						
	sh "mvn clean package -DskipTests=true"
}
