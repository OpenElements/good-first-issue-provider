package com.openelements.issues;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class IssueCache {

    private final static String GOOD_FIRST_ISSUE = "good first issue";

    private final static String GOOD_FIRST_ISSUE_CANDIDATE = "good first issue candidate";

    private final static Duration CACHE_DURATION = Duration.ofSeconds(60);

    private final GitHubClient gitHubClient;

    private final Map<String, List<Issue>> cache;

    public IssueCache(@NonNull final IssueServiceProperties properties, final GitHubClient gitHubClient) {
        this.cache = new ConcurrentHashMap<>();

        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), GOOD_FIRST_ISSUE));
                properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), GOOD_FIRST_ISSUE_CANDIDATE));
        }, 0, CACHE_DURATION.getSeconds(), TimeUnit.SECONDS);
    }

    private void update(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        final List<Issue> issues = gitHubClient.getIssues(org, repo, label);
        setIssues(org, repo, label, issues);
    }

    private void setIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label, @NonNull final List<Issue> issues) {
        Objects.requireNonNull(issues, "issues must not be null");
        cache.put(hash(org, repo, label), issues);
    }

    public List<Issue> getIssues(@NonNull final String label) {
        return cache.keySet().stream()
                .flatMap(key -> cache.get(key).stream())
                .filter(issue -> issue.labels().contains(label))
                .toList();
    }

    public List<Issue> getIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        return cache.getOrDefault(hash(org, repo, label), List.of());
    }

    private String hash(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        Objects.requireNonNull(label, "label must not be null");
        return org + "/" + repo + "/" + label;
    }
}
