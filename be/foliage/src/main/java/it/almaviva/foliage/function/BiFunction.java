package it.almaviva.foliage.function;

@FunctionalInterface
public interface BiFunction<R, T1, T2> {
    R get(T1 par1, T2 par2) throws Exception;
}

