#!/usr/bin/groovy

def call() {

def project = 'green-dispatch-219519'
def  appName = 'sample-app'
def  imageTag = "gcr.io/${project}/${appName}:${env.BRANCH_NAME}.${env.BUILD_NUMBER}"

sh "PYTHONUNBUFFERED=1 gcloud builds submit -t ${imageTag} ."

}
