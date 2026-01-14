package es.vargontoc.platform.agent.skills;

import es.vargontoc.platform.agent.mcp.tools.SourceCodeTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class SourceCodeToolTest {

    @Autowired
    private SourceCodeTool sourceCodeTool;

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.vectorstore.VectorStore vectorStore;

    @Test
    void shouldListFiles(@TempDir Path tempDir) throws IOException {
        // Create dummy files
        Files.createFile(tempDir.resolve("test1.txt"));
        Files.createFile(tempDir.resolve("test2.java"));
        Files.createDirectory(tempDir.resolve("subdir"));

        Function<SourceCodeTool.ListFilesRequest, List<String>> listFilesFunction = sourceCodeTool.listFiles();
        List<String> files = listFilesFunction
                .apply(new SourceCodeTool.ListFilesRequest(tempDir.toAbsolutePath().toString()));

        assertNotNull(files);
        // Only regular files are returned by the tool currently
        assertEquals(2, files.size());
        assertTrue(files.stream().anyMatch(f -> f.endsWith("test1.txt")));
        assertTrue(files.stream().anyMatch(f -> f.endsWith("test2.java")));
    }

    @Test
    void shouldReadFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("content.txt");
        Files.writeString(file, "Hello World");

        Function<SourceCodeTool.ReadFileRequest, String> readFileFunction = sourceCodeTool.readFile();
        String content = readFileFunction.apply(new SourceCodeTool.ReadFileRequest(file.toAbsolutePath().toString()));

        assertEquals("Hello World", content);
    }
}
