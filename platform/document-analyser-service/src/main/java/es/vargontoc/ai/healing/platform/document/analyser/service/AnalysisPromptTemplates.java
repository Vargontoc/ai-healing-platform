package es.vargontoc.ai.healing.platform.document.analyser.service;

public class AnalysisPromptTemplates {

        public static final String SUMMARY_PROMPT = """
                        You are a helpful assistant assisting an SRE team.
                        Please provide a concise summary of the following document.
                        Focus on the main events, errors, or key information.

                        Context from knowledge base:
                        {context}

                        Document content:
                        {content}
                        """;

        public static final String ERROR_ANALYSIS_PROMPT = """
                        You are an expert SRE tool. Analyze the following log content for errors.
                        Identify distinct error patterns, their frequency (if possible), and potential root causes.
                        Suggest remediation steps for each error found.

                        Use the following context from similar past incidents or documentation if relevant:
                        {context}

                        Log content:
                        {content}
                        """;
}
