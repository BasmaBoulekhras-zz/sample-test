/*@Library('jenkins-pipeline-library')
import hudson.model.*
pipeline {
	agent { node { label 'maven' } }
	stages {
		stage('Init Pipeline') {
            steps {
                parallel (
                    "Parse WebHook": {
                        initPipeline()
                    },
                    "Init CICD": {
                        initCICD()
                    }
                )
            }
        }
        
        stage ("Checkout & Build") {
            steps {
                gitCheckout repoURL: repoURL, branch: branch, directory: appName, credentialsId: 'jenkins-gogs'
                
                dir("${appName}") {
                    mavenBuild()
                }
            }
        }
        
        stage('Unit & Integration Testing') {
            steps {
                dir("${appName}") {
                    mavenTest()
                }
            }
        }
        
        stage('Sonar Scan') {
            steps {
                dir("${appName}") {
                    mavenSonarScan()
                }
            }
        }
		
		stage('OWASP Scan') {
            steps {
                dir("${appName}") {
                    mavenOwaspScan()
                }
            }
        }
        
        stage('Push Artifacts') {
            when {
                expression { return branch == "develop" }
            }
            steps {
                dir("${appName}") {
                    mavenDeploy()
                }
            }
        }
		
		stage('Check Config Changes') {
            when {
                expression { return branch == "develop" }
            }
            steps {
                chechConfigChanges()
            }
        }
			  
        stage("Build Image") {
            when {
                expression { return branch == "develop" }
            }
            steps {
                buildDockerImage()
            }
        }
		
		stage('Cleanup Dev') {
			when {
				expression{ return (branch == "develop" && devChanged.toBoolean()) }
			}
			steps {
				cleanConfig(devProject)
			}
        }
		
        stage("Deploy to Dev") {
            when {
                expression { return branch == "develop" }
            }
            steps {
				sh "oc process -f cicd/iamp-service-config-dev.yaml -l commit=${cicdCommit} | oc create -f- -n ${devProject} || true"
				
				deployImage project: devProject, version: 'latest', replicas: 1
            }
        }
		
		stage('Run System Tests') {
			when {
				expression{ return branch == "develop" }
			}
			steps {
				dir("${appName}") {
					mavenSoapuiTests env: 'dev', goal: 'test'
				}
			}
        }
		
		stage ('Promote to Test') {			
			when {
                expression { return branch == "develop" }
            }
			steps {
				timeout(time:30, unit:'MINUTES') {
					input message: "Promote to Test?", ok: "Promote"
				}
				script{
					// Tag for Test
					openshift.withCluster() {
						openshift.tag("${devProject}/${appName}:latest", "${testProject}/${appName}:${version}")
                    }
				}
			}
		}
		
		stage('Cleanup Test') {
			when {
				expression{ return (branch == "develop" && testChanged.toBoolean()) }
			}
			steps {
				cleanConfig(testProject)
			}
        }
		
		stage("Deploy to Test") {
            when {
                expression { return branch == "develop" }
            }
            steps {
			
				sh "oc delete hpa -l app=${appName} -n ${testProject} || true"
				
				sh "oc process -f cicd/iamp-service-config-test.yaml -l commit=${cicdCommit} | oc create -f- -n ${testProject} || true"
				
				deployImage project: testProject, version: version, replicas: 2
            }
        }
		
		stage('Auto Scale') {
			when {
				expression{ return branch == "develop" }
			}
			steps {
				//Wait for CPU to idle
				sleep(time:60,unit:"SECONDS")
				
				// deploy AutoScaler
				sh "oc process -f cicd/service-autoscaler-cpu-template.yaml -p APP_NAME=${appName} -p REPLICAS_MIN=2 -p REPLICAS_MAX=4 -p CPU_THRESHOLD=80 -l app=${appName},commit=${cicdCommit} | oc create -f- -n ${testProject} || true"
			}
        }
		
		stage('Run Load Tests') {
			when {
				expression{ return branch == "develop" }
			}
			steps {
				dir("${appName}") {
					mavenSoapuiTests env: 'test', goal: 'loadtest'
				}
			}
        }
    }
	
	post('Publish Results') {
        always {
            slackBuildResult()
        }
    }
}*/ 

#!/usr/bin/groovy

def call() {
	sh "echo hello world"
}
