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

public class PlayerStatModifier extends VaultModifier<PlayerStatModifier.Properties> {
   public PlayerStatModifier(ResourceLocation id, PlayerStatModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.addend * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLAYER_STAT.register(context.getUUID(), data -> {
         if (vault.get(Vault.LISTENERS).contains(data.getEntity().getUUID())) {
            if (!context.hasTarget() || context.getTarget().equals(data.getEntity().getUUID())) {
               data.setValue(data.getValue() + this.properties.getAddend(context));
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final PlayerStat stat;
      @Expose
      private final float addend;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(PlayerStat stat, float addend, ScalarReputationProperty reputation) {
         this.stat = stat;
         this.addend = addend;
         this.reputation = reputation;
      }

      public PlayerStat getStat() {
         return this.stat;
      }

      public float getAddend() {
         return this.addend;
      }

      public float getAddend(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.addend, context) : this.addend;
      }
   }
}
