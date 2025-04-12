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
                sh 'mvn sonar:sonar -Dsonar.login=squ_eec056176572f02fe6565e157539b9d4c57baf15 -Dmaven.test.skip=true'
            }
        } 

        stage('MVN Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
    } // Fin du bloc stages

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
    }
}
