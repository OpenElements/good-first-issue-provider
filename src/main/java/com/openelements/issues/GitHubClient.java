package com.openelements.issues;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Service
public class GitHubClient {

    private final static Logger log = LoggerFactory.getLogger(GitHubClient.class);

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public GitHubClient() {
        Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github.v3+json");
        final String githubToken = System.getenv("GITHUB_TOKEN");
        if(githubToken != null) {
            log.info("Using GITHUB_TOKEN environment variable for GitHub API authentication");
            builder = builder.defaultHeader("Authorization", "token " + githubToken);
        } else {
            log.warn("No GITHUB_TOKEN environment variable found, GitHub API rate limits will apply");
        }
        restClient = builder.build();
        objectMapper = new ObjectMapper();
    }

    public Repository getRepository(@NonNull final String org, @NonNull final String repo) {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        final ResponseEntity<String> repoEntity = restClient.get()
                .uri("/repos/{org}/{repo}", org, repo)
                .retrieve()
                .toEntity(String.class);
        if(!repoEntity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to get repo from GitHub: " + repoEntity.getBody());
        }
        final JsonNode repoJsonNode;
        try {
            repoJsonNode = objectMapper.readTree(repoEntity.getBody());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse languages from GitHub: " + repoEntity.getBody(), e);
        }
        String imageUrl = repoJsonNode.get("owner").get("avatar_url").asText();

        final ResponseEntity<String> languagesEntity = restClient.get()
                .uri("/repos/{org}/{repo}/languages", org, repo)
                .retrieve()
                .toEntity(String.class);
        if(!languagesEntity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to get languages from GitHub: " + languagesEntity.getBody());
        }
        final JsonNode languagesJsonNode;
        try {
            languagesJsonNode = objectMapper.readTree(languagesEntity.getBody());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse languages from GitHub: " + languagesEntity.getBody(), e);
        }
        final List<String> fieldNames = new ArrayList<>();
        languagesJsonNode.fieldNames().forEachRemaining(fieldNames::add);

        final int languageSum = fieldNames
                .stream()
                .mapToInt(fieldName -> languagesJsonNode.get(fieldName).asInt())
                .sum();

        final List<String> languageTags = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            final int count = languagesJsonNode.get(fieldName).asInt();
            final double percentage = (double) count / languageSum * 100;
            if(percentage > 33.0) {
                languageTags.add(fieldName);
            }
        });
        return new Repository(org, repo, imageUrl, languageTags);
    }

    public List<Issue> getIssues(@NonNull final Repository repository, @NonNull final String label) {
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(label, "label must not be null");
        final List<Issue> issues = new ArrayList<>();
        final ResponseEntity<String> entity = restClient.get()
                .uri("/repos/{org}/{repo}/issues?labels={label}", repository.org(), repository.name(), label)
                .retrieve()
                .toEntity(String.class);
        if(!entity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to get issues from GitHub: " + entity.getBody());
        }
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(entity.getBody());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse issues from GitHub: " + entity.getBody(), e);
        }
        if(!jsonNode.isArray()) {
           throw new IllegalStateException("Expected an array of issues from GitHub, but got: " + entity.getBody());
        }
        jsonNode.iterator().forEachRemaining(issueNode -> {
           if(!issueNode.has("html_url")) {
               throw new IllegalStateException("Expected an issue to have an html_url, but got: " + issueNode);
           }
           final JsonNode urlNode = issueNode.get("html_url");
           if(!urlNode.isTextual()) {
               throw new IllegalStateException("Expected an issue's html_url to be a string, but got: " + urlNode);
           }
           final String url = urlNode.asText();

            if(!issueNode.has("title")) {
                throw new IllegalStateException("Expected an issue to have an title, but got: " + issueNode);
            }
            final JsonNode titleNode = issueNode.get("title");
            if(!titleNode.isTextual()) {
                throw new IllegalStateException("Expected an issue's title to be a string, but got: " + urlNode);
            }
            final String title = titleNode.asText();

            if(!issueNode.has("number")) {
                throw new IllegalStateException("Expected an issue to have an number, but got: " + issueNode);
            }
            final JsonNode numberNode = issueNode.get("number");
            if(!numberNode.isInt()) {
                throw new IllegalStateException("Expected an issue's number to be a int, but got: " + urlNode);
            }
            final int number = numberNode.asInt();

            final boolean isAssigned = issueNode.has("assignee") && !issueNode.get("assignee").isNull();
            final boolean isClosed = issueNode.get("state").asText().equals("closed");
            final List<String> labels = new ArrayList<>();
            if(issueNode.has("labels")) {
                issueNode.get("labels").forEach(labelNode -> {
                    if(!labelNode.has("name")) {
                        throw new IllegalStateException("Expected a label to have a name, but got: " + labelNode);
                    }
                    final JsonNode nameNode = labelNode.get("name");
                    if(!nameNode.isTextual()) {
                        throw new IllegalStateException("Expected a label's name to be a string, but got: " + nameNode);
                    }
                    labels.add(nameNode.asText());
                });
            }


            final Issue issue = new Issue(title, url, repository.org(), repository.name(), repository.imageUrl(), Integer.valueOf(number).toString(), isAssigned, isClosed, labels, repository.languages());
            issues.add(issue);
        });
        return Collections.unmodifiableList(issues);
    }
}
