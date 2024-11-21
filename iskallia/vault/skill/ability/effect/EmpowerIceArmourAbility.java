package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.skill.ability.effect.spi.AbstractEmpowerAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.calc.EffectDurationHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class EmpowerIceArmourAbility extends AbstractEmpowerAbility {
   private int chilledAmplifier;
   private int chilledDuration;
   private float additionalManaPerHit;

   public EmpowerIceArmourAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      int durationTicks,
      float radius,
      int chilledAmplifier,
      int chilledDuration,
      float additionalManaPerHit
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, durationTicks);
      this.chilledAmplifier = chilledAmplifier;
      this.chilledDuration = chilledDuration;
      this.additionalManaPerHit = additionalManaPerHit;
   }

   public EmpowerIceArmourAbility() {
   }

   public int getChilledAmplifier() {
      return this.chilledAmplifier;
   }

   public int getChilledDurationUnmodified() {
      return this.chilledDuration;
   }

   public int getChilledDuration(LivingEntity entity) {
      int duration = this.getChilledDurationUnmodified();
      return EffectDurationHelper.adjustEffectDurationFloor(entity, duration);
   }

   public float getAdditionalManaPerHit() {
      return this.additionalManaPerHit;
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            int amplifier = Mth.clamp(this.getChilledAmplifier() * 100, 0, 100);
            ModEffects.EMPOWER_ICE_ARMOUR.addTo(player, amplifier);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.EMPOWER_ICE_ARMOUR);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.EMPOWER_ICE_ARMOUR)) {
            player.removeEffect(ModEffects.EMPOWER_ICE_ARMOUR);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_ICE_ARMOUR));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_ICE_ARMOUR));
   }

   @SubscribeEvent
   public static void onDamage(LivingAttackEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         abilities.getAll(EmpowerIceArmourAbility.class, Ability::isActive).stream().findFirst().ifPresent(ability -> {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
               attacker.addEffect(new MobEffectInstance(ModEffects.CHILLED, ability.getChilledDuration(player), ability.getChilledAmplifier(), false, false));
               if (Mana.decrease(player, ManaAction.PLAYER_ACTION, ability.getAdditionalManaPerHit()) <= 0.0F) {
                  player.removeEffect(ModEffects.SHELL);
                  ability.putOnCooldown(SkillContext.of(player));
                  ability.setActive(false);
               }
            }
         });
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.chilledAmplifier), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.chilledDuration), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalManaPerHit), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.chilledAmplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.chilledDuration = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.additionalManaPerHit = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.chilledAmplifier)).ifPresent(tag -> nbt.put("chilledAmplifier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.chilledDuration)).ifPresent(tag -> nbt.put("chilledDuration", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalManaPerHit)).ifPresent(tag -> nbt.put("additionalManaPerHit", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.chilledAmplifier = Adapters.INT.readNbt(nbt.get("chilledAmplifier")).orElse(0);
      this.chilledDuration = Adapters.INT.readNbt(nbt.get("chilledDuration")).orElse(0);
      this.additionalManaPerHit = Adapters.FLOAT.readNbt(nbt.get("additionalManaPerHit")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.chilledAmplifier)).ifPresent(element -> json.add("chilledAmplifier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.chilledDuration)).ifPresent(element -> json.add("chilledDuration", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.additionalManaPerHit)).ifPresent(element -> json.add("additionalManaPerHit", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.chilledAmplifier = Adapters.INT.readJson(json.get("chilledAmplifier")).orElse(0);
      this.chilledDuration = Adapters.INT.readJson(json.get("chilledDuration")).orElse(0);
      this.additionalManaPerHit = Adapters.FLOAT.readJson(json.get("additionalManaPerHit")).orElse(0.0F);
   }

   public static class EmpowerIceArmourEffect extends EmpowerAbility.EmpowerEffect {
      public EmpowerIceArmourEffect(int color, ResourceLocation resourceLocation) {
         super(EmpowerIceArmourAbility.EmpowerIceArmourEffect.class, color, resourceLocation);
      }
   }
}
