package iskallia.vault.core.vault.enhancement;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class KillMobsEnhancementTask extends IntFilterEnhancementTask<KillMobsEnhancementTask.Config> {
   public KillMobsEnhancementTask() {
   }

   public KillMobsEnhancementTask(KillMobsEnhancementTask.Config config, UUID vault, UUID player, UUID altar, int requiredCount) {
      super(config, vault, player, altar, requiredCount);
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         Entity killer = event.getSource().getEntity();
         if (killer != null && this.belongsTo(killer)) {
            if (event.getEntity().level == killer.level) {
               if (this.config.isValid(event.getEntity())) {
                  this.count++;
               }
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.ENTITY_DEATH.release(this);
   }

   public static class Config extends IntFilterEnhancementTask.Config<KillMobsEnhancementTask> {
      private static final ArrayAdapter<EntityPredicate> FILTER = Adapters.ofArray(EntityPredicate[]::new, Adapters.ENTITY_PREDICATE);
      protected EntityPredicate[] filter;

      public Config() {
      }

      public Config(String display, IntRoll range, EntityPredicate... filter) {
         super(display, range);
         this.filter = filter;
      }

      public KillMobsEnhancementTask create(Vault vault, Player player, VaultEnhancementAltarTileEntity altar, RandomSource random) {
         return new KillMobsEnhancementTask(this, vault.get(Vault.ID), player.getUUID(), altar.getUUID(), this.range.get(random));
      }

      public boolean isValid(Entity entity) {
         for (EntityPredicate filter : this.filter) {
            if (filter.test(entity)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            FILTER.writeNbt(this.filter).ifPresent(tag -> nbt.put("filter", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = FILTER.readNbt(nbt.get("filter")).orElse(new EntityPredicate[0]);
      }
   }
}
