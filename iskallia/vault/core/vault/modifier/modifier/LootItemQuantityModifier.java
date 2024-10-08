package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class LootItemQuantityModifier extends VaultModifier<LootItemQuantityModifier.Properties> {
   public LootItemQuantityModifier(ResourceLocation id, LootItemQuantityModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getPercentage() * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.LOOT_GENERATION.pre().register(context.getUUID(), data -> this.getGenerator(vault, data, context).ifPresent(generator -> {
         if (this.properties.filter == null) {
            generator.itemQuantity = generator.itemQuantity + this.properties.getPercentage();
         } else {
            generator.itemQuantityOverrides.put(this.properties.filter, this.properties.getPercentage());
         }
      }));
      CommonEvents.LOOT_GENERATION.post().register(context.getUUID(), data -> this.getGenerator(vault, data, context).ifPresent(generator -> {
         if (this.properties.filter == null) {
            generator.itemQuantity = generator.itemQuantity - this.properties.getPercentage();
         } else {
            generator.itemQuantityOverrides.remove(this.properties.filter);
         }
      }));
   }

   public Optional<TieredLootTableGenerator> getGenerator(Vault vault, LootGenerationEvent.Data data, ModifierContext context) {
      if (data.getGenerator() instanceof TieredLootTableGenerator generator) {
         if (generator.getSource() == null) {
            return Optional.empty();
         } else {
            UUID uuid = generator.getSource().getUUID();
            if (!vault.get(Vault.LISTENERS).contains(uuid)) {
               return Optional.empty();
            } else {
               return context.hasTarget() && !context.getTarget().equals(uuid) ? Optional.empty() : Optional.of(generator);
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public static class Properties {
      @Expose
      private final ItemPredicate filter;
      @Expose
      private final float percentage;

      public Properties(ItemPredicate filter, float percentage) {
         this.filter = filter;
         this.percentage = percentage;
      }

      public ItemPredicate getFilter() {
         return this.filter;
      }

      public float getPercentage() {
         return this.percentage;
      }
   }
}
