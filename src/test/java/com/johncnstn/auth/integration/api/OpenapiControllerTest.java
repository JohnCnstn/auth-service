package com.johncnstn.auth.integration.api;

import com.johncnstn.auth.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static com.johncnstn.auth.api.OpenapiController.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OpenapiControllerTest extends AbstractIntegrationTest {

    @Test
    public void openapiUiRedirect_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActions = mockMvc.perform(
                get(OPENAPI_UI_REDIRECT_PATH));
        //THEN
        resultActions.andExpect(status().is3xxRedirection());
    }

    @Test
    public void fetchUiConfiguration_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActions = mockMvc.perform(
                get(GET_UI_CONFIGURATION_PATH));
        var body = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        //THEN
        resultActions.andExpect(status().isOk());
        assertNotNull(body);
    }

    @Test
    public void fetchSecurityConfiguration_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActions = mockMvc.perform(
                get(GET_SECURITY_CONFIGURATION_PATH));
        var body = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        //THEN
        resultActions.andExpect(status().isOk());
        assertNotNull(body);
    }

    @Test
    public void fetchConfiguration_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActions = mockMvc.perform(
                get(GET_CONFIGURATION_PATH));
        var body = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        //THEN
        resultActions.andExpect(status().isOk());
        assertNotNull(body);
    }

    @Test
    public void fetchOpenapiYaml_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActionsYml = mockMvc.perform(
                get(GET_OPENAPI_YML_PATH));
        var resultActionsYaml = mockMvc.perform(
                get(GET_OPENAPI_YAML_PATH));

        var bodyYml = resultActionsYml.andReturn()
                .getResponse()
                .getContentAsString();
        var bodyYaml = resultActionsYaml.andReturn()
                .getResponse()
                .getContentAsString();
        //THEN
        resultActionsYml.andExpect(status().isOk());
        resultActionsYaml.andExpect(status().isOk());
        assertNotNull(bodyYml);
        assertNotNull(bodyYaml);
    }

    @Test
    public void fetchOpenapiJson_happyPath() throws Exception {
        //GIVEN
        //WHEN
        var resultActions = mockMvc.perform(
                get(GET_OPENAPI_JSON_PATH));
        var body = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        //THEN
        resultActions.andExpect(status().isOk());
        assertNotNull(body);
    }

}
