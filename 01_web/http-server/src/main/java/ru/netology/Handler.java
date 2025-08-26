package ru.netology;

import java.io.BufferedOutputStream;

@FunctionalInterface
public interface Handler {
    void handle(Request request, BufferedOutputStream out);
}
