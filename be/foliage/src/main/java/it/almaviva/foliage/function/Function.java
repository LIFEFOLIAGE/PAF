package it.almaviva.foliage.function;

@FunctionalInterface
public interface Function<T, P> {
    T get(P par) throws Exception;
}