package iskallia.vault.skill.ability.effect.spi.core;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.util.calc.CooldownHelper;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public abstract class Ability extends LearnableSkill implements TickingSkill {
   public static final SerializableAdapter<Ability.Cooldown, CompoundTag, JsonObject> COOLDOWN = new SerializableAdapter<>(Ability.Cooldown::new, true);
   private int cooldownTicks;
   private Ability.Cooldown cooldown;
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

   public Ability.Cooldown getCooldown() {
      return this.cooldown;
   }

   public int getRemainingCooldown() {
      return this.cooldown != null ? this.cooldown.remainingCooldownTicks : 0;
   }

   public int getTotalCooldown() {
      return this.cooldown != null ? this.cooldown.maxCooldownTicks : 0;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public abstract String getAbilityGroupName();

   @Override
   public void onTick(SkillContext context) {
      if (this.cooldown != null) {
         this.cooldown.decrement();
         if (this.cooldown.remainingCooldownTicks <= 0) {
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

   public void onKeyDown(SkillContext context) {
   }

   public void onKeyUp(SkillContext context) {
   }

   public void onCancelKeyDown(SkillContext context) {
   }

   public void onFocus(SkillContext context) {
   }

   public void onBlur(SkillContext context) {
   }

   public boolean isOnCooldown() {
      return this.cooldown != null && this.cooldown.remainingCooldownTicks > 0;
   }

   public void putOnCooldown(SkillContext context) {
      this.putOnCooldown(0, context);
   }

   public void putOnCooldown(int cooldownDelayTicks, SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresentOrElse(
            player -> this.putOnCooldown(CooldownHelper.adjustCooldown(player, this.getId(), this.cooldownTicks), cooldownDelayTicks, context),
            () -> this.putOnCooldown(this.cooldownTicks, cooldownDelayTicks, context)
         );
   }

   public void putOnCooldown(int cooldownTicks, int cooldownDelayTicks, SkillContext context) {
      this.putOnCooldown(cooldownTicks, cooldownTicks, cooldownDelayTicks, context);
   }

   public void putOnCooldown(int cooldownTicks, int maxCooldownTicks, int cooldownDelayTicks, SkillContext context) {
      this.cooldown = new Ability.Cooldown(cooldownTicks, maxCooldownTicks, cooldownDelayTicks);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.cooldownTicks), buffer);
      COOLDOWN.writeBits(this.cooldown, buffer);
      Adapters.BOOLEAN.writeBits(this.active, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.cooldownTicks = Adapters.INT.readBits(buffer).orElse(0);
      this.cooldown = COOLDOWN.readBits(buffer).orElse(null);
      this.active = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.cooldownTicks)).ifPresent(tag -> nbt.put("cooldownTicks", tag));
         COOLDOWN.writeNbt(this.cooldown).ifPresent(tag -> nbt.put("cooldown", tag));
         Adapters.BOOLEAN.writeNbt(this.active).ifPresent(tag -> nbt.put("active", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.cooldownTicks = Adapters.INT.readNbt(nbt.get("cooldownTicks")).orElse(0);
      this.cooldown = COOLDOWN.readNbt((CompoundTag)nbt.get("cooldown")).orElse(null);
      this.active = Adapters.BOOLEAN.readNbt(nbt.get("active")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.cooldownTicks)).ifPresent(element -> json.add("cooldownTicks", element));
         COOLDOWN.writeJson(this.cooldown).ifPresent(element -> json.add("cooldown", element));
         Adapters.BOOLEAN.writeJson(this.active).ifPresent(element -> json.add("active", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.cooldownTicks = Adapters.INT.readJson(json.get("cooldownTicks")).orElse(0);
      this.cooldown = COOLDOWN.readJson((JsonObject)json.get("cooldown")).orElse(null);
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

   public static class Cooldown implements ISerializable<CompoundTag, JsonObject> {
      public int maxCooldownTicks;
      public int remainingCooldownTicks;
      public int remainingCooldownDelayTicks;

      public Cooldown(int maxCooldownTicks, int remainingCooldownTicks, int remainingCooldownDelayTicks) {
         this.maxCooldownTicks = maxCooldownTicks;
         this.remainingCooldownTicks = remainingCooldownTicks;
         this.remainingCooldownDelayTicks = remainingCooldownDelayTicks;
      }

      public Cooldown() {
      }

      public Ability.Cooldown decrement() {
         if (this.remainingCooldownDelayTicks > 0) {
            this.remainingCooldownDelayTicks--;
         } else {
            this.remainingCooldownTicks--;
         }

         return this;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.maxCooldownTicks), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.remainingCooldownTicks), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.remainingCooldownDelayTicks), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.maxCooldownTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         this.remainingCooldownTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         this.remainingCooldownDelayTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.INT.writeNbt(Integer.valueOf(this.maxCooldownTicks)).ifPresent(tag -> nbt.put("maxCooldownTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.remainingCooldownTicks)).ifPresent(tag -> nbt.put("remainingCooldownTicks", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.remainingCooldownDelayTicks)).ifPresent(tag -> nbt.put("remainingCooldownDelayTicks", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.maxCooldownTicks = Adapters.INT.readNbt(nbt.get("maxCooldownTicks")).orElse(0);
         this.remainingCooldownTicks = Adapters.INT.readNbt(nbt.get("remainingCooldownTicks")).orElse(0);
         this.remainingCooldownDelayTicks = Adapters.INT.readNbt(nbt.get("remainingCooldownDelayTicks")).orElse(0);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.INT.writeJson(Integer.valueOf(this.maxCooldownTicks)).ifPresent(element -> json.add("maxCooldownTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.remainingCooldownTicks)).ifPresent(element -> json.add("remainingCooldownTicks", element));
         Adapters.INT.writeJson(Integer.valueOf(this.remainingCooldownDelayTicks)).ifPresent(element -> json.add("remainingCooldownDelayTicks", element));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         this.maxCooldownTicks = Adapters.INT.readJson(json.get("maxCooldownTicks")).orElse(0);
         this.remainingCooldownTicks = Adapters.INT.readJson(json.get("remainingCooldownTicks")).orElse(0);
         this.remainingCooldownDelayTicks = Adapters.INT.readJson(json.get("remainingCooldownDelayTicks")).orElse(0);
      }
   }

   public static enum TickResult {
      PASS,
      COOLDOWN;
   }
}
