package com.mipt.angelikaliber.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class FavoritesService {

    public static final String SESSION_KEY = "favoriteTaskIds";

    @SuppressWarnings("unchecked")
    public Set<Long> getFavorites(HttpSession session) {
        Set<Long> ids = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (ids == null) {
            ids = new LinkedHashSet<>();
            session.setAttribute(SESSION_KEY, ids);
        }
        return ids;
    }

    public void add(HttpSession session, Long taskId) {
        getFavorites(session).add(taskId);
    }

    public boolean remove(HttpSession session, Long taskId) {
        return getFavorites(session).remove(taskId);
    }
}
