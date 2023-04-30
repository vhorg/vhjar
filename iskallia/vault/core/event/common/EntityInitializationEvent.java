package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityInitializationEvent extends Event<EntityInitializationEvent, EntityInitializationEvent.Data> {
   public EntityInitializationEvent() {
   }

   protected EntityInitializationEvent(EntityInitializationEvent parent) {
      super(parent);
   }

   public EntityInitializationEvent createChild() {
      return new EntityInitializationEvent(this);
   }

   public EntityInitializationEvent.Data invoke(
      Mob mob, ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag nbt
   ) {
      return this.invoke(new EntityInitializationEvent.Data(mob, world, difficulty, reason, data, nbt));
   }

   public static class Data {
      private final Mob mob;
      private final ServerLevelAccessor world;
      private final DifficultyInstance difficulty;
      private final MobSpawnType reason;
      private final SpawnGroupData data;
      private final CompoundTag nbt;

      public Data(Mob mob, ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag nbt) {
         this.mob = mob;
         this.world = world;
         this.difficulty = difficulty;
         this.reason = reason;
         this.data = data;
         this.nbt = nbt;
      }

      public Mob getMob() {
         return this.mob;
      }

      public ServerLevelAccessor getWorld() {
         return this.world;
      }

      public DifficultyInstance getDifficulty() {
         return this.difficulty;
      }

      public MobSpawnType getReason() {
         return this.reason;
      }

      public SpawnGroupData getData() {
         return this.data;
      }

      public CompoundTag getNbt() {
         return this.nbt;
      }
   }
}
