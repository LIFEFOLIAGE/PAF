package it.almaviva.foliage.function;

import java.util.HashMap;

@FunctionalInterface
public interface ContextTest {
    boolean eval(HashMap<String, Object> contesto) throws Exception;
}