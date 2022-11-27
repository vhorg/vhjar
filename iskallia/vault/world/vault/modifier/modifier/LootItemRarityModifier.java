package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class LootItemRarityModifier extends VaultModifier<LootItemRarityModifier.Properties> {
   public LootItemRarityModifier(ResourceLocation id, LootItemRarityModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getPercentage() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.LOOT_GENERATION
         .pre()
         .register(
            context.getUUID(),
            data -> this.getGenerator(vault, data).ifPresent(generator -> generator.itemRarity = (float)(generator.itemRarity + this.properties.percentage))
         );
      CommonEvents.LOOT_GENERATION
         .post()
         .register(
            context.getUUID(),
            data -> this.getGenerator(vault, data).ifPresent(generator -> generator.itemRarity = (float)(generator.itemRarity - this.properties.percentage))
         );
   }

   public Optional<TieredLootTableGenerator> getGenerator(Vault vault, LootGenerationEvent.Data data) {
      if (data.getGenerator() instanceof TieredLootTableGenerator generator) {
         if (generator.source == null) {
            return Optional.empty();
         } else {
            return !vault.get(Vault.LISTENERS).contains(generator.source.getUUID()) ? Optional.empty() : Optional.of(generator);
         }
      } else {
         return Optional.empty();
      }
   }

   public static class Properties {
      @Expose
      private final double percentage;

      public Properties(double percentage) {
         this.percentage = percentage;
      }

      public double getPercentage() {
         return this.percentage;
      }
   }
}
