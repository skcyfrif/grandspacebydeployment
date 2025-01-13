pipeline {
    agent any

    environment {
        REGISTRY = "docker.io"
        IMAGE_NAME_BACKEND = "cyfdoc/grandspace-fullstack"
        DOCKER_CREDENTIALS_ID = 'cyfdoc' // Docker Hub credentials ID
        BUILD_TAG = "${env.BUILD_NUMBER}"
        SPRING_DATASOURCE_URL = "jdbc:mysql://db:3306/grandspace?createDatabaseIfNotExist=true"
        SPRING_DATASOURCE_USERNAME = "root"
        SPRING_DATASOURCE_PASSWORD = "root"
        DOCKER_NETWORK = "grandspace_network"
    }

    tools {
        maven 'maven'
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    echo "Cloning repository..."
                    checkout scm
                    echo "Listing files in workspace root..."
                    sh "ls -l"
                }
            }
        }

        stage('Build and Package') {
            steps {
                script {
                    echo "Building the application..."
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image..."
                    sh """
                    docker build -t ${REGISTRY}/${IMAGE_NAME_BACKEND}:${BUILD_TAG} .
                    """
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    echo "Pushing Docker image to Docker Hub..."
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        sh """
                        docker push ${REGISTRY}/${IMAGE_NAME_BACKEND}:${BUILD_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    echo "Deploying the application using Docker Compose..."
                    sh """
                    docker-compose down
                    docker-compose up -d
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "Build and deployment succeeded!"
        }
        failure {
            echo "Build or deployment failed. Check logs for details."
        }
    }
}
