package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.eventbus.api.EventPriority;

public class DropOnKillModifier extends VaultModifier<DropOnKillModifier.Properties> {
   public DropOnKillModifier(ResourceLocation id, DropOnKillModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_DROPS
         .register(
            context.getUUID(),
            EventPriority.HIGHEST,
            event -> {
               if (event.getSource().getEntity() instanceof ServerPlayer attacker && !attacker.getLevel().isClientSide()) {
                  if (attacker.level == world) {
                     if (event.getEntity().getTags().contains("soul_shards")) {
                        if (!IModifierImmunity.of(event.getEntity()).test(this)) {
                           if (this.properties.filter.test(event.getEntity())) {
                              LootTableKey lootTable = VaultRegistry.LOOT_TABLE.getKey(this.properties.lootTable);
                              if (lootTable != null) {
                                 LootTableGenerator generator = new LootTableGenerator(Version.latest(), lootTable, 0.0F);
                                 generator.generate(JavaRandom.ofNanoTime());
                                 generator.getItems()
                                    .forEachRemaining(
                                       stack -> {
                                          ItemEntity itemEntity = new ItemEntity(
                                             world, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack.copy()
                                          );
                                          itemEntity.setDefaultPickUpDelay();
                                          event.getDrops().add(itemEntity);
                                       }
                                    );
                              }
                           }
                        }
                     }
                  }
               }
            },
            -100
         );
   }

   @Override
   public void releaseServer(ModifierContext context) {
      CommonEvents.ENTITY_DROPS.release(context.getUUID());
   }

   public static class Properties {
      @Expose
      private EntityPredicate filter;
      @Expose
      private ResourceLocation lootTable;

      public Properties(EntityPredicate filter, ResourceLocation lootTable) {
         this.filter = filter;
         this.lootTable = lootTable;
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public ResourceLocation getLootTable() {
         return this.lootTable;
      }
   }
}
