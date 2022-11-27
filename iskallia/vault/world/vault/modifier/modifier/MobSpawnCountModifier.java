package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class MobSpawnCountModifier extends VaultModifier<MobSpawnCountModifier.Properties> {
   public MobSpawnCountModifier(ResourceLocation id, MobSpawnCountModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.maxMobsAdded * s));
   }

   @Override
   public void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      listener.ifPresent(Runner.SPAWNER, spawner -> spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, maxMobs -> maxMobs + this.properties().maxMobsAdded));
   }

   @Override
   public void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      listener.ifPresent(Runner.SPAWNER, spawner -> spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, maxMobs -> maxMobs - this.properties().maxMobsAdded));
   }

   public static class Properties {
      @Expose
      private final int maxMobsAdded;

      public Properties(int maxMobsAdded) {
         this.maxMobsAdded = maxMobsAdded;
      }

      public int getMaxMobsAdded() {
         return this.maxMobsAdded;
      }
   }
}
