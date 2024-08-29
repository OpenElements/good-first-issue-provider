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

    private final static String GOOD_FIRST_ISSUE = "good first issue";

    private final static String GOOD_FIRST_ISSUE_CANDIDATE = "good first issue candidate";

    public IssuesEndpoint(@NonNull final IssueCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @GetMapping("/good-first-issues")
    public List<Issue> getGoodFirstIssues() {
        log.info("Getting good first issues");
        return issueCache.getIssues(GOOD_FIRST_ISSUE);
    }

    @GetMapping("/good-first-issue-candidates")
    public List<Issue> getGoodFirstIssuesCandidates() {
        log.info("Getting good first issue candidates");
        return issueCache.getIssues(GOOD_FIRST_ISSUE_CANDIDATE);
    }
}
