package org.mschoe.aocutil.lib;

import org.mschoe.aocutil.lib.exception.AocUtilException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

import static java.io.File.separator;

public class InputProvider {

    private final HttpClient client;

    private final String sessionCookie;

    private static final String URL = "https://adventofcode.com/%s/day/%s/input";

    public InputProvider() {
        this.client = HttpClient.newHttpClient();
        this.sessionCookie = System.getenv("SESSION_COOKIE");

        if (sessionCookie == null) {
            throw new AocUtilException("SessionCookie not set");
        }
    }

    public String get(int day, int year) {
        try {
            var file = getFile(day, year);
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new AocUtilException("Error while reading file");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getFile(int day, int year) throws IOException {
        File file = new File(
                System.getProperty("java.io.tmpdir") + separator +
                        "aoc" + separator + year + separator + day
        );

        if (!file.exists()) {
            String input = getFromSite(day, year);
            file.getParentFile().mkdirs();
            Files.createFile(file.toPath());
            Files.writeString(file.toPath(), input);
        }

        return file;
    }

    private String getFromSite(int day, int year) {
        HttpResponse<String> response = getResponse(day, year);

        if (response.statusCode() == 200) {
            return response.body();
        }

        throw new AocUtilException("Input request returned illegal status code: " + response.statusCode());
    }

    private HttpResponse<String> getResponse(int day, int year) {
        try {
            var urlString = String.format(URL, year, day);
            var request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .header("Cookie", sessionCookie)
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new AocUtilException("Exception occurred while fetching input data");
        }
    }
}
