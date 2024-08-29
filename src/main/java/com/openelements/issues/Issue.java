package com.openelements.issues;

import java.util.List;

public record Issue(String title, String link, String org, String repo, String imageUrl, int number, boolean isAssigned, boolean isClosed, List<String> labels, List<String> languageTags) {
}
