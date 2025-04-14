package com.jeein.member.docs;

// import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;

public class RestDocsUtil {

    public static RestDocumentationResultHandler doc(String identifier, Snippet... snippets) {
        return document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                snippets);
    }
}
