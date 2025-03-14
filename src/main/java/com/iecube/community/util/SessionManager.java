package com.iecube.community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SessionManager {
    // ID → WebSocketSession 的映射
    private final ConcurrentHashMap<String, WebSocketSession> idToSession = new ConcurrentHashMap<>();
    // WebSocketSession → ID 的映射（使用 WebSocketSession 的唯一标识符作为键）
    private final ConcurrentHashMap<String, String> sessionToId = new ConcurrentHashMap<>();
    // 锁对象确保原子操作
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 添加 WebSocketSession 和 ID 的映射关系
     */
    public void addSession(String id, WebSocketSession session) {
        lock.lock();
        try {
            idToSession.put(id, session);
            sessionToId.put(getSessionKey(session), id);
        } finally {
            lock.unlock();
            log.info("idToSession:{}", idToSession);
            log.info("sessionToId:{}", sessionToId);
        }
    }

    /**
     * 根据 WebSocketSession 移除映射关系
     */
    public void removeSession(WebSocketSession session) {
        lock.lock();
        try {
            String sessionKey = getSessionKey(session);
            String id = sessionToId.get(sessionKey);
            if (id != null) {
                idToSession.remove(id);
                sessionToId.remove(sessionKey);

            }
        } finally {
            lock.unlock();
            log.info("idToSession:{}", idToSession);
            log.info("sessionToId:{}", sessionToId);
        }
    }

    public void removeSession(String id) {
        lock.lock();
        try {
            WebSocketSession session = idToSession.get(id);
            if(session != null) {
                String sessionKey = getSessionKey(session);
                idToSession.remove(id);
                sessionToId.remove(sessionKey);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 根据 ID 获取 WebSocketSession
     */
    public WebSocketSession getSessionById(String id) {
        return idToSession.get(id);
    }

    /**
     * 根据 WebSocketSession 获取 ID
     */
    public String getIdBySession(WebSocketSession session) {
        return sessionToId.get(getSessionKey(session));
    }

    /**
     * 获取 WebSocketSession 的唯一标识符（如 WebSocketSession ID）
     */
    private String getSessionKey(WebSocketSession session) {
        return session.getId(); // 假设 WebSocketSession 有唯一 ID
    }
}