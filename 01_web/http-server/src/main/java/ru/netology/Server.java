package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class Server {

    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) {
        var poolExecutor = Executors.newFixedThreadPool(64);

        try (final var serverSocket = new ServerSocket(port)) {
            log("Server start!");
            while (true) {
                Socket socket = serverSocket.accept();
                poolExecutor.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void handle(Socket socket) {
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            log("client connected: " + socket.getRemoteSocketAddress());
            Request request = new Request(in, 4096);

            final var requestLine = request.getRequestLine();
            final var path = request.getPath();
            final var method = request.getMethod();
            final var headers = request.getHeaders();

            if (requestLine == null || path == null || method == null || headers == null) {
                // just close socket
                badRequest(out);
                return;
            }

            if (!validPaths.contains(path)) {
                badRequest(out);
                return;
            }

            if (handlers.get(method).contains(path)) {
                handlers.get(method).get(path).handle(request, out);
            } else {
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    normalRequest(out, mimeType, content.length);
                    out.write(content);
                    out.flush();
                    log("classic.html loaded");
                    return;
                }

                final var length = Files.size(filePath);
                normalRequest(out, mimeType, length);
                Files.copy(filePath, out);
                log(filePath.getFileName() + " loaded");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    private void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }

    private void normalRequest(BufferedOutputStream out, String mimeType, long length) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void addHandler(String method, String path, Handler handler) {
        if (handlers.contains(method)) {
            handlers.get(method).put(path, handler);
        } else {
            var hashMap = new ConcurrentHashMap<String, Handler>();
            hashMap.put(path, handler);
            handlers.put(method, hashMap);
        }
    }
}
