package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class LootItemQuantityModifier extends VaultModifier<LootItemQuantityModifier.Properties> {
   public LootItemQuantityModifier(ResourceLocation id, LootItemQuantityModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getPercentage() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.LOOT_GENERATION
         .pre()
         .register(
            context.getUUID(),
            data -> this.getGenerator(vault, data, context)
               .ifPresent(generator -> generator.itemQuantity = (float)(generator.itemQuantity + this.properties.getPercentage(context)))
         );
      CommonEvents.LOOT_GENERATION
         .post()
         .register(
            context.getUUID(),
            data -> this.getGenerator(vault, data, context)
               .ifPresent(generator -> generator.itemQuantity = (float)(generator.itemQuantity - this.properties.getPercentage(context)))
         );
   }

   public Optional<TieredLootTableGenerator> getGenerator(Vault vault, LootGenerationEvent.Data data, ModifierContext context) {
      if (data.getGenerator() instanceof TieredLootTableGenerator generator) {
         if (generator.source == null) {
            return Optional.empty();
         } else {
            UUID uuid = generator.source.getUUID();
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
      private final double percentage;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(double percentage, ScalarReputationProperty reputation) {
         this.percentage = percentage;
         this.reputation = reputation;
      }

      public double getPercentage() {
         return this.percentage;
      }

      public double getPercentage(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.percentage, context) : this.percentage;
      }
   }
}
