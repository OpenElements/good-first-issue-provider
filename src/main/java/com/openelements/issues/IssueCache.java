package com.openelements.issues;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class IssueCache {

    private Map<String, List<Issue>> cache;

    private Map<String, LocalDateTime> cacheTime;

    public IssueCache() {
        this.cache = new HashMap<>();
        cacheTime = new HashMap<>();
    }

    public void setIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label, @NonNull final List<Issue> issues) {
        Objects.requireNonNull(issues, "issues must not be null");
        cache.put(hash(org, repo, label), issues);
        cacheTime.put(hash(org, repo, label), LocalDateTime.now());
    }

    public List<Issue> getIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        return cache.getOrDefault(hash(org, repo, label), List.of());
    }

    public LocalDateTime getCacheTime(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        return cacheTime.getOrDefault(hash(org, repo, label), LocalDateTime.MIN);
    }

    private String hash(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        Objects.requireNonNull(label, "label must not be null");
        return org + "/" + repo + "/" + label;
    }
}
