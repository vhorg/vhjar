package iskallia.vault.etching;

import iskallia.vault.init.ModConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class EtchingSet<T> extends ForgeRegistryEntry<EtchingSet<?>> {
   public EtchingSet(ResourceLocation name) {
      this.setRegistryName(name);
   }

   public abstract Class<T> getConfigClass();

   public abstract T getDefaultConfig();

   public T getConfig() {
      return (T)ModConfigs.ETCHING.getEtchingConfig(this).getConfig();
   }

   public void apply(ServerPlayer player) {
   }

   public void remove(ServerPlayer player) {
   }

   public void tick(ServerPlayer player) {
   }

   public static class Simple extends EtchingSet<EtchingSet.Simple.NoOpConfig> {
      public Simple(ResourceLocation name) {
         super(name);
      }

      @Override
      public Class<EtchingSet.Simple.NoOpConfig> getConfigClass() {
         return EtchingSet.Simple.NoOpConfig.class;
      }

      public EtchingSet.Simple.NoOpConfig getDefaultConfig() {
         return new EtchingSet.Simple.NoOpConfig();
      }

      public static class NoOpConfig {
      }
   }
}
