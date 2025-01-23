package it.almaviva.foliage.function;

@FunctionalInterface
public interface TriFunction<R, T1, T2, T3> {
    R get(T1 par1, T2 par2, T3 par3) throws Exception;
}