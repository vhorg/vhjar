package iskallia.vault.skill.ability.effect.spi.core;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.util.calc.CooldownHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public abstract class Ability extends LearnableSkill implements TickingSkill, CooldownSkill {
   private int cooldownTicks;
   private Cooldown cooldown;
   private boolean active;

   public Ability(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.cooldownTicks = cooldownTicks;
      this.cooldown = null;
      this.active = false;
   }

   protected Ability() {
   }

   public boolean isActive() {
      return this.active;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   @Override
   public Optional<Cooldown> getCooldown() {
      return Optional.ofNullable(this.cooldown);
   }

   protected void setCooldown(int cooldownTicks, int cooldownDelayTicks) {
      this.cooldown = new Cooldown(cooldownTicks, cooldownTicks, cooldownDelayTicks);
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   @Deprecated
   public abstract String getAbilityGroupName();

   @Override
   public void onTick(SkillContext context) {
      if (this.cooldown != null) {
         this.cooldown.decrement();
         if (this.cooldown.remainingTicks <= 0) {
            this.cooldown = null;
         }
      }

      if (!this.isUnlocked()) {
         this.setActive(false);
      } else {
         if (this.isActive()) {
            Ability.TickResult result = this.doActiveTick(context);
            if (result == Ability.TickResult.COOLDOWN) {
               this.putOnCooldown(context);
            }
         } else {
            Ability.TickResult result = this.doInactiveTick(context);
            if (result == Ability.TickResult.COOLDOWN) {
               this.putOnCooldown(context);
            }
         }
      }
   }

   public Ability.TickResult doActiveTick(SkillContext context) {
      return Ability.TickResult.PASS;
   }

   public Ability.TickResult doInactiveTick(SkillContext context) {
      return Ability.TickResult.PASS;
   }

   public boolean onKeyDown(SkillContext context) {
      return this.isUnlocked() && !this.isTreeOnCooldown();
   }

   public boolean onKeyUp(SkillContext context) {
      if (this.isUnlocked() && !this.isTreeOnCooldown()) {
         return true;
      } else {
         context.getSource()
            .as(Entity.class)
            .ifPresent(entity -> entity.level.playSound(null, entity, ModSounds.ABILITY_ON_COOLDOWN, SoundSource.PLAYERS, 1.0F, 1.0F));
         return false;
      }
   }

   public boolean onCancelKeyDown(SkillContext context) {
      return this.isUnlocked() && !this.isTreeOnCooldown();
   }

   public void onFocus(SkillContext context) {
   }

   public void onBlur(SkillContext context) {
   }

   @Override
   public boolean isOnCooldown() {
      return this.cooldown != null && this.cooldown.remainingTicks > 0;
   }

   @Override
   public void putOnCooldown(int cooldownDelayTicks, SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresentOrElse(
            player -> this.setCooldown(CooldownHelper.adjustCooldown(player, this.getId(), this.cooldownTicks), cooldownDelayTicks),
            () -> this.setCooldown(this.cooldownTicks, cooldownDelayTicks)
         );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.cooldownTicks), buffer);
      Cooldown.ADAPTER.writeBits(this.cooldown, buffer);
      Adapters.BOOLEAN.writeBits(this.active, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.cooldownTicks = Adapters.INT.readBits(buffer).orElse(0);
      this.cooldown = Cooldown.ADAPTER.readBits(buffer).orElse(null);
      this.active = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.cooldownTicks)).ifPresent(tag -> nbt.put("cooldownTicks", tag));
         Cooldown.ADAPTER.writeNbt(this.cooldown).ifPresent(tag -> nbt.put("cooldown", tag));
         Adapters.BOOLEAN.writeNbt(this.active).ifPresent(tag -> nbt.put("active", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.cooldownTicks = Adapters.INT.readNbt(nbt.get("cooldownTicks")).orElse(0);
      this.cooldown = Cooldown.ADAPTER.readNbt((CompoundTag)nbt.get("cooldown")).orElse(null);
      this.active = Adapters.BOOLEAN.readNbt(nbt.get("active")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.cooldownTicks)).ifPresent(element -> json.add("cooldownTicks", element));
         Cooldown.ADAPTER.writeJson(this.cooldown).ifPresent(element -> json.add("cooldown", element));
         Adapters.BOOLEAN.writeJson(this.active).ifPresent(element -> json.add("active", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.cooldownTicks = Adapters.INT.readJson(json.get("cooldownTicks")).orElse(0);
      this.cooldown = Cooldown.ADAPTER.readJson((JsonObject)json.get("cooldown")).orElse(null);
      this.active = Adapters.BOOLEAN.readJson(json.get("active")).orElse(false);
   }

   public static class ActionResult {
      private static final Ability.ActionResult FAIL = new Ability.ActionResult(Ability.ActionResult.State.FAIL, false, 0);
      private static final Ability.ActionResult SUCCESS_COOLDOWN_IMMEDIATE = new Ability.ActionResult(Ability.ActionResult.State.SUCCESS, true, 0);
      private static final Ability.ActionResult SUCCESS_COOLDOWN_DEFERRED = new Ability.ActionResult(Ability.ActionResult.State.SUCCESS, false, 0);
      private final Ability.ActionResult.State state;
      private final boolean startCooldown;
      private final int cooldownDelayTicks;

      private ActionResult(Ability.ActionResult.State state, boolean startCooldown, int cooldownDelayTicks) {
         this.state = state;
         this.startCooldown = startCooldown;
         this.cooldownDelayTicks = cooldownDelayTicks;
      }

      public boolean isSuccess() {
         return this.state == Ability.ActionResult.State.SUCCESS;
      }

      public boolean startCooldown() {
         return this.startCooldown;
      }

      public int getCooldownDelayTicks() {
         return this.cooldownDelayTicks;
      }

      public static Ability.ActionResult fail() {
         return FAIL;
      }

      public static Ability.ActionResult successCooldownImmediate() {
         return SUCCESS_COOLDOWN_IMMEDIATE;
      }

      public static Ability.ActionResult successCooldownDeferred() {
         return SUCCESS_COOLDOWN_DEFERRED;
      }

      public static Ability.ActionResult successCooldownDelayed(int cooldownDelayTicks) {
         return new Ability.ActionResult(Ability.ActionResult.State.SUCCESS, true, cooldownDelayTicks);
      }

      public static enum State {
         FAIL,
         SUCCESS;
      }
   }

   public static enum ActivityFlag {
      NO_OP,
      DEACTIVATE_ABILITY,
      ACTIVATE_ABILITY;
   }

   public static enum TickResult {
      PASS,
      COOLDOWN;
   }
}
