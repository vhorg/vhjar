package iskallia.vault.block.entity.challenge.elite;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;

public class EliteSpawner implements ISerializable<CompoundTag, JsonObject> {
   private double currentHealth;
   private double totalHealth;
   private UUID bossId;

   public double getCurrentHealth() {
      return this.currentHealth;
   }

   public double getTotalHealth() {
      return this.totalHealth;
   }

   public boolean isCompleted() {
      return this.bossId == null;
   }

   public void onTick(ServerLevel world) {
      this.updateHealth(world);
   }

   private void updateHealth(ServerLevel world) {
      if (this.bossId != null) {
         Entity entity = world.getEntity(this.bossId);
         if (entity == null) {
            this.bossId = null;
         } else {
            double total = 0.0;
            this.currentHealth = 0.0;
            if (entity instanceof LivingEntity living) {
               this.currentHealth = this.currentHealth + living.getHealth();
               total += living.getMaxHealth();
            }

            if (total > this.totalHealth) {
               this.totalHealth = total;
            }
         }
      }
   }

   public void onSpawn(ServerLevel world, BlockPos pos, PartialEntity elite) {
      EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(elite.getId());
      Entity entity = type.spawn(world, null, null, null, pos, MobSpawnType.SPAWNER, false, false);
      elite.getNbt().asWhole().ifPresent(nbt -> entity.deserializeNBT(nbt));
      entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
      world.addFreshEntity(entity);
      this.bossId = entity.getUUID();
      this.updateHealth(world);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.currentHealth)).ifPresent(tag -> nbt.put("currentHealth", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.totalHealth)).ifPresent(tag -> nbt.put("totalHealth", tag));
         Adapters.UUID.writeNbt(this.bossId).ifPresent(tag -> nbt.put("bossId", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.currentHealth = Adapters.DOUBLE.readNbt(nbt.get("currentHealth")).orElse(0.0);
      this.totalHealth = Adapters.DOUBLE.readNbt(nbt.get("totalHealth")).orElse(0.0);
      this.bossId = Adapters.UUID.readNbt(nbt.get("bossId")).orElse(null);
   }
}
