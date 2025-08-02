package com.openelements.issues;

import com.openelements.issues.data.Contributor;
import com.openelements.issues.data.Issue;
import com.openelements.issues.services.GitHubCache;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ApiEndpoint {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(ApiEndpoint.class);

    private final GitHubCache issueCache;

    public ApiEndpoint(@NonNull final GitHubCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @Deprecated(forRemoval = true)
    private record OldIssueResponse(@NonNull String title, @NonNull String link, @NonNull String org, @NonNull String repo, @NonNull String imageUrl, @NonNull String identifier, boolean isAssigned, boolean isClosed, @NonNull List<String> labels, @NonNull List<String> languageTags) {
    }

    /**
     * @deprecated Use {@link #getIssues(Boolean, Boolean, Set, Set, Set)} instead
     * @return Set of good first issues
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/hacktoberfest-issues")
    public Set<OldIssueResponse> getHacktoberfestIssuesOld() {
        log.warn("DEPRECATED API CALLED: Getting Hacktoberfest issues");
        return issueCache.getIssues(LabelConstants.HACKTOBERFEST_LABEL).stream()
                .map(issue -> new OldIssueResponse(issue.title(), issue.link(), issue.repository().org(), issue.repository().name(), issue.repository().imageUrl(), issue.identifier(), issue.isAssigned(), issue.isClosed(), issue.labels(), issue.repository().languages()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @deprecated Use {@link #getContributors()} instead
     * @return
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/contributors")
    public Set<Contributor> getContributorsOld() {
        log.warn("DEPRECATED API CALLED: Getting Contributors");
        return getContributors();
    }

    /**
     * @deprecated Use {@link #getIssues(Boolean, Boolean, Set, Set, Set)} instead
     * @return Set of good first issues
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/good-first-issues")
    public Set<OldIssueResponse> getGoodFirstIssuesOld() {
        log.info("Getting good first issues");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_LABEL).stream()
                .map(issue -> new OldIssueResponse(issue.title(), issue.link(), issue.repository().org(), issue.repository().name(), issue.repository().imageUrl(), issue.identifier(), issue.isAssigned(), issue.isClosed(), issue.labels(), issue.repository().languages()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @deprecated Use {@link #getIssues(Boolean, Boolean, Set, Set, Set)} instead
     * @return Set of good first issues
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/good-first-issue-candidates")
    public Set<OldIssueResponse> getGoodFirstIssuesCandidatesOld() {
        log.info("Getting good first issue candidates");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL).stream()
                .map(issue -> new OldIssueResponse(issue.title(), issue.link(), issue.repository().org(), issue.repository().name(), issue.repository().imageUrl(), issue.identifier(), issue.isAssigned(), issue.isClosed(), issue.labels(), issue.repository().languages()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @deprecated Use {@link #getIssues(Boolean, Boolean, Set, Set, Set)} instead
     * @return Set of good first issues
     */
    @Deprecated(forRemoval = true)
    @GetMapping("/api/help-wanted-issues")
    public Set<OldIssueResponse> getHelpWantedIssuesOld() {
        log.info("Getting help wanted issues");
        return issueCache.getIssues(LabelConstants.HELP_WANTED_LABEL).stream()
                .map(issue -> new OldIssueResponse(issue.title(), issue.link(), issue.repository().org(), issue.repository().name(), issue.repository().imageUrl(), issue.identifier(), issue.isAssigned(), issue.isClosed(), issue.labels(), issue.repository().languages()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @GetMapping("/api/v2/contributors")
    public Set<Contributor> getContributors() {
        log.info("Getting contributors");
        return issueCache.getContributors();
    }

    @GetMapping("/api/v2/issues")
    public Set<Issue> getIssues(
            @RequestParam(name = "isAssigned", required = false) Boolean isAssigned, 
            @RequestParam(name = "isClosed", required = false) Boolean isClosed, 
            @RequestParam(name = "filteredLabels", required = false) Set<String> filteredLabels, 
            @RequestParam(name = "excludedLabels", required = false) Set<String> excludedLabels, 
            @RequestParam(name = "filteredLanguages", required = false) Set<String> filteredLanguages) {
        log.info("Getting issues with filters - isAssigned: {}, isClosed: {}, filteredLabels: {}, excludedLabels: {}, filteredLanguages: {}", 
                 isAssigned, isClosed, filteredLabels, excludedLabels, filteredLanguages);
        return issueCache.getAllIssues().stream()
                .filter(issue -> isAssigned == null || issue.isAssigned() == isAssigned)
                .filter(issue -> isClosed == null || issue.isClosed() == isClosed)
                .filter(issue -> filteredLabels == null || filteredLabels.isEmpty() || issue.labels().containsAll(filteredLabels))
                .filter(issue -> excludedLabels == null || excludedLabels.isEmpty() || issue.labels().stream().noneMatch(excludedLabels::contains))
                .filter(issue -> filteredLanguages == null || filteredLanguages.isEmpty() || issue.repository().languages().containsAll(filteredLanguages))
                .collect(Collectors.toUnmodifiableSet());
    }
}
