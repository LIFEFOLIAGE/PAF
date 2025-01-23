package it.almaviva.foliage.function;

@FunctionalInterface
public interface ResultFunction<T> {
    T get() throws Exception;
}