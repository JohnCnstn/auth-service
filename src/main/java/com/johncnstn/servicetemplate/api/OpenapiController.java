package com.johncnstn.servicetemplate.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

import static com.johncnstn.servicetemplate.util.VersionUtils.getAppVersion;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenapiController {

    public static final String OPENAPI_UI_REDIRECT_PATH = "/";

    public static final String GET_UI_CONFIGURATION_PATH = "/swagger-resources/configuration/ui";
    public static final String GET_SECURITY_CONFIGURATION_PATH = "/swagger-resources/configuration/security";
    public static final String GET_CONFIGURATION_PATH = "/swagger-resources";

    public static final String GET_OPENAPI_YAML_PATH = "/openapi.yaml";
    public static final String GET_OPENAPI_YML_PATH = "/openapi.yml";
    public static final String GET_OPENAPI_JSON_PATH = "/openapi.json";


    private final String uiConfigurationJson;
    private final String securityConfigurationJson;
    private final String configurationJson;
    private final String openapiYaml;
    private final String openapiJson;

    @SneakyThrows
    @SuppressWarnings("checkstyle:LineLength")
    public OpenapiController(ObjectMapper objectMapper,
                             ResourceLoader resourceLoader,
                             Yaml yaml) {
        Map<String, Object> openapiSpec = yaml.load(
                resourceLoader.getResource("classpath:/openapi/openapi.yaml").getInputStream());

        var configurationUiJson = loadJson(
                objectMapper, resourceLoader, "classpath:/openapi/configuration-ui.json");
        var configurationSecurityJson = loadJson(
                objectMapper, resourceLoader, "classpath:/openapi/configuration-security.json");
        var configurationJson = loadJson(
                objectMapper, resourceLoader, "classpath:/openapi/configuration.json");

        var openapiYaml = populateVersion(openapiSpec);
        this.uiConfigurationJson = objectMapper.writeValueAsString(configurationUiJson);
        this.securityConfigurationJson = objectMapper.writeValueAsString(configurationSecurityJson);
        this.configurationJson = objectMapper.writeValueAsString(configurationJson);
        this.openapiYaml = yaml.dump(openapiYaml);
        this.openapiJson = objectMapper.writeValueAsString(openapiYaml);
    }

    @GetMapping(OPENAPI_UI_REDIRECT_PATH)
    public String openapiUiRedirect() {
        return "redirect:swagger-ui.html";
    }

    @ResponseBody
    @GetMapping(GET_UI_CONFIGURATION_PATH)
    public String getUiConfiguration() {
        return uiConfigurationJson;
    }

    @ResponseBody
    @GetMapping(GET_SECURITY_CONFIGURATION_PATH)
    public String getSecurityConfiguration() {
        return securityConfigurationJson;
    }

    @ResponseBody
    @GetMapping(GET_CONFIGURATION_PATH)
    public String getConfiguration() {
        return configurationJson;
    }

    @ResponseBody
    @GetMapping(value = {GET_OPENAPI_YAML_PATH, GET_OPENAPI_YML_PATH}, produces = "text/yaml")
    public String getOpenapiYaml() {
        return openapiYaml;
    }

    @ResponseBody
    @GetMapping(GET_OPENAPI_JSON_PATH)
    public String getOpenapiJson() {
        return openapiJson;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> populateVersion(Map<String, Object> openapiYaml) {
        if (openapiYaml.containsKey("info")) {
            var info = (Map<String, Object>) openapiYaml.get("info");
            info.put("version", getAppVersion());
        }
        return openapiYaml;
    }

    private JsonNode loadJson(final ObjectMapper objectMapper,
                              final ResourceLoader resourceLoader,
                              final String location) throws IOException {
        return objectMapper.readTree(resourceLoader.getResource(location).getInputStream());
    }

}
