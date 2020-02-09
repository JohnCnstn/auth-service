val generateOpenapi by tasks.creating(GenerateOpenapiTask::class) {
    inputFile = project.rootDir.path + "/src/main/resources/openapi/openapi.yaml"
    outputDirectory = project.rootDir.path + "/src/main/java"
    apiPackage = "com.johncnstn.auth.generated.api"
    modelPackage = "com.johncnstn.auth.generated.model"
}

tasks {
    "clean" {
        doFirst {
            delete(project.rootDir.path + "/src/main/java/com/johncnstn/auth/generated")
        }
    }
    "compileJava" { dependsOn(generateOpenapi) }
    "compileKotlin" { dependsOn(generateOpenapi) }
}
