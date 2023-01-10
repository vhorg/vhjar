package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class PlayerDurabilityDamageModifier extends VaultModifier<PlayerDurabilityDamageModifier.Properties> {
   public PlayerDurabilityDamageModifier(ResourceLocation id, PlayerDurabilityDamageModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)(Math.abs(p.getDurabilityDamageTakenMultiplier() * s) * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLAYER_STAT.of(PlayerStat.DURABILITY_DAMAGE).register(context.getUUID(), data -> {
         if (data.getEntity().level == world) {
            if (!context.hasTarget() || context.getTarget().equals(data.getEntity().getUUID())) {
               data.setValue(data.getValue() * this.properties.getDurabilityDamageTakenMultiplier(context));
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final float durabilityDamageTakenMultiplier;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(float durabilityDamageTakenMultiplier, ScalarReputationProperty reputation) {
         this.durabilityDamageTakenMultiplier = durabilityDamageTakenMultiplier;
         this.reputation = reputation;
      }

      public float getDurabilityDamageTakenMultiplier() {
         return this.durabilityDamageTakenMultiplier;
      }

      public float getDurabilityDamageTakenMultiplier(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.durabilityDamageTakenMultiplier, context) : this.durabilityDamageTakenMultiplier;
      }
   }
}
