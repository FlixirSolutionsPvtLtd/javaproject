
pipeline {
    agent any
    // environment {
    //  PATH = "/usr/bin/mvn:$PATH"
    // }
    stages {
        stage("git"){
            steps{
                git branch: 'main', url: 'https://github.com/FlixirSolutionsPvtLtd/javaproject.git'
            }
        }
        stage("build"){
            steps{
                  sh "mvn clean install"
                sh "pwd"
            }
        }
        stage("deploy"){
            steps{
                // sh "rm /home/ubuntu/api-0.0.1-SNAPSHOT.jar"
                // sh 'chown -R root:jenkins target/'
                // sh "cp -f target/*.jar /home/ubuntu"
                // sh "java -version"
//                 sh "ls"
//                 sh "sudo chmod +x ./shcmd.sh"
//                 sh "./shcmd.sh"

                sh "java -jar ./target/api-0.0.1-SNAPSHOT.jar &"

            }
        }
        //  stage("test"){
        //     steps{
        //         sh "ps -aux"
        //     }
        // }
    }
}
