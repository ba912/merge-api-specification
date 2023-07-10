package com.ba912.mergeapispecification;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class MergeApiSpecificationApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(MergeApiSpecificationApplication.class, args);

        SwaggerParseResult sample1 = new OpenAPIV3Parser().readLocation("./src/main/resources/swagger/api-1.yaml", null, null);
        SwaggerParseResult sample2 = new OpenAPIV3Parser().readLocation("./src/main/resources/swagger/api-2.yaml", null, null);

        Paths mergedPaths = new Paths();
        sample1.getOpenAPI().getPaths().forEach(mergedPaths::addPathItem);
        sample2.getOpenAPI().getPaths().forEach(mergedPaths::addPathItem);

        Components mergedComponents = new Components();
        sample1.getOpenAPI().getComponents().getSchemas().forEach(mergedComponents::addSchemas);
        sample2.getOpenAPI().getComponents().getSchemas().forEach(mergedComponents::addSchemas);

        OpenAPI mergedApi = new OpenAPI()
                .info(new Info()
                        .title("Merged API")
                        .description("Merged Api Specification")
                        .version("1.0.0"))
                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8080/merged-api")
                        ))
                .paths(mergedPaths)
                .components(mergedComponents);

        File file = new File("./src/main/resources/swagger/merged-api.yaml");
        String yamlOutput = Yaml.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(mergedApi);
        FileUtils.write(file, yamlOutput);
    }

}
