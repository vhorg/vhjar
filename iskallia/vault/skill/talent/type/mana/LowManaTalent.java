package iskallia.vault.skill.talent.type.mana;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.mana.ManaPlayer;
import iskallia.vault.skill.talent.type.health.ConditionalEntitySkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public abstract class LowManaTalent extends ConditionalEntitySkill {
   private float manaThreshold;

   public LowManaTalent(int unlockLevel, int learnPointCost, int regretPointCost, MobEffect effect, float manaThreshold) {
      super(unlockLevel, learnPointCost, regretPointCost, effect);
      this.manaThreshold = manaThreshold;
   }

   protected LowManaTalent() {
   }

   @Override
   public boolean shouldGetBenefits(LivingEntity entity) {
      return entity instanceof ManaPlayer owner ? owner.getMana() < this.manaThreshold * owner.getManaMax() : false;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.manaThreshold), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.manaThreshold = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.manaThreshold)).ifPresent(tag -> nbt.put("manaThreshold", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.manaThreshold = Adapters.FLOAT.readNbt(nbt.get("manaThreshold")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.manaThreshold)).ifPresent(element -> json.add("manaThreshold", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.manaThreshold = Adapters.FLOAT.readJson(json.get("manaThreshold")).orElseThrow();
   }
}
