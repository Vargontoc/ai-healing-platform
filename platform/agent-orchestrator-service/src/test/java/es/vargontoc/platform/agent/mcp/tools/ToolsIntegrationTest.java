package es.vargontoc.platform.agent.mcp.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class ToolsIntegrationTest {

    @Autowired
    private SourceCodeTool sourceCodeTool;

    @Autowired
    private KnowledgeBaseTool knowledgeBaseTool;

    @Test
    void shouldListFilesInDirectory() throws IOException {
        // Create a temp directory and file
        Path tempDir = Files.createTempDirectory("agent-test");
        Files.createFile(tempDir.resolve("testfile.txt"));

        Function<SourceCodeTool.ListFilesRequest, List<String>> listFunction = sourceCodeTool.listFiles();
        List<String> files = listFunction
                .apply(new SourceCodeTool.ListFilesRequest(tempDir.toAbsolutePath().toString()));

        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertTrue(files.stream().anyMatch(f -> f.endsWith("testfile.txt")));

        // Cleanup
        Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a)) // Delete files first
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void shouldReadFileContent() throws IOException {
        Path tempFile = Files.createTempFile("agent-read-test", ".txt");
        Files.writeString(tempFile, "Hello Agent");

        Function<SourceCodeTool.ReadFileRequest, String> readFunction = sourceCodeTool.readFile();
        String content = readFunction.apply(new SourceCodeTool.ReadFileRequest(tempFile.toAbsolutePath().toString()));

        assertEquals("Hello Agent", content);

        Files.delete(tempFile);
    }

    @Test
    void shouldQueryKnowledgeBaseStub() {
        Function<KnowledgeBaseTool.QueryRequest, String> queryFunction = knowledgeBaseTool.queryKnowledgeBase();
        String response = queryFunction.apply(new KnowledgeBaseTool.QueryRequest("How to fix NPE?"));

        assertTrue(response.contains("No matching documents found"));
    }
}
