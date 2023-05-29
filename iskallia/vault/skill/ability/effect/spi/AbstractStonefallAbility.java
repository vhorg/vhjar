package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractStonefallAbility extends InstantManaAbility {
   private int durationTicks;
   private float knockbackMultiplier;
   private float radius;
   private float damageReduction;

   public AbstractStonefallAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int durationTicks,
      float knockbackMultiplier,
      float radius,
      float damageReduction
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.durationTicks = durationTicks;
      this.knockbackMultiplier = knockbackMultiplier;
      this.radius = radius;
      this.damageReduction = damageReduction;
   }

   public AbstractStonefallAbility() {
   }

   @Override
   public String getAbilityGroupName() {
      return "Stonefall";
   }

   public float getDamageReduction() {
      return this.damageReduction;
   }

   public float getRadius() {
      return this.radius;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public float getKnockbackMultiplier() {
      return this.knockbackMultiplier;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.knockbackMultiplier), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageReduction), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.knockbackMultiplier = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.damageReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.knockbackMultiplier)).ifPresent(tag -> nbt.put("knockbackMultiplier", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageReduction)).ifPresent(tag -> nbt.put("damageReduction", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
      this.knockbackMultiplier = Adapters.FLOAT.readNbt(nbt.get("knockbackMultiplier")).orElse(0.0F);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.damageReduction = Adapters.FLOAT.readNbt(nbt.get("damageReduction")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.knockbackMultiplier)).ifPresent(element -> json.add("knockbackMultiplier", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageReduction)).ifPresent(element -> json.add("damageReduction", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
      this.knockbackMultiplier = Adapters.FLOAT.readJson(json.get("knockbackMultiplier")).orElse(0.0F);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.damageReduction = Adapters.FLOAT.readJson(json.get("damageReduction")).orElse(0.0F);
   }
}
