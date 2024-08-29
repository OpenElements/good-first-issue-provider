package com.openelements.issues;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesEndpoint {

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(IssuesEndpoint.class);

    private final IssueCache issueCache;

    public IssuesEndpoint(@NonNull final IssueCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @GetMapping("/api/good-first-issues")
    public List<Issue> getGoodFirstIssues() {
        log.info("Getting good first issues");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_LABEL);
    }

    @GetMapping("/api/good-first-issue-candidates")
    public List<Issue> getGoodFirstIssuesCandidates() {
        log.info("Getting good first issue candidates");
        return issueCache.getIssues(LabelConstants.GOOD_FIRST_ISSUE_CANDIDATE_LABEL);
    }

    @GetMapping("/api/hacktoberfest-issues")
    public List<Issue> getHacktoberfestIssues() {
        log.info("Getting Hacktoberfest issues");
        return issueCache.getIssues(LabelConstants.HACKTOBERFEST_LABEL);
    }

    @GetMapping("/api/help-wanted-issues")
    public List<Issue> getHelpWantedIssues() {
        log.info("Getting help wanted issues");
        return issueCache.getIssues(LabelConstants.HELP_WANTED_LABEL);
    }
}
