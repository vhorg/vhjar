package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class StackingGearAttributeTalent extends GearAttributeTalent {
   private MobEffect effect;
   private int durationTicks;
   private int maxStacks;
   private int timeLeft;
   private int stacks;

   public StackingGearAttributeTalent(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      VaultGearAttribute<?> attribute,
      double value,
      MobEffect effect,
      int durationTicks,
      int maxStacks
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, attribute, value);
      this.effect = effect;
      this.durationTicks = durationTicks;
      this.maxStacks = maxStacks;
   }

   public StackingGearAttributeTalent() {
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   @Override
   public double getValue() {
      return super.getValue() * this.stacks;
   }

   @Override
   public boolean canApply(SkillContext context) {
      return super.canApply(context) && this.timeLeft > 0 && this.stacks > 0;
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      if (--this.timeLeft < 0) {
         this.timeLeft = 0;
         this.stacks = 0;
      }

      if (this.isUnlocked() && this.effect != null && this.timeLeft > 0 && this.stacks > 0) {
         context.getSource().as(ServerPlayer.class).ifPresent(player -> {
            player.removeEffect(this.effect);
            player.addEffect(new MobEffectInstance(this.effect, this.timeLeft, this.stacks - 1, true, false, true));
         });
      }
   }

   public void onStack(ServerPlayer player) {
      if (++this.stacks > this.maxStacks) {
         this.stacks = this.maxStacks;
      }

      this.timeLeft = this.durationTicks;
      this.refreshSnapshot(player);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.EFFECT.writeBits((IForgeRegistryEntry)this.effect, buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.maxStacks), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.timeLeft), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.stacks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.effect = (MobEffect)Adapters.EFFECT.readBits(buffer).orElseThrow();
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.maxStacks = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.timeLeft = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.stacks = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.EFFECT.writeNbt((IForgeRegistryEntry)this.effect).ifPresent(tag -> nbt.put("effect", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxStacks)).ifPresent(tag -> nbt.put("maxStacks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.timeLeft)).ifPresent(tag -> nbt.put("timeLeft", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.stacks)).ifPresent(tag -> nbt.put("stacks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.effect = (MobEffect)Adapters.EFFECT.readNbt(nbt.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + nbt));
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
      this.maxStacks = Adapters.INT.readNbt(nbt.get("maxStacks")).orElse(0);
      this.timeLeft = Adapters.INT.readNbt(nbt.get("timeLeft")).orElse(0);
      this.stacks = Adapters.INT.readNbt(nbt.get("stacks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.EFFECT.writeJson((IForgeRegistryEntry)this.effect).ifPresent(element -> json.add("effect", element));
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxStacks)).ifPresent(element -> json.add("maxStacks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.timeLeft)).ifPresent(element -> json.add("timeLeft", element));
         Adapters.INT.writeJson(Integer.valueOf(this.stacks)).ifPresent(element -> json.add("stacks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.effect = (MobEffect)Adapters.EFFECT.readJson(json.get("effect")).orElseThrow(() -> new IllegalStateException("Unknown effect in " + json));
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
      this.maxStacks = Adapters.INT.readJson(json.get("maxStacks")).orElse(0);
      this.timeLeft = Adapters.INT.readJson(json.get("timeLeft")).orElse(0);
      this.stacks = Adapters.INT.readJson(json.get("stacks")).orElse(0);
   }

   static {
      CommonEvents.ENTITY_DROPS.register(StackingGearAttributeTalent.class, EventPriority.HIGHEST, event -> {
         Entity attacker = event.getSource().getEntity();
         if (attacker instanceof ServerPlayer player && !attacker.getLevel().isClientSide()) {
            if (attacker.getLevel() == event.getEntity().getLevel()) {
               TalentTree tree = PlayerTalentsData.get(attacker.getServer()).getTalents(player);
               tree.getAll(StackingGearAttributeTalent.class, Skill::isUnlocked).forEach(talent -> talent.onStack(player));
            }
         }
      });
   }
}
