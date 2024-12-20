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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
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

    @GetMapping("/api/v2/issues")
    public Set<Issue> getIssues(@PathVariable(name = "isAssigned", required = false) Boolean isAssigned,
                                @PathVariable(name = "isClosed",required = false) Boolean isClosed,
                                @PathVariable(name = "filteredLabels",required = false) Set<String> filteredLabels,
                                @PathVariable(name = "excludedLabels",required = false) Set<String> excludedLabels,
                                @PathVariable(name = "filteredLanguages",required = false) Set<String> filteredLanguages) {
        log.info("Getting good first issues");
        return issueCache.getAllIssues().stream()
                .filter(issue -> isAssigned == null || issue.isAssigned() == isAssigned)
                .filter(issue -> isClosed == null || issue.isClosed() == isClosed)
                .filter(issue -> filteredLabels == null || issue.labels().containsAll(filteredLabels))
                .filter(issue -> excludedLabels == null || issue.labels().stream().noneMatch(excludedLabels::contains))
                .filter(issue -> filteredLanguages == null || issue.repository().languages().containsAll(filteredLanguages))
                .collect(Collectors.toUnmodifiableSet());
    }
}
