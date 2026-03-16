package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();
//    server.addHandler("GET", "/", new Handler() {
//      @Override
//      public void handle(Request request, BufferedOutputStream responseStream) {
//        final var filePath = Path.of(".", "public", "index.html");
//        final String mimeType;
//        try {
//          mimeType = Files.probeContentType(filePath);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        // special case for classic
//
//        final long length;
//        try {
//          length = Files.size(filePath);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        try {
//          Files.copy(filePath, responseStream);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//
//      }
//    });
    server.listen(9999);
  }
}


