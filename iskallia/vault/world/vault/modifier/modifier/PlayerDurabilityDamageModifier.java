package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class PlayerDurabilityDamageModifier extends VaultModifier<PlayerDurabilityDamageModifier.Properties> {
   public PlayerDurabilityDamageModifier(ResourceLocation id, PlayerDurabilityDamageModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)(Math.abs(p.durabilityDamageTakenMultiplier * s) * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLAYER_STAT.of(PlayerStat.DURABILITY_DAMAGE).register(context.getUUID(), data -> {
         if (data.getEntity().level == world) {
            data.setValue(data.getValue() * this.properties.durabilityDamageTakenMultiplier);
         }
      });
   }

   public static class Properties {
      @Expose
      private final float durabilityDamageTakenMultiplier;

      public Properties(float durabilityDamageTakenMultiplier) {
         this.durabilityDamageTakenMultiplier = durabilityDamageTakenMultiplier;
      }

      public float getDurabilityDamageTakenMultiplier() {
         return this.durabilityDamageTakenMultiplier;
      }
   }
}
