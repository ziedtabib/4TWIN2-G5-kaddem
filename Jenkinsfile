pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKER_IMAGE = 'ziedtabib/ziedtabib-4twin2-g5-kaddem'
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
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
            }
        }

stage('Build Docker Image') {
    steps {
        script {
            // Find the JAR file that was built
            def jarFile = sh(script: 'ls target/*.jar', returnStdout: true).trim()
            echo "Building Docker image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
            docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}", "--build-arg JAR_FILE=${jarFile} .")
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
                sh 'docker-compose down || true'
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
            cleanWs()
        }
    }
}
