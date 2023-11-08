package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class NoSoulShardsModifier extends VaultModifier<NoSoulShardsModifier.Properties> {
   public NoSoulShardsModifier(ResourceLocation id, NoSoulShardsModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_READ.register(context.getUUID(), data -> {
         if (data.getEntity().level == world) {
            if (this.properties.filter.test(data.getEntity())) {
               if (data.getEntity().level.getRandom().nextDouble() < this.properties.chance) {
                  data.getEntity().getTags().remove("soul_shards");
               }
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final EntityPredicate filter;
      @Expose
      private final double chance;

      public Properties(EntityPredicate filter, double chance) {
         this.filter = filter;
         this.chance = chance;
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public double getChance() {
         return this.chance;
      }
   }
}
