pipeline {
    agent any

    environment {
        REGISTRY = "docker.io"
        IMAGE_NAME_BACKEND = "grandspace-fullstackk"
        DOCKER_CREDENTIALS_ID = 'cyfdoc' // Docker Hub credentials ID
        BUILD_TAG = "${env.BUILD_NUMBER}"
        SPRING_DATASOURCE_URL = "jdbc:mysql://172.22.0.2:3306/grandspace?createDatabaseIfNotExist=true"
        SPRING_DATASOURCE_USERNAME = "root"
        SPRING_DATASOURCE_PASSWORD = "root"
        DOCKER_NETWORK = "grandspace_networkk"
        DB_CONTAINER = "grandspace-dbb"
        PHPMYADMIN_CONTAINER = "grandspace-phpmyadminn"
        BACKEND_CONTAINER = "grandspace-containerr"
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
                    echo "Listing files in the workspace root for debugging..."
                    sh "ls -l"
                    echo "Checking if GrandSpaceProject directory exists..."
                    sh "ls -l GrandSpaceProject || echo 'GrandSpaceProject directory not found!'"
                }
            }
        }

        stage('Build GrandSpaceProject') {
            steps {
                script {
                    echo "Navigating to the project directory..."
                    sh "ls -l" // Debugging: Verify files in the workspace root
                    dir('GrandSpaceProject') {
                        script {
                            echo "Building GrandSpaceProject..."
                            sh "ls -l" // Debugging: Verify presence of pom.xml
                            sh 'mvn clean install -X' // Verbose Maven build logs
                            echo "Building Docker image for GrandSpaceProject..."
                            sh """
                            docker build -t ${REGISTRY}/${IMAGE_NAME_BACKEND}:${BUILD_TAG} .
                            """
                        }
                    }
                }
            }
        }

        stage('Create Docker Network') {
            steps {
                script {
                    echo "Creating Docker network if not exists..."
                    sh """
                    docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || docker network create ${DOCKER_NETWORK}
                    """
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    echo "Starting MySQL container..."
                    sh """
                    if docker ps --filter "name=${DB_CONTAINER}" | grep -q ${DB_CONTAINER}; then
                        echo "Container ${DB_CONTAINER} is already running."
                    elif docker ps -a --filter "name=${DB_CONTAINER}" | grep -q ${DB_CONTAINER}; then
                        echo "Container ${DB_CONTAINER} exists but is stopped. Restarting it..."
                        docker start ${DB_CONTAINER}
                    else
                        echo "Creating and starting a new MySQL container..."
                        docker run -d --name ${DB_CONTAINER} \
                            --network ${DOCKER_NETWORK} \
                            -e MYSQL_ROOT_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                            -e MYSQL_DATABASE=grandspace \
                            -p 3408:3306 mysql:5.7
                    fi
                    """
                }
            }
        }

        stage('Start phpMyAdmin') {
            steps {
                script {
                    echo "Starting phpMyAdmin container..."
                    sh """
                    docker ps -q --filter "name=${PHPMYADMIN_CONTAINER}" | grep -q . || \
                    docker run -d --name ${PHPMYADMIN_CONTAINER} \
                        --network ${DOCKER_NETWORK} \
                        -e PMA_HOST=${DB_CONTAINER} \
                        -e MYSQL_ROOT_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                        -p 8568:80 phpmyadmin/phpmyadmin
                    """
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                script {
                    echo "Logging in to Docker Hub..."
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        echo "Docker login successful"
                    }
                }
            }
        }

        stage('Push Images to Docker Hub') {
            steps {
                script {
                    echo "Pushing Docker images to Docker Hub..."
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        sh """
                        docker tag ${REGISTRY}/${IMAGE_NAME_BACKEND}:${BUILD_TAG} ${REGISTRY}/cyfrifprotech/${IMAGE_NAME_BACKEND}:${BUILD_TAG}
                        docker push ${REGISTRY}/cyfrifprotech/${IMAGE_NAME_BACKEND}:${BUILD_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy Grandspace') {
            steps {
                script {
                    echo "Updating GrandSpaceProject container..."
                    sh """
                    docker ps -a -q --filter "name=${BACKEND_CONTAINER}" | grep -q . && \
                    docker rm -f ${BACKEND_CONTAINER} || echo "No existing backend container to remove."

                    docker run -d --name ${BACKEND_CONTAINER} \
                        --network ${DOCKER_NETWORK} \
                        -e SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} \
                        -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
                        -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                        -p 9066:9555 ${REGISTRY}/cyfrifprotech/${IMAGE_NAME_BACKEND}:${BUILD_TAG}
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
