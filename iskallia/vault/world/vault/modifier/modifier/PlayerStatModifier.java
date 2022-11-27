package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class PlayerStatModifier extends VaultModifier<PlayerStatModifier.Properties> {
   public PlayerStatModifier(ResourceLocation id, PlayerStatModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.PLAYER_STAT.register(context.getUUID(), data -> {
         if (vault.get(Vault.LISTENERS).contains(data.getEntity().getUUID())) {
            data.setValue(data.getValue() * this.properties.multiplier);
         }
      });
   }

   public static class Properties {
      @Expose
      private final PlayerStat stat;
      @Expose
      private final float multiplier;

      public Properties(PlayerStat stat, float multiplier) {
         this.stat = stat;
         this.multiplier = multiplier;
      }

      public PlayerStat getStat() {
         return this.stat;
      }

      public float getMultiplier() {
         return this.multiplier;
      }
   }
}
