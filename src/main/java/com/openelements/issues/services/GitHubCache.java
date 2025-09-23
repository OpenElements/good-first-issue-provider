package com.openelements.issues.services;

import static com.openelements.issues.data.LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL;
import static com.openelements.issues.data.LabelConstants.GOOD_FIRST_ISSUE_LABEL;
import static com.openelements.issues.data.LabelConstants.HACKTOBERFEST_LABEL;
import static com.openelements.issues.data.LabelConstants.HELP_WANTED_LABEL;

import com.openelements.issues.config.IssueServiceProperties;
import com.openelements.issues.config.RepositoryProperty;
import com.openelements.issues.data.Contributor;
import com.openelements.issues.data.Issue;
import com.openelements.issues.data.Repository;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class GitHubCache {

    private final static Duration CACHE_DURATION = Duration.ofMinutes(5);

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(GitHubCache.class);

    private final GitHubClient gitHubClient;

    private final Map<String, List<Issue>> issuesCache;

    private final Map<String, List<Contributor>> contributorsCache;

    private final Set<Repository> repositoriesCache;

    public GitHubCache(@NonNull final IssueServiceProperties properties, final GitHubClient gitHubClient) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        this.issuesCache = new ConcurrentHashMap<>();
        this.contributorsCache = new ConcurrentHashMap<>();
        this.repositoriesCache = Collections.synchronizedSet(new HashSet<>());
        log.info("Cache will be updated all {} seconds", CACHE_DURATION.getSeconds());
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                log.info("Updating cache");
                final List<RepositoryProperty> repos = properties.getRepositories();
                repos.forEach(repo -> updateRepo(repo.org(), repo.repo()));
                repos.forEach(repo -> updateContributors(repo.org(), repo.repo()));
                repos.forEach(repo -> updateIssues(repo.org(), repo.repo(), repo.excludeIdentifiers(),
                        GOOD_FIRST_ISSUE_LABEL));
                repos.forEach(repo -> updateIssues(repo.org(), repo.repo(), repo.excludeIdentifiers(),
                        GOOD_FIRST_ISSUE_CANDIDATE_LABEL));
                repos.forEach(
                        repo -> updateIssues(repo.org(), repo.repo(), repo.excludeIdentifiers(), HACKTOBERFEST_LABEL));
                repos.forEach(
                        repo -> updateIssues(repo.org(), repo.repo(), repo.excludeIdentifiers(), HELP_WANTED_LABEL));
                log.info("Cache updated. Found {} contributors and {} issues", getContributors().size(),
                        getAllIssues().size());
            } catch (final Exception e) {
                log.error("Failed to update cache", e);
            }
        }, 0, CACHE_DURATION.getSeconds(), TimeUnit.SECONDS);
    }

    private void updateRepo(@NonNull String org, @NonNull String repo) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        final Repository repository = gitHubClient.getRepository(org, repo);
        this.repositoriesCache.add(repository);
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

    private void updateIssues(@NonNull final String org, @NonNull final String repo,
            @Nullable List<String> excludedIdentifiers, @NonNull final String label) {
        try {
            log.info("Updating issues cache for repo '{}/{}' with label '{}'", org, repo, label);
            final Repository repository = gitHubClient.getRepository(org, repo);
            final List<Issue> issues = gitHubClient.getIssues(repository, label, excludedIdentifiers);
            log.info("Found {} issues for repo '{}/{}' with label '{}'", issues.size(), org, repo, label);
            this.issuesCache.put(hash(org, repo, label), issues);
        } catch (final Exception e) {
            log.error("Failed to update issue cache for repo '" + org + "/" + repo + "' and label '" + label + "'", e);
        }
    }

    public Set<Issue> getAllIssues() {
        return issuesCache.keySet().stream()
                .flatMap(key -> issuesCache.get(key).stream())
                .collect(Collectors.toUnmodifiableSet());
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

    public Set<Repository> getRepositories() {
        return Collections.unmodifiableSet(repositoriesCache);
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
