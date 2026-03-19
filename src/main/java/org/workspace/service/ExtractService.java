package org.workspace.service;

import org.workspace.dto.response.ExtractResponse;

import java.util.concurrent.CompletableFuture;

public interface ExtractService {
    CompletableFuture<ExtractResponse> extract(String message);
}
