package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractRampageAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.PlayerStat;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RampageChainAbility extends AbstractRampageAbility {
   private int additionalChainCount;

   public RampageChainAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond, float damageIncrease, int additionalChainCount
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, damageIncrease);
      this.additionalChainCount = additionalChainCount;
   }

   public RampageChainAbility() {
   }

   public int getAdditionalChainCount() {
      return this.additionalChainCount;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.RAMPAGE_CHAIN.addTo(player, this.getAdditionalChainCount());
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.RAMPAGE_CHAIN);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.RAMPAGE_CHAIN)) {
            player.removeEffect(ModEffects.RAMPAGE_CHAIN);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE_CHAIN));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE_CHAIN));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.additionalChainCount), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalChainCount = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalChainCount)).ifPresent(tag -> nbt.put("additionalChainCount", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalChainCount = Adapters.INT.readNbt(nbt.get("additionalChainCount")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalChainCount)).ifPresent(element -> json.add("additionalChainCount", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalChainCount = Adapters.INT.readJson(json.get("additionalChainCount")).orElse(0);
   }

   static {
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.ON_HIT_CHAIN)
         .filter(data -> data.getEntity().hasEffect(ModEffects.RAMPAGE_CHAIN))
         .register(RampageChainAbility.class, data -> {
            int amplifier = data.getEntity().getEffect(ModEffects.RAMPAGE_CHAIN).getAmplifier();
            data.setValue(data.getValue() + amplifier);
         });
   }

   public static class RampageChainEffect extends RampageAbility.RampageEffect {
      public RampageChainEffect(int color, ResourceLocation resourceLocation) {
         super(RampageChainAbility.class, color, resourceLocation);
      }
   }
}
