package iskallia.vault.world.vault.modifier.spi;

@FunctionalInterface
public interface IVaultModifierTextFormatter<P> {
   String format(String var1, P var2, int var3);
}
