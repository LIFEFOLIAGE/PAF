package it.almaviva.foliage.function;

@FunctionalInterface
public interface TriProcedure<T1, T2, T3> {
    void eval(T1 par1, T2 par2, T3 par3) throws Exception;
}