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
                    taskDefinitionOverride "arn:aws:ecs:eu-west-2:${env.MANAGEMENT_ACCOUNT}:task-definition/sonatype-${params.STAGE}:1"
                }
            }
            steps {
                script {
                    sh "aws s3 cp s3://tdr-secrets/keys/sonatype.key /home/jenkins/sonatype.key"
                    sh "aws s3 cp s3://tdr-secrets/keys/sonatype_credential /home/jenkins/.sbt/sonatype_credential"
                    withCredentials([string(credentialsId: 'sonatype-gpg-passphrase', variable: 'PGP_PASSPHRASE')]) {
                        sh 'gpg --batch --passphrase $PGP_PASSPHRASE --import /home/jenkins/sonatype.key'
                        sh "sbt +compile +package +publishSigned +sonatypeBundleRelease"
                        slackSend color: "good", message: "The graphql client package has been published", channel: "#tdr"
                    }
                }
            }
        }
    }
}

def getAccountNumberFromStage() {
    def stageToAccountMap = [
            "intg": env.INTG_ACCOUNT,
            "staging": env.STAGING_ACCOUNT,
            "prod": env.PROD_ACCOUNT
    ]

    return stageToAccountMap.get(params.STAGE)
}
