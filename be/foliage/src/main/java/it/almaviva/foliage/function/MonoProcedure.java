package it.almaviva.foliage.function;

@FunctionalInterface
public interface MonoProcedure<T1> {
    void eval(T1 par1) throws Exception;
}