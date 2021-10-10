package org.example.ataccama.datasources;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void consume(T object) throws Exception;
}
