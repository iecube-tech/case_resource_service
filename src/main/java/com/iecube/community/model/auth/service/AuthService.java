package com.iecube.community.model.auth.service;

import java.util.List;

public interface AuthService {
    List<String> userAuthList(Integer teacherId);

    Boolean havaAuth(Integer teacherId, String authName);
}
