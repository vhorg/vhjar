package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.skill.talent.GearAttributeSkill;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class EffectTalent extends LearnableSkill implements GearAttributeSkill, TickingSkill {
   private MobEffect effect;
   private int amplifier;

   public EffectTalent(int unlockLevel, int learnPointCost, int regretPointCost, MobEffect effect, int amplifier) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.effect = effect;
      this.amplifier = amplifier;
   }

   public EffectTalent() {
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public MobEffectInstance toEffect(int duration) {
      return new MobEffectInstance(this.effect, duration, this.amplifier, false, false, true);
   }

   @Override
   public void onAdd(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::refreshSnapshot);
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::refreshSnapshot);
   }

   @Override
   public void onTick(SkillContext context) {
      if (!this.isUnlocked()) {
         this.onRemoveModifiers(context);
      } else {
         this.onAddModifiers(context);
      }
   }

   @Override
   public Stream<VaultGearAttributeInstance<?>> getGearAttributes(SkillContext context) {
      return Stream.of(new VaultGearAttributeInstance<>(ModGearAttributes.EFFECT, new EffectGearAttribute(this.effect, this.amplifier)));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.EFFECT.writeBits((IForgeRegistryEntry)this.effect, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.amplifier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.effect = (MobEffect)Adapters.EFFECT.readBits(buffer).orElseThrow();
      this.amplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.EFFECT.writeNbt((IForgeRegistryEntry)this.effect).ifPresent(tag -> nbt.put("effect", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.amplifier)).ifPresent(tag -> nbt.put("amplifier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.effect = (MobEffect)Adapters.EFFECT.readNbt(nbt.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + nbt));
      this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.EFFECT.writeJson((IForgeRegistryEntry)this.effect).ifPresent(element -> json.add("effect", element));
         Adapters.INT.writeJson(Integer.valueOf(this.amplifier)).ifPresent(element -> json.add("amplifier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.effect = (MobEffect)Adapters.EFFECT.readJson(json.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + json));
      this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(0);
   }
}
