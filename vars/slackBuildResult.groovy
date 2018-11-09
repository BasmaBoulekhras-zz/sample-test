#!/usr/bin/groovy

def call() {
	
	if ( currentBuild.currentResult == "SUCCESS") {
		slack.success message: currentBuild.currentResult, appendBuildInfo: true
	}
	else if( currentBuild.currentResult == "UNSTABLE" || currentBuild.currentResult == "ABORTED" ) { 
		slack.warning message: currentBuild.currentResult, appendBuildInfo: true
	}
	else if( currentBuild.currentResult == "FAILURE" || currentBuild.currentResult == "NOT_BUILT" ) { 
		slack.failure message: currentBuild.currentResult, appendBuildInfo: true
	}
	else {
		slack.failure message: currentBuild.currentResult, appendBuildInfo: true
	}
}
