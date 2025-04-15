pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKER_IMAGE = 'ziedtabib/ziedtabib-4twin2-g5-kaddem'
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        JAR_FILE = 'target/4TWIN2-G5-kaddem-1.0.0.jar'
        // Utilisation du credentialsId visible dans votre image
        DOCKER_CREDS = credentials('docker-hub-creds')
    }

    options {
        timestamps()
        skipDefaultCheckout()
    }

    stages {
        stage('Cloner le projet') {
            steps {
                checkout scm
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
                sh 'mvn sonar:sonar -Dsonar.login=squ_e7b1e0ea23c135bca1f1f969b1d44ee73340030b -Dmaven.test.skip=true'
            }
        }

        stage('Nexus Deploy') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                sh 'ls -l target/'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "test -f ${JAR_FILE} || { echo 'Fichier JAR introuvable'; exit 1; }"
                    echo "Building Docker image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}", "--build-arg JAR_FILE=${JAR_FILE} .")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Vérification de l'image locale
                    sh "docker images | grep ${DOCKER_IMAGE}"

                    // Authentification avec les credentials de votre image
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-creds',
                        usernameVariable: 'ziedtabib',
                        passwordVariable: 'zied@1234'
                    )]) {
                        sh '''
                            echo "Authentification sur Docker Hub..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin || {
                                echo "Échec de l'authentification Docker Hub";
                                exit 1;
                            }
                        '''
                    }

                    // Push de l'image
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-creds') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                        // Tag et push de la version latest
                        sh "docker tag ${DOCKER_IMAGE}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh """
                        docker-compose down || true
                        BUILD_NUMBER=${env.BUILD_NUMBER} docker-compose up -d --build
                    """
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    sh "docker rmi ${DOCKER_IMAGE}:${env.BUILD_NUMBER} || true"
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline exécuté avec succès !"
            slackSend(color: 'good', message: "Build ${env.BUILD_NUMBER} réussi - ${env.JOB_NAME}")
        }
        failure {
            echo "❌ Échec du pipeline"
            slackSend(color: 'danger', message: "Échec du build ${env.BUILD_NUMBER} - ${env.JOB_NAME}")
        }
        always {
            cleanWs()
            script {
                // Nettoyage des images intermédiaires
                sh 'docker system prune -f || true'
            }
        }
    }
}
