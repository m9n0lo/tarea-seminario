pipeline {

    environment {
        BASE_GIT_URL = 'https://github.com/m9n0lo'
        APP_REPO_URL = "${env.BASE_GIT_URL}/${nombre_repo}.git"
        INFRA_REPO_URL = "${env.BASE_GIT_URL}/tarea-seminario.git"
        DOCKER_IMAGE = "m9n0lo/${nombre_repo}"
        DEPLOY_FOLDER = "deploy/kubernate/${nombre_repo}"
    }

    agent any
    stages {
        stage("Checkout app-code") {
            steps {
            //se esta crando una carpeta....
               dir('app') {
                    git url:"${env.APP_REPO_URL}" , branch: "${version}"
                } 
            }
        }
         
         stage("Checkout deploy-code") {
            steps {
               dir('deploy') {
                    git url:"${env.INFRA_REPO_URL}" , branch: "master"
                } 
            }
        }
        
         stage("Build image") {
            steps {
                dir('app') {
                    script {
                        dockerImage = docker.build("${env.DOCKER_IMAGE}:${tag}")
                    }
                }
            }
        }
        
        stage("Push image") {
            steps {
                script {
                    docker.withRegistry('', 'manueldocker') {
                    dockerImage.push()
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps{
                sh "sed -i 's:DOCKER_IMAGE:${env.DOCKER_IMAGE}:g' ${DEPLOY_FOLDER}/deployment.yaml"
                sh "sed -i 's:TAG:${tag}:g' ${DEPLOY_FOLDER}/deployment.yaml"
                
                step([$class: 'KubernetesEngineBuilder', 
                        projectId: "nice-root-288300",
                        clusterName: "cluster-manuel",
                        zone: "us-central1-c",
                        manifestPattern: "${DEPLOY_FOLDER}/",
                        credentialsId: "seminario",
                        verifyDeployments: true])
            }
        }
    }
}