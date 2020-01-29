val generateOpenapi by tasks.creating(GenerateOpenapiTask::class) {
    inputFile = project.rootDir.path + "/src/main/resources/openapi/openapi.yaml"
    outputDirectory = project.rootDir.path + "/src/main/java"
    apiPackage = "com.johncnstn.servicetemplate.generated.api"
    modelPackage = "com.johncnstn.servicetemplate.generated.model"
}

tasks {
    "clean" {
        doFirst {
            delete(project.rootDir.path + "/src/main/java/com/johncnstn/servicetemplate/generated")
        }
    }
    "compileJava" { dependsOn(generateOpenapi) }
    "compileKotlin" { dependsOn(generateOpenapi) }
}
