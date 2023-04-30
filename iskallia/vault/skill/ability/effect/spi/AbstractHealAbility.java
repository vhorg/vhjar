package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractHealAbility extends InstantManaAbility {
   protected float flatLifeHealed;

   public AbstractHealAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
   }

   protected AbstractHealAbility() {
   }

   public float getFlatLifeHealed() {
      return this.flatLifeHealed;
   }

   @Override
   public String getAbilityGroupName() {
      return "Heal";
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.flatLifeHealed), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.flatLifeHealed = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.flatLifeHealed)).ifPresent(tag -> nbt.put("flatLifeHealed", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.flatLifeHealed = Adapters.FLOAT.readNbt(nbt.get("flatLifeHealed")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.flatLifeHealed)).ifPresent(element -> json.add("flatLifeHealed", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.flatLifeHealed = Adapters.FLOAT.readJson(json.get("flatLifeHealed")).orElse(0.0F);
   }
}
