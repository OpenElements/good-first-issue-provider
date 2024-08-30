package com.openelements.issues.services;

import com.openelements.issues.LabelConstants;
import com.openelements.issues.config.IssueServiceProperties;
import com.openelements.issues.data.Contributor;
import com.openelements.issues.data.Issue;
import com.openelements.issues.data.Repository;
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
public class GitHubCache {

    private final static Duration CACHE_DURATION = Duration.ofMinutes(5);

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(GitHubCache.class);

    private final GitHubClient gitHubClient;

    private final Map<String, List<Issue>> issuesCache;

    private final Map<String, List<Contributor>> contributorsCache;

    public GitHubCache(@NonNull final IssueServiceProperties properties, final GitHubClient gitHubClient) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        this.issuesCache = new ConcurrentHashMap<>();
        this.contributorsCache = new ConcurrentHashMap<>();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            properties.getRepositories().forEach(repository -> updateContributors(repository.org(), repository.repo()));

            properties.getRepositories().forEach(repository -> updateIssues(repository.org(), repository.repo(), LabelConstants.GOOD_FIRST_ISSUE_LABEL));
            properties.getRepositories().forEach(repository -> updateIssues(repository.org(), repository.repo(), LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL));
            properties.getRepositories().forEach(repository -> updateIssues(repository.org(), repository.repo(), LabelConstants.HACKTOBERFEST_LABEL));
            properties.getRepositories().forEach(repository -> updateIssues(repository.org(), repository.repo(), LabelConstants.HELP_WANTED_LABEL));
        }, 0, CACHE_DURATION.getSeconds(), TimeUnit.SECONDS);
    }

    private void updateContributors(@NonNull final String org, @NonNull final String repo) {
        try {
            log.info("Updating contributors cache for repo '{}/{}'", org, repo);
            final Repository repository = gitHubClient.getRepository(org, repo);
            final List<Contributor> contributors = gitHubClient.getContributors(repository);
            log.info("Found {} contributors for repo '{}/", contributors.size(), repository.org(), repository.name());
            this.contributorsCache.put(hash(org, repo), contributors);
        } catch (final Exception e) {
            log.error("Failed to update contributor cache for repo '" + org + "/" + repo + "'", e);
        }
    }

    private void updateIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        try {
            log.info("Updating issues cache for repo '{}/{}' with label '{}'", org, repo, label);
            final Repository repository = gitHubClient.getRepository(org, repo);
            final List<Issue> issues = gitHubClient.getIssues(repository, label);
            log.info("Found {} issues for repo '{}/{}' with label '{}'", issues.size(), org, repo, label);
            this.issuesCache.put(hash(org, repo, label), issues);
        } catch (final Exception e) {
            log.error("Failed to update issue cache for repo '" + org + "/" + repo + "' and label '" + label + "'", e);
        }
    }

    public Set<Issue> getIssues(@NonNull final String label) {
        Objects.requireNonNull(label, "label must not be null");
        return issuesCache.keySet().stream()
                .flatMap(key -> issuesCache.get(key).stream())
                .filter(issue -> issue.labels().stream().anyMatch(l -> l.equalsIgnoreCase(label)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Contributor> getContributors(@NonNull final String org, @NonNull final String repo) {
        return contributorsCache.getOrDefault(hash(org, repo), List.of());
    }

    public Set<Contributor> getContributors() {
        return contributorsCache.keySet().stream()
                .flatMap(key -> contributorsCache.get(key).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Issue> getIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        return issuesCache.getOrDefault(hash(org, repo, label), List.of());
    }

    private String hash(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        Objects.requireNonNull(label, "label must not be null");
        return hash(org, repo) + "/" + label;
    }

    private String hash(@NonNull final String org, @NonNull final String repo) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        return org + "/" + repo;
    }
}
