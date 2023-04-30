package iskallia.vault.skill.ability.effect;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractEmpowerAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class EmpowerAbility extends AbstractEmpowerAbility {
   private static final UUID SPEED_ADDITION_ID = UUID.fromString("8f61de6d-4c50-4b49-a0b4-88dfe2c1050d");
   private float speedPercentAdded;
   private int buffRadius;

   public EmpowerAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond, int durationTicks, float speedPercentAdded
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, durationTicks);
      this.speedPercentAdded = speedPercentAdded;
   }

   public EmpowerAbility() {
   }

   public float getSpeedPercentAdded() {
      return this.speedPercentAdded;
   }

   public float getBuffRadius() {
      return this.buffRadius;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.EMPOWER.addTo(player, 0);
            player.getAttributes().addTransientAttributeModifiers(this.getSpeedAttributeModifier());
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            this.removeEmpower(player);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   private HashMultimap<Attribute, AttributeModifier> getSpeedAttributeModifier() {
      HashMultimap<Attribute, AttributeModifier> attributeMap = HashMultimap.create();
      attributeMap.put(
         Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_ADDITION_ID, "Empower Speed", this.getSpeedPercentAdded(), Operation.MULTIPLY_BASE)
      );
      return attributeMap;
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.EMPOWER, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.EMPOWER, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.EMPOWER)) {
            this.removeEmpower(player);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   public Ability.TickResult doActiveTick(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> player.getLevel()
               .getPlayers(
                  anotherPlayer -> player.distanceTo(anotherPlayer) <= this.buffRadius
                     && !anotherPlayer.getUUID().equals(player.getUUID())
                     && !anotherPlayer.hasEffect(ModEffects.EMPOWER)
               )
               .forEach(anotherPlayer -> {
                  HashMultimap<Attribute, AttributeModifier> speedAttributeModifier = this.getSpeedAttributeModifier();
                  anotherPlayer.getAttributes().addTransientAttributeModifiers(speedAttributeModifier);
                  anotherPlayer.addEffect(new EmpowerAbility.EmpowerCoopEffectInstance(player, this.buffRadius, speedAttributeModifier));
               })
         );
      return super.doActiveTick(context);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::removeEmpower);
   }

   @Override
   public void onRemove(SkillContext context) {
      super.onRemove(context);
      context.getSource().as(ServerPlayer.class).ifPresent(this::removeEmpower);
   }

   private void removeEmpower(ServerPlayer entity) {
      entity.removeEffect(ModEffects.EMPOWER);
      entity.getAttributes().removeAttributeModifiers(this.getSpeedAttributeModifier());
   }

   public static boolean hasEmpowerEffectActive(LivingEntity entity) {
      for (MobEffectInstance instance : entity.getActiveEffects()) {
         if (instance.getEffect() instanceof EmpowerAbility.EmpowerEffect) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.speedPercentAdded), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.buffRadius), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.speedPercentAdded = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.buffRadius = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.speedPercentAdded)).ifPresent(tag -> nbt.put("speedPercentAdded", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.buffRadius)).ifPresent(tag -> nbt.put("buffRadius", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.speedPercentAdded = Adapters.FLOAT.readNbt(nbt.get("speedPercentAdded")).orElse(0.0F);
      this.buffRadius = Adapters.INT.readNbt(nbt.get("buffRadius")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.speedPercentAdded)).ifPresent(element -> json.add("speedPercentAdded", element));
         Adapters.INT.writeJson(Integer.valueOf(this.buffRadius)).ifPresent(element -> json.add("buffRadius", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.speedPercentAdded = Adapters.FLOAT.readJson(json.get("speedPercentAdded")).orElse(0.0F);
      this.buffRadius = Adapters.INT.readJson(json.get("buffRadius")).orElse(0);
   }

   static {
      CommonEvents.PLAYER_STAT.of(PlayerStat.SPEED).filter(data -> data.getEntity().hasEffect(ModEffects.EMPOWER)).register(EmpowerAbility.class, data -> {
         int amplifier = data.getEntity().getEffect(ModEffects.EMPOWER).getAmplifier();
         float speed = (amplifier + 1) / 100.0F;
         data.setValue(data.getValue() + speed);
      });
   }

   public static class EmpowerCoopEffect extends MobEffect {
      public EmpowerCoopEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }

   public static class EmpowerCoopEffectInstance extends MobEffectInstance {
      private static final int CHECK_INTERVAL = 20;
      private static final int OUT_OF_RANGE_DURATION = 100;
      private long nextCheckTime = Long.MIN_VALUE;
      private final WeakReference<Player> empowerAbilityPlayer;
      private final int playerRange;
      private HashMultimap<Attribute, AttributeModifier> speedAttributeModifier;
      private boolean expired = false;

      public EmpowerCoopEffectInstance(Player empowerAbilityPlayer, int playerRange, HashMultimap<Attribute, AttributeModifier> speedAttributeModifier) {
         super(ModEffects.EMPOWER_COOP, 32767, 0, false, false, true);
         this.empowerAbilityPlayer = new WeakReference<>(empowerAbilityPlayer);
         this.playerRange = playerRange;
         this.speedAttributeModifier = speedAttributeModifier;
      }

      public boolean tick(LivingEntity livingEntity, Runnable onExpired) {
         if (!livingEntity.getLevel().isClientSide() && livingEntity.getLevel().getGameTime() >= this.nextCheckTime) {
            this.nextCheckTime = livingEntity.getLevel().getGameTime() + 20L;
            this.checkEffectConditions(livingEntity);
         }

         boolean result = super.tick(livingEntity, onExpired);
         if (this.duration == 0) {
            this.removeSpeedAttribute(livingEntity);
         }

         return result;
      }

      private void checkEffectConditions(LivingEntity livingEntity) {
         if (!this.expired) {
            if (this.empowerAbilityPlayer.get() == null
               || this.empowerAbilityPlayer.get() instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()
               || this.empowerAbilityTurnedOff(this.empowerAbilityPlayer.get())) {
               this.removeSpeedAttribute(livingEntity);
               this.duration = 1;
               this.expired = true;
            } else if (livingEntity.distanceTo((Entity)this.empowerAbilityPlayer.get()) > this.playerRange && this.duration > 100) {
               this.duration = 100;
            } else if (livingEntity.distanceTo((Entity)this.empowerAbilityPlayer.get()) <= this.playerRange && this.duration <= 100) {
               this.duration = 32767;
            }
         }
      }

      private void removeSpeedAttribute(LivingEntity livingEntity) {
         if (livingEntity instanceof Player player) {
            player.getAttributes().removeAttributeModifiers(this.speedAttributeModifier);
         }
      }

      private boolean empowerAbilityTurnedOff(Player player) {
         return player instanceof ServerPlayer serverPlayer
            ? PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(player).getAll(EmpowerAbility.class, Ability::isActive).isEmpty()
            : true;
      }
   }

   public static class EmpowerEffect extends ToggleAbilityEffect {
      protected EmpowerEffect(Class<?> type, int color, ResourceLocation resourceLocation) {
         super(type, color, resourceLocation);
      }

      public EmpowerEffect(int color, ResourceLocation resourceLocation) {
         this(EmpowerAbility.class, color, resourceLocation);
      }
   }
}
