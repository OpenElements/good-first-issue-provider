package com.openelements.issues;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesEndpoint {

    private final IssueServiceProperties properties;

    private final GitHubClient gitHubClient;

    private final IssueCache issueCache;

    public IssuesEndpoint(@NonNull final IssueServiceProperties properties, @NonNull final GitHubClient gitHubClient, @NonNull final IssueCache issueCache) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @GetMapping("/good-first-issues")
    public List<Issue> getGoodFirstIssues() {
        return properties.getRepositories().stream()
                .flatMap(r -> getIssues(r.org(), r.repo(), "good first issue").stream())
                .toList();
    }

    @GetMapping("/good-first-issue-candidates")
    public List<Issue> getGoodFirstIssuesCandidates() {
        return properties.getRepositories().stream()
                .flatMap(r -> getIssues(r.org(), r.repo(), "good first issue candidate").stream())
                .toList();
    }

    private List<Issue> getIssues(@NonNull final String org, @NonNull final String repo, @NonNull final String label) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        Objects.requireNonNull(label, "label must not be null");
        final LocalDateTime cacheTime = issueCache.getCacheTime(org, repo, label);
        if(cacheTime.isBefore(LocalDateTime.now().minusSeconds(70))) {
            final List<Issue> issues = gitHubClient.getIssues(org, repo, label);
            issueCache.setIssues(org, repo, label, issues);
        }
        return issueCache.getIssues(org, repo, label);
    }
}
