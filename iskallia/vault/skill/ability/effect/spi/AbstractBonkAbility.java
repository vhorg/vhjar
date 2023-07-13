package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBonkAbility extends InstantManaAbility {
   public static int CHAMPION_COUNT = 30;
   public static int DUNGEON_COUNT = 10;
   public static int TANK_COUNT = 10;
   public static int ASSASSIN_COUNT = 3;
   public static int HORDE_COUNT = 1;
   private float radius;
   private int maxStacksTotal;
   private int maxStacksUsedPerHit;
   private int stackDuration;

   public AbstractBonkAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      int maxStacksTotal,
      int maxStacksUsedPerHit,
      int stackDuration
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.radius = radius;
      this.maxStacksTotal = maxStacksTotal;
      this.maxStacksUsedPerHit = maxStacksUsedPerHit;
      this.stackDuration = stackDuration;
   }

   protected AbstractBonkAbility() {
   }

   public int getMaxStacksUsedPerHit() {
      return this.maxStacksUsedPerHit;
   }

   public int getStackDuration() {
      return this.stackDuration;
   }

   public int getMaxStacksTotal() {
      return this.maxStacksTotal;
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, realRadius);
      }

      return realRadius;
   }

   @Override
   public String getAbilityGroupName() {
      return "Battle_Cry";
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos) {
      float radius = this.getRadius(attacker);
      return world.getNearbyEntities(
         LivingEntity.class,
         TargetingConditions.forCombat().range(radius).selector(entity -> !(entity instanceof Player)),
         attacker,
         AABBHelper.create(pos, radius)
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.maxStacksTotal), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.maxStacksUsedPerHit), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.stackDuration), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.maxStacksTotal = Adapters.INT.readBits(buffer).orElseThrow();
      this.maxStacksUsedPerHit = Adapters.INT.readBits(buffer).orElseThrow();
      this.stackDuration = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxStacksTotal)).ifPresent(tag -> nbt.put("maxStacksTotal", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxStacksUsedPerHit)).ifPresent(tag -> nbt.put("maxStacksUsedPerHit", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.stackDuration)).ifPresent(tag -> nbt.put("stackDuration", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.maxStacksTotal = Adapters.INT.readNbt(nbt.get("maxStacksTotal")).orElse(0);
      this.maxStacksUsedPerHit = Adapters.INT.readNbt(nbt.get("maxStacksUsedPerHit")).orElse(0);
      this.stackDuration = Adapters.INT.readNbt(nbt.get("stackDuration")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxStacksTotal)).ifPresent(element -> json.add("maxStacksTotal", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxStacksUsedPerHit)).ifPresent(element -> json.add("maxStacksUsedPerHit", element));
         Adapters.INT.writeJson(Integer.valueOf(this.stackDuration)).ifPresent(element -> json.add("stackDuration", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.maxStacksTotal = Adapters.INT.readJson(json.get("maxStacksTotal")).orElse(0);
      this.maxStacksUsedPerHit = Adapters.INT.readJson(json.get("maxStacksUsedPerHit")).orElse(0);
      this.stackDuration = Adapters.INT.readJson(json.get("stackDuration")).orElse(0);
   }

   public static class BattleCryEffect extends MobEffect {
      public BattleCryEffect(MobEffectCategory category, int color, ResourceLocation id) {
         super(category, color);
         this.setRegistryName(id);
      }
   }
}
