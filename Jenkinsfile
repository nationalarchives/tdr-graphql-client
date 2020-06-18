pipeline {
    agent {
        label "master"
    }
    parameters {
        choice(name: "STAGE", choices: ["intg", "staging", "prod"], description: "The stage you are building the front end for")
    }
    stages {
        stage("Deploy to sonatype") {
            agent {
                ecs {
                    inheritFrom "base"
                    taskDefinitionOverride "arn:aws:ecs:eu-west-2:${env.MANAGEMENT_ACCOUNT}:task-definition/s3publish-${params.STAGE}:2"
                }
            }
            steps {
                script {
                    sshagent(['github-jenkins']) {
                        sh "git push --set-upstream origin ${env.GIT_LOCAL_BRANCH}"
                        sh 'git config --global user.email tna-digital-archiving-jenkins@nationalarchives.gov.uk'
                        sh 'git config --global user.name tna-digital-archiving-jenkins'
                        sh "sbt +'release with-defaults'"
                    }
                    slackSend color: "good", message: "*GraphQL client* :arrow_up: The GraphQL client library has been published", channel: "#tdr-releases"

                }
            }
        }
    }
}
