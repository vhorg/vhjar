package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.NaturalSpawner;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class MobSpawnCountModifier extends VaultModifier<MobSpawnCountModifier.Properties> {
   public MobSpawnCountModifier(ResourceLocation id, MobSpawnCountModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted(p.getMaxMobsAdded() * s));
   }

   @Override
   public void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      if (!context.hasTarget() || context.getTarget().equals(listener.getId())) {
         listener.ifPresent(
            Runner.SPAWNER, spawner -> spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, maxMobs -> maxMobs + this.properties().getMaxMobsAdded(context))
         );
      }
   }

   @Override
   public void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
      if (!context.hasTarget() || context.getTarget().equals(listener.getId())) {
         listener.ifPresent(
            Runner.SPAWNER, spawner -> spawner.modify(NaturalSpawner.EXTRA_MAX_MOBS, maxMobs -> maxMobs - this.properties().getMaxMobsAdded(context))
         );
      }
   }

   public static class Properties {
      @Expose
      private final int maxMobsAdded;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(int maxMobsAdded, ScalarReputationProperty reputation) {
         this.maxMobsAdded = maxMobsAdded;
         this.reputation = reputation;
      }

      public int getMaxMobsAdded() {
         return this.maxMobsAdded;
      }

      public int getMaxMobsAdded(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.maxMobsAdded, context) : this.maxMobsAdded;
      }
   }
}
