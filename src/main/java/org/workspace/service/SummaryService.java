package org.workspace.service;

import org.workspace.dto.response.SummaryResponse;

import java.util.concurrent.CompletableFuture;

public interface SummaryService {
    CompletableFuture<SummaryResponse> summarize(String message);
}
