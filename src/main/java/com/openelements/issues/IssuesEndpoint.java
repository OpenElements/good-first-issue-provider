package com.openelements.issues;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuesEndpoint {

    private final IssueCache issueCache;

    private final static String GOOD_FIRST_ISSUE = "good first issue";

    private final static String GOOD_FIRST_ISSUE_CANDIDATE = "good first issue candidate";

    public IssuesEndpoint(@NonNull final IssueCache issueCache) {
        this.issueCache = Objects.requireNonNull(issueCache, "issueCache must not be null");
    }

    @GetMapping("/good-first-issues")
    public List<Issue> getGoodFirstIssues() {
        return issueCache.getIssues(GOOD_FIRST_ISSUE);
    }

    @GetMapping("/good-first-issue-candidates")
    public List<Issue> getGoodFirstIssuesCandidates() {
        return issueCache.getIssues(GOOD_FIRST_ISSUE_CANDIDATE);
    }
}
