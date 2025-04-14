pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKER_IMAGE = 'ziedtabib/ziedtabib-4twin2-g5-kaddem'
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        JAR_FILE = 'target/4TWIN2-G5-kaddem-1.0.0.jar' // Définition explicite du nom du JAR
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
                // Vérification que le JAR a bien été généré
                sh 'ls -l target/'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Vérification explicite du fichier JAR
                    sh "test -f ${JAR_FILE} || { echo 'Fichier JAR introuvable'; exit 1; }"

                    echo "Building Docker image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"

                    // Construction avec le chemin absolu du JAR
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}", "--build-arg JAR_FILE=${JAR_FILE} .")
                }
            }
        }


        stage('Push to Docker Hub') {
            steps {
                script {
                    // Vérifiez que l'image existe localement
                    sh "docker images | grep ${DOCKER_IMAGE}"

                    // Tentez une connexion test
                    withCredentials([usernamePassword(
                        credentialsId: '8e0cf094-a5ab-49a7-9410-c1114dfd04ec',
                        usernameVariable: 'ziedtabib',
                        passwordVariable: 'zied@1234'
                    )]) {
                        sh '''
                            echo "Trying to login to Docker Hub..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        '''
                    }

                    // Push seulement si le login réussit
                    docker.withRegistry('https://index.docker.io/v1/', '8e0cf094-a5ab-49a7-9410-c1114dfd04ec') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Utilisation de la variable d'environnement correctement
                    sh """
                        export BUILD_NUMBER=${env.BUILD_NUMBER}
                        docker-compose down || true
                        docker-compose up -d --build
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
            echo "✅ Pipeline terminé avec succès !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            cleanWs()
        }
    }
}
