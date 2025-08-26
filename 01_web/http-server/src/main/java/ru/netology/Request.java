package ru.netology;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Request {
    private String[] requestLine;
    private String method;
    private String path;
    private List<String> headers;
    private String body;
    private final BufferedInputStream in;
    private final int limit;
    final List<String> allowedMethods = List.of("GET", "POST");

    public Request(BufferedInputStream in, int limit) throws IOException {
        this.in = in;
        this.limit = limit;
        parseRequest();
    }

    public String[] getRequestLine() {
        return requestLine;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public List<String> getHeaders() {
        return headers;
    }

    private void parseRequest() throws IOException {
        in.mark(limit);
        var buffer = new byte[limit];
        var read = in.read(buffer);
        var requestLineDelimiter = new byte[]{'\r', '\n'};
        var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            requestLine = null;
            return;
        }
        // читаем request line
        requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            requestLine = null;
            return;
        }
        method = requestLine[0];
        if (!allowedMethods.contains(method)) {
            method = null;
            return;
        }
        path = requestLine[1];
        if (!path.startsWith("/")) {
            path = null;
            return;
        }
        // ищем заголовки
        var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        var headersStart = requestLineEnd + requestLineDelimiter.length;
        var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            headers = null;
            return;
        }
        // отматываем на начало буфера
        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);
        var headersBytes = in.readNBytes(headersEnd - headersStart);
        headers = Arrays.asList(new String(headersBytes).split("\r\n"));

        // для GET тела нет
        if (!method.equals("GET")) {
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                var length = Integer.parseInt(contentLength.get());
                var bodyBytes = in.readNBytes(length);

                body = new String(bodyBytes);
            }
        }
    }

    // from google guava with modifications
    private int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }
}
