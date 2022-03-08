pipeline {
     agent any
     stages {
        stage("Build") {
            steps {
                sh "npm install"
                sh "npm run build"
                sh "pwd" 
            }
        }
        stage("Deploy") {
            steps{
            sshagent(['edu-server-creds']) {
                sh """ 
                scp -r -o StrictHostKeyChecking=no ${WORKSPACE}/build/ ubuntu@ip-IPAddress:/home/ubuntu/frontend_build/
                """                
                }
            }
        }
    }
}                
   
