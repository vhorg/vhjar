package iskallia.vault.util.function;

@FunctionalInterface
public interface QuadFunction<S, T, U, V, R> {
   R apply(S var1, T var2, U var3, V var4);
}
