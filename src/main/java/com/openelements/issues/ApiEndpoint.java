package com.openelements.issues;

import com.openelements.issues.data.Contributor;
import com.openelements.issues.data.Issue;
import com.openelements.issues.services.GitHubCache;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiEndpoint {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(ApiEndpoint.class);

    private final GitHubCache issueCache;

    public ApiEndpoint(@NonNull final GitHubCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }


    @GetMapping("/api/v2/contributors")
    public Set<Contributor> getContributors() {
        log.info("Getting contributors");
        return issueCache.getContributors();
    }

    @GetMapping("/api/v2/contributors/count")
    public long getContributorCount() {
        log.info("Getting contributors count");
        return issueCache.getContributors().size();
    }

    @GetMapping("/api/v2/issues")
    public Set<Issue> getIssues(
            @RequestParam(name = "isAssigned", required = false) Boolean isAssigned,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "filteredLabels", required = false) Set<String> filteredLabels,
            @RequestParam(name = "excludedLabels", required = false) Set<String> excludedLabels,
            @RequestParam(name = "filteredLanguages", required = false) Set<String> filteredLanguages) {
        log.info(
                "Getting issues with filters - isAssigned: {}, isClosed: {}, filteredLabels: {}, excludedLabels: {}, filteredLanguages: {}",
                isAssigned, isClosed, filteredLabels, excludedLabels, filteredLanguages);
        return issueCache.getAllIssues().stream()
                .filter(issue -> isAssigned == null || issue.isAssigned() == isAssigned)
                .filter(issue -> isClosed == null || issue.isClosed() == isClosed)
                .filter(issue -> filteredLabels == null || filteredLabels.isEmpty() || issue.labels()
                        .containsAll(filteredLabels))
                .filter(issue -> excludedLabels == null || excludedLabels.isEmpty() || issue.labels().stream()
                        .noneMatch(excludedLabels::contains))
                .filter(issue -> filteredLanguages == null || filteredLanguages.isEmpty() || issue.repository()
                        .languages().containsAll(filteredLanguages))
                .collect(Collectors.toUnmodifiableSet());
    }

    @GetMapping("/api/v2/issues/count")
    public long getIssuesCount(
            @RequestParam(name = "isAssigned", required = false) Boolean isAssigned,
            @RequestParam(name = "isClosed", required = false) Boolean isClosed,
            @RequestParam(name = "filteredLabels", required = false) Set<String> filteredLabels,
            @RequestParam(name = "excludedLabels", required = false) Set<String> excludedLabels,
            @RequestParam(name = "filteredLanguages", required = false) Set<String> filteredLanguages) {
        return getIssues(isAssigned, isClosed, filteredLabels, excludedLabels, filteredLanguages).size();
    }

    @GetMapping("/api/v2/repositories/stars")
    public long getStarsCount() {
        return issueCache.getRepositories().stream().mapToLong(repo -> repo.stars()).sum();
    }

    @GetMapping("/api/v2/repositories/count")
    public long getRepositoryCount() {
        return issueCache.getRepositories().size();
    }
}
