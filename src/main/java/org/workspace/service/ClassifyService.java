package org.workspace.service;

import org.workspace.dto.response.ClassifyResponse;

import java.util.concurrent.CompletableFuture;

public interface ClassifyService {
    CompletableFuture<ClassifyResponse> classify(String message);
}
