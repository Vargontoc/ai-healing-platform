package es.vargontoc.ai.healing.platform.agent.tools;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TestRunnerTool {

    private static final String WORKSPACE_ROOT = "d:/Workspace/ai-healing-platform/platform";

    public String runTests(String moduleName) {
        return executeMavenCommand(moduleName, "test");
    }

    public String runSingleTest(String moduleName, String testName) {
        return executeMavenCommand(moduleName, "-Dtest=" + testName + " test");
    }

    private String executeMavenCommand(String moduleName, String args) {
        File moduleDir = new File(WORKSPACE_ROOT, moduleName);
        if (!moduleDir.exists()) {
            return "Error: Module directory not found: " + moduleDir.getAbsolutePath();
        }

        try {
            // Using 'mvn.cmd' for Windows
            String command = "mvn.cmd " + args;
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.directory(moduleDir);
            builder.redirectErrorStream(true);

            Process process = builder.start();

            // Read output with a timeout
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            if (!finished) {
                process.destroy();
                return output + "\n[TIMEOUT] Test execution timed out.";
            }

            int exitCode = process.exitValue();
            return output + "\n\nExit Code: " + exitCode;

        } catch (Exception e) {
            return "Error executing tests: " + e.getMessage();
        }
    }
}
