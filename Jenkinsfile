pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'    
        maven 'M2_HOME'    
    }

    environment {
        SONAR_TOKEN = 'squ_38e27a2f6d49836a65b117cd550798a0648f61bb'
        SONAR_HOST_URL = 'http://localhost:9000'
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        DOCKER_IMAGE = 'ziedtabib-4TWIN2-G5-kaddem'
    }

    stages {
        stage('Cloner le projet') {
            steps {
                git branch: 'ZiedTabib-4TWIN2-G5',
                    url: 'https://github.com/ziedtabib/4TWIN2-G5-kaddem.git'
            }
        }

        stage('Compiler le projet') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Exécuter les tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('MVN SONARQUAR') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.login=squ_1a609967b2840ac670555bf59c7accf95ff0ed91 -Dmaven.test.skip=true'
            }
        } 

        stage('MVN Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
    
    stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', '756d7d06-7ce7-414c-a54a-4c505df44299') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push('latest')
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose up -d --build'
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker rmi ${DOCKER_IMAGE}:${env.BUILD_NUMBER} || true'
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
    }
}
