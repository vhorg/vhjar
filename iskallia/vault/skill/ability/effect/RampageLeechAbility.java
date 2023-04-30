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
import net.minecraft.util.Mth;

public class RampageLeechAbility extends AbstractRampageAbility {
   private float leechPercent;

   public RampageLeechAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond, float damageIncrease, float leechPercent
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, damageIncrease);
      this.leechPercent = leechPercent;
   }

   public RampageLeechAbility() {
   }

   public float getLeechPercent() {
      return this.leechPercent;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            int amplifier = (int)Mth.clamp(this.getLeechPercent() * 100.0F, 0.0F, 100.0F);
            ModEffects.RAMPAGE_LEECH.addTo(player, amplifier);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.RAMPAGE_LEECH);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.RAMPAGE_LEECH)) {
            player.removeEffect(ModEffects.RAMPAGE_LEECH);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE_LEECH));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE_LEECH));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.leechPercent), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.leechPercent = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.leechPercent)).ifPresent(tag -> nbt.put("leechPercent", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.leechPercent = Adapters.FLOAT.readNbt(nbt.get("leechPercent")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.leechPercent)).ifPresent(element -> json.add("leechPercent", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.leechPercent = Adapters.FLOAT.readJson(json.get("leechPercent")).orElse(0.0F);
   }

   static {
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.LEECH)
         .filter(data -> data.getEntity().hasEffect(ModEffects.RAMPAGE_LEECH))
         .register(RampageLeechAbility.class, data -> {
            int amplifier = data.getEntity().getEffect(ModEffects.RAMPAGE_LEECH).getAmplifier();
            float leechPercent = amplifier / 100.0F;
            data.setValue(data.getValue() + leechPercent);
         });
   }

   public static class RampageLeechEffect extends RampageAbility.RampageEffect {
      public RampageLeechEffect(int color, ResourceLocation resourceLocation) {
         super(RampageLeechAbility.class, color, resourceLocation);
      }
   }
}
