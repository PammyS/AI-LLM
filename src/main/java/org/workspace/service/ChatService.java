package org.workspace.service;

import org.workspace.dto.response.ChatResponse;

import java.util.concurrent.CompletableFuture;

public interface ChatService {
    CompletableFuture<ChatResponse> chat(String message);
}
