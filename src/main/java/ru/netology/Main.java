package ru.netology;

public class Main {
  final static int PORT = 9999;

  public static void main(String[] args) {

    final var server = new Server();
    server.listen(PORT);

  }
}


