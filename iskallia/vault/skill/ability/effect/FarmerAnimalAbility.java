package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.phys.AABB;

public class FarmerAnimalAbility extends FarmerAbility {
   private static final int PARTICLE_COUNT = 20;
   private static final Predicate<AgeableMob> AGEABLE_MOB_PREDICATE = entity -> entity.isAlive() && !entity.isSpectator() && entity.isBaby();
   private float adultChance;

   public FarmerAnimalAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      int tickDelay,
      int horizontalRange,
      int verticalRange,
      float adultChance
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, tickDelay, horizontalRange, verticalRange);
      this.adultChance = adultChance;
   }

   public FarmerAnimalAbility() {
   }

   public float getAdultChance() {
      return this.adultChance;
   }

   @Override
   protected void doGrow(ServerPlayer player, ServerLevel world) {
      super.doGrow(player, world);
      int horizontalRange = this.getHorizontalRange();
      int verticalRange = this.getVerticalRange();
      horizontalRange = Math.round(AreaOfEffectHelper.adjustAreaOfEffect(player, horizontalRange));
      verticalRange = Math.round(AreaOfEffectHelper.adjustAreaOfEffect(player, verticalRange));
      AABB searchBox = AABBHelper.create(player.position(), horizontalRange, verticalRange, horizontalRange);

      for (AgeableMob entity : world.getEntitiesOfClass(AgeableMob.class, searchBox, AGEABLE_MOB_PREDICATE)) {
         if (world.getRandom().nextFloat() < 0.4F) {
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY(), entity.getZ(), 20, 0.5, 0.5, 0.5, 0.0);
         }

         if (world.getRandom().nextFloat() < this.getAdultChance()) {
            entity.setBaby(false);
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.adultChance), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.adultChance = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.adultChance)).ifPresent(tag -> nbt.put("adultChance", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.adultChance = Adapters.FLOAT.readNbt(nbt.get("adultChance")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.adultChance)).ifPresent(element -> json.add("adultChance", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.adultChance = Adapters.FLOAT.readJson(json.get("adultChance")).orElse(0.0F);
   }
}
