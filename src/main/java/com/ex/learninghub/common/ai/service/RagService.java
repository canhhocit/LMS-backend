package com.ex.learninghub.common.ai.service;

import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.security.UserPrincipal;

public interface RagService {
    void ingestDocument(RagIngestRequest request, UserPrincipal principal);
    RagQueryResponse queryCourseMaterials(RagQueryRequest request, UserPrincipal principal);
}
