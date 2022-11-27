package iskallia.vault.core.world.processor;

@FunctionalInterface
public interface Processor<T> {
   T process(T var1, ProcessorContext var2);
}
