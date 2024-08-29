package com.openelements.issues;

import com.openelements.issues.config.IssueServiceProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class IssueCache {

    private final static Duration CACHE_DURATION = Duration.ofMinutes(5);

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(IssueCache.class);

    private final GitHubClient gitHubClient;

    private final Map<String, List<Issue>> cache;

    public IssueCache(@NonNull final IssueServiceProperties properties, final GitHubClient gitHubClient) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        this.cache = new ConcurrentHashMap<>();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), LabelConstants.GOOD_FIRST_ISSUE_LABEL));
            properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL));
            properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), LabelConstants.HACKTOBERFEST_LABEL));
            properties.getRepositories().forEach(repository -> update(repository.org(), repository.repo(), LabelConstants.HELP_WANTED_LABEL));
        }, 0, CACHE_DURATION.getSeconds(), TimeUnit.SECONDS);
    }

    private void update(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        try {
            log.info("Updating cache for repo '{}/{}' with label '{}'", org, repo, label);
            final Repository repository = gitHubClient.getRepository(org, repo);
            final List<Issue> issues = gitHubClient.getIssues(repository, label);
            log.info("Found {} issues for repo '{}/{}' with label '{}'", issues.size(), org, repo, label);
            setIssues(org, repo, label, issues);
        } catch (final Exception e) {
            log.error("Failed to update cache for repo '{}/{}' with label '{}'", org, repo, label, e);
        }
    }

    private void setIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label, @NonNull final List<Issue> issues) {
        Objects.requireNonNull(issues, "issues must not be null");
        cache.put(hash(org, repo, label), issues);
    }

    public Set<Issue> getIssues(@NonNull final String label) {
        Objects.requireNonNull(label, "label must not be null");
        return cache.keySet().stream()
                .flatMap(key -> cache.get(key).stream())
                .filter(issue -> issue.labels().contains(label))
                .collect(Collectors.toUnmodifiableSet());
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
