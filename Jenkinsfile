pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        SONAR_TOKEN = credentials('squ_e7b1e0ea23c135bca1f1f969b1d44ee73340030b')
        SONAR_HOST_URL = 'http://localhost:9000'
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        DOCKER_IMAGE = 'ziedtabib/4twin2-g5-kaddem'
    }

    options {
        timestamps() // Add timestamps for better debugging
        skipDefaultCheckout() // Avoid duplicate checkout
    }

    stages {
        stage('Cloner le projet') {
            steps {
                checkout scm // Use SCM config from Jenkins job
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { // Use Jenkins SonarQube plugin config
                    sh "mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=${SONAR_HOST_URL} -Dmaven.test.skip=true"
                }
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
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', '756d7d06-7ce7-414c-a54a-4c505df44299') {
                        def dockerImage = docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                        dockerImage.push()
                        dockerImage.push('latest')
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh "export BUILD_NUMBER=${env.BUILD_NUMBER}"
                sh 'docker-compose down || true' // Prevent failure if no containers exist
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
        always {
            cleanWs() // Clean workspace after run
        }
    }
}
