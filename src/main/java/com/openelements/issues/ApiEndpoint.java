package com.openelements.issues;

import com.openelements.issues.data.Contributor;
import com.openelements.issues.data.Issue;
import com.openelements.issues.services.GitHubCache;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiEndpoint {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(ApiEndpoint.class);

    private final GitHubCache issueCache;

    public ApiEndpoint(@NonNull final GitHubCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @GetMapping("/api/good-first-issues")
    public Set<Issue> getGoodFirstIssues() {
        log.info("Getting good first issues");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_LABEL);
    }

    @GetMapping("/api/good-first-issue-candidates")
    public Set<Issue> getGoodFirstIssuesCandidates() {
        log.info("Getting good first issue candidates");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL);
    }

    @GetMapping("/api/hacktoberfest-issues")
    public Set<Issue> getHacktoberfestIssues() {
        log.info("Getting Hacktoberfest issues");
        return issueCache.getIssues(LabelConstants.HACKTOBERFEST_LABEL);
    }

    @GetMapping("/api/help-wanted-issues")
    public Set<Issue> getHelpWantedIssues() {
        log.info("Getting help wanted issues");
        return issueCache.getIssues(LabelConstants.HELP_WANTED_LABEL);
    }

    @GetMapping("/api/contributors")
    public Set<Contributor> getContributors() {
        log.info("Getting contributors");
        return issueCache.getContributors();
    }

}
