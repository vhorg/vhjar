package iskallia.vault.skill.talent.type.health;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public abstract class LowHealthTalent extends ConditionalEntitySkill {
   private float healthThreshold;

   public LowHealthTalent(int unlockLevel, int learnPointCost, int regretPointCost, float healthThreshold, MobEffect effect) {
      super(unlockLevel, learnPointCost, regretPointCost, effect);
      this.healthThreshold = healthThreshold;
   }

   protected LowHealthTalent() {
   }

   @Override
   public boolean shouldGetBenefits(LivingEntity entity) {
      return entity.getHealth() < this.healthThreshold * entity.getMaxHealth();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.healthThreshold), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.healthThreshold = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.healthThreshold)).ifPresent(tag -> nbt.put("healthThreshold", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.healthThreshold = Adapters.FLOAT.readNbt(nbt.get("healthThreshold")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.healthThreshold)).ifPresent(element -> json.add("healthThreshold", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.healthThreshold = Adapters.FLOAT.readJson(json.get("healthThreshold")).orElseThrow();
   }
}
