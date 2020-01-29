import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.openapitools.codegen.DefaultGenerator
import org.openapitools.codegen.config.CodegenConfigurator
import org.openapitools.codegen.languages.SpringCodegen

open class GenerateOpenapiTask : DefaultTask() {

    @InputFile
    lateinit var inputFile: String

    @OutputDirectory
    lateinit var outputDirectory: String

    @Input
    lateinit var apiPackage: String

    @Input
    lateinit var modelPackage: String

    @TaskAction
    fun taskAction() {
        val config = CodegenConfigurator()

        config.lang = CustomSpringCodegen::class.java.name
        config.inputSpec = inputFile
        config.outputDir = outputDirectory

        config.addAdditionalProperty("apiPackage", apiPackage)
        config.addAdditionalProperty("modelPackage", modelPackage)
        config.addAdditionalProperty("sourceFolder", "")

        config.addAdditionalProperty("hideGenerationTimestamp", true)
        config.addAdditionalProperty("interfaceOnly", true)
        config.addAdditionalProperty("java8", false)
        config.addAdditionalProperty("useTags", true)

        config.addSystemProperty("apis", "")
        config.addSystemProperty("models", "")

        DefaultGenerator().opts(config.toClientOptInput()).generate()
    }

}

class CustomSpringCodegen : SpringCodegen() {

    override fun findCommonPrefixOfVars(vars: List<Any>): String {
        return ""
    }

    override fun processOpts() {
        super.processOpts()
        additionalProperties["jsr310"] = "true"
        typeMapping["date"] = "LocalDate"
        typeMapping["DateTime"] = "OffsetDateTime"
        importMapping["LocalDate"] = "java.time.LocalDate"
        importMapping["OffsetDateTime"] = "java.time.OffsetDateTime"
    }

}
