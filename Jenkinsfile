library("tdr-jenkinslib")

def versionBumpBranch = "version-bump-${BUILD_NUMBER}-${params.VERSION}"

pipeline {
    agent {
        label "master"
    }
    parameters {
        choice(name: "STAGE", choices: ["intg", "staging", "prod"], description: "The stage you are deploying the graphql library to")
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
                tdr.configureJenkinsGitUser()
              }

              sh "git checkout ${versionBumpBranch}"

              sshagent(['github-jenkins']) {
                sh "sbt +'release with-defaults'"
              }

              slackSend color: "good", message: "*GraphQL client* :arrow_up: The GraphQL client library has been published", channel: "#tdr-releases"

              script {
                tdr.pushGitHubBranch(versionBumpBranch)
              }
            }
        }
        stage("Create version bump pull request") {
          agent {
            label "master"
          }
          steps {
            script {
              tdr.createGitHubPullRequest(
                pullRequestTitle: "Version Bump from build number ${BUILD_NUMBER}",
                buildUrl: env.BUILD_URL,
                repo: "tdr-graphql-client",
                branchToMergeTo: "master",
                branchToMerge: versionBumpBranch
              )
            }
          }
        }
    }
}
