package iskallia.vault.skill.talent.type.health;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class ConditionalEntitySkill extends LearnableSkill implements TickingSkill {
   private MobEffect effect;

   public ConditionalEntitySkill(int unlockLevel, int learnPointCost, int regretPointCost, MobEffect effect) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.effect = effect;
   }

   protected ConditionalEntitySkill() {
   }

   public abstract boolean shouldGetBenefits(LivingEntity var1);

   @Override
   public void onTick(SkillContext context) {
      context.getSource().as(LivingEntity.class).ifPresent(entity -> {
         if (this.isUnlocked() && this.shouldGetBenefits(entity)) {
            this.addEffect(entity);
         }
      });
   }

   public void addEffect(LivingEntity entity) {
      if (this.effect != null) {
         entity.removeEffect(this.effect);
         entity.addEffect(new MobEffectInstance(this.effect, 20, 0, true, false, true));
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.EFFECT.asNullable().writeBits((IForgeRegistryEntry)this.effect, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.effect = (MobEffect)Adapters.EFFECT.asNullable().readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.EFFECT.writeNbt((IForgeRegistryEntry)this.effect).ifPresent(tag -> nbt.put("effect", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.effect = (MobEffect)Adapters.EFFECT.readNbt(nbt.get("effect")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.EFFECT.writeJson((IForgeRegistryEntry)this.effect).ifPresent(element -> json.add("effect", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.effect = (MobEffect)Adapters.EFFECT.readJson(json.get("effect")).orElse(null);
   }
}
