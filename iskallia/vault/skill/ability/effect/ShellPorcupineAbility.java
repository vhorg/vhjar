package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ShellPorcupineAbility extends AbstractShellAbility {
   private float additionalDurabilityWearReduction;
   private float additionalThornsDamagePercent;
   private float additionalManaPerHit;

   public ShellPorcupineAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float additionalDurabilityWearReduction,
      float additionalThornsDamagePercent,
      float additionalManaPerHit
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
      this.additionalDurabilityWearReduction = additionalDurabilityWearReduction;
      this.additionalThornsDamagePercent = additionalThornsDamagePercent;
      this.additionalManaPerHit = additionalManaPerHit;
   }

   public ShellPorcupineAbility() {
   }

   public float getAdditionalDurabilityWearReduction() {
      return this.additionalDurabilityWearReduction;
   }

   public float getAdditionalThornsDamagePercent() {
      return this.additionalThornsDamagePercent;
   }

   public float getAdditionalManaPerHit() {
      return this.additionalManaPerHit;
   }

   private static List<ShellPorcupineAbility> getAll(LivingEntity livingEntity) {
      List<ShellPorcupineAbility> result = new ArrayList<>();
      if (livingEntity instanceof ServerPlayer player
         && !player.getActiveEffects().stream().noneMatch(e -> e.getEffect() instanceof ShellPorcupineAbility.ShellPorcupineEffect)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         return abilities.getAll(ShellPorcupineAbility.class, Skill::isUnlocked);
      } else {
         return result;
      }
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.SHELL_PORCUPINE.addTo(player, 0);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.SHELL_PORCUPINE);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SHELL_PORCUPINE, SoundSource.MASTER, 0.7F, 1.0F);
            player.playNotifySound(ModSounds.SHELL_PORCUPINE, SoundSource.MASTER, 0.7F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.SHELL_PORCUPINE)) {
            player.removeEffect(ModEffects.SHELL_PORCUPINE);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL_PORCUPINE));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.SHELL_PORCUPINE));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalDurabilityWearReduction), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalThornsDamagePercent), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.additionalManaPerHit), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.additionalThornsDamagePercent = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.additionalManaPerHit = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalDurabilityWearReduction)).ifPresent(tag -> nbt.put("additionalDurabilityWearReduction", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalThornsDamagePercent)).ifPresent(tag -> nbt.put("additionalThornsDamagePercent", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.additionalManaPerHit)).ifPresent(tag -> nbt.put("additionalManaPerHit", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readNbt(nbt.get("additionalDurabilityWearReduction")).orElse(0.0F);
      this.additionalThornsDamagePercent = Adapters.FLOAT.readNbt(nbt.get("additionalThornsDamagePercent")).orElse(0.0F);
      this.additionalManaPerHit = Adapters.FLOAT.readNbt(nbt.get("additionalManaPerHit")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson()
         .map(
            json -> {
               Adapters.FLOAT
                  .writeJson(Float.valueOf(this.additionalDurabilityWearReduction))
                  .ifPresent(element -> json.add("additionalDurabilityWearReduction", element));
               Adapters.FLOAT
                  .writeJson(Float.valueOf(this.additionalThornsDamagePercent))
                  .ifPresent(element -> json.add("additionalThornsDamagePercent", element));
               Adapters.FLOAT.writeJson(Float.valueOf(this.additionalManaPerHit)).ifPresent(element -> json.add("additionalManaPerHit", element));
               return (JsonObject)json;
            }
         );
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalDurabilityWearReduction = Adapters.FLOAT.readJson(json.get("additionalDurabilityWearReduction")).orElse(0.0F);
      this.additionalThornsDamagePercent = Adapters.FLOAT.readJson(json.get("additionalThornsDamagePercent")).orElse(0.0F);
      this.additionalManaPerHit = Adapters.FLOAT.readJson(json.get("additionalManaPerHit")).orElse(0.0F);
   }

   static {
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.DURABILITY_WEAR_REDUCTION)
         .register(
            ShellPorcupineAbility.class,
            data -> getAll(data.getEntity()).forEach(skill -> data.setValue(data.getValue() + skill.getAdditionalDurabilityWearReduction()))
         );
      CommonEvents.PLAYER_STAT.of(PlayerStat.THORNS_DAMAGE_FLAT).register(ShellPorcupineAbility.class, data -> getAll(data.getEntity()).forEach(skill -> {
         data.setValue(data.getValue() + data.getValue() * skill.getAdditionalThornsDamagePercent());
         if (data.getEntity() instanceof ServerPlayer player && Mana.decrease(player, skill.getAdditionalManaPerHit()) <= 0.0F) {
            player.removeEffect(ModEffects.SHELL_PORCUPINE);
            skill.putOnCooldown(SkillContext.of(player));
            skill.setActive(false);
         }
      }));
   }

   public static class ShellPorcupineEffect extends ToggleAbilityEffect {
      public ShellPorcupineEffect(int color, ResourceLocation resourceLocation) {
         this(ShellPorcupineAbility.class, color, resourceLocation);
      }

      public ShellPorcupineEffect(Class<?> type, int color, ResourceLocation resourceLocation) {
         super(type, color, resourceLocation);
         this.addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE, Mth.createInsecureUUID(new Random(resourceLocation.hashCode())).toString(), 0.01, Operation.ADDITION
         );
      }
   }
}
