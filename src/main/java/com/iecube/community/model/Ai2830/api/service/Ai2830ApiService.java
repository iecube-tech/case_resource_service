package com.iecube.community.model.Ai2830.api.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface Ai2830ApiService {

    String register();

    JsonNode getUserIdMessages(String userId, String courseId, Integer page);
}
