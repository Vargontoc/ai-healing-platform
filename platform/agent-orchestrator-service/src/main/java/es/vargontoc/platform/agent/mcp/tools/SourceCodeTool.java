package es.vargontoc.platform.agent.mcp.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SourceCodeTool {

    @Bean
    @Description("List files in a directory to explore project structure")
    public Function<ListFilesRequest, List<String>> listFiles() {
        return request -> {
            try (Stream<Path> paths = Files.walk(Paths.get(request.path), 1)) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                return List.of("Error listing files: " + e.getMessage());
            }
        };
    }

    @Bean
    @Description("Read content of a file")
    public Function<ReadFileRequest, String> readFile() {
        return request -> {
            try {
                return Files.readString(Paths.get(request.path));
            } catch (IOException e) {
                return "Error reading file: " + e.getMessage();
            }
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to list files in a directory")
    public record ListFilesRequest(
            @JsonProperty(required = true, value = "path") @JsonPropertyDescription("The absolute path of the directory") String path) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to read a file")
    public record ReadFileRequest(
            @JsonProperty(required = true, value = "path") @JsonPropertyDescription("The absolute path of the file") String path) {
    }
}
