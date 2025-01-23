package it.almaviva.foliage.function;


@FunctionalInterface
public interface BiProcedure<T1, T2> {
    void eval(T1 par1, T2 par2) throws Exception;
}
