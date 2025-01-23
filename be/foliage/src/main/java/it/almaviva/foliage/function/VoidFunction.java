package it.almaviva.foliage.function;

@FunctionalInterface
public interface VoidFunction<T> {
    T get() throws Exception;
}
