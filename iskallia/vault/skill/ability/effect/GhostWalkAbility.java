package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.Targeting;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class GhostWalkAbility extends InstantManaAbility {
   private int durationTicks;

   public GhostWalkAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int durationTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.durationTicks = durationTicks;
   }

   public GhostWalkAbility() {
   }

   protected MobEffect getEffect() {
      return ModEffects.GHOST_WALK;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(this.getEffect())) {
            return Ability.ActionResult.fail();
         } else {
            int duration = this.getDurationTicks();
            MobEffectInstance newEffect = new MobEffectInstance(this.getEffect(), duration, 0, false, false, true);
            player.addEffect(newEffect);
            return Ability.ActionResult.successCooldownDeferred();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.GHOST_WALK_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.GHOST_WALK_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      });
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDamage(LivingDamageEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

         for (GhostWalkAbility ability : abilities.getAll(GhostWalkAbility.class, Skill::isUnlocked)) {
            if (ability.doRemoveWhenDealingDamage() && player.hasEffect(ability.getEffect())) {
               player.removeEffect(ability.getEffect());
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onHurt(LivingHurtEvent event) {
      if (isInvulnerable(event.getEntityLiving(), event.getSource())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onAttack(LivingAttackEvent event) {
      if (isInvulnerable(event.getEntityLiving(), event.getSource())) {
         event.setCanceled(true);
      }
   }

   public static boolean isInvulnerable(@Nullable LivingEntity entity, @Nullable DamageSource source) {
      if (entity instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

         for (GhostWalkAbility ability : abilities.getAll(GhostWalkAbility.class, Skill::isUnlocked)) {
            if (player.hasEffect(ability.getEffect()) && ability.preventsDamage() && (source == null || !source.isBypassInvul())) {
               return true;
            }
         }
      }

      return false;
   }

   protected boolean preventsDamage() {
      return true;
   }

   protected boolean doRemoveWhenDealingDamage() {
      return true;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
   }

   static {
      Targeting.addIgnoredTargetOverride((attacker, target) -> target instanceof LivingEntity livingEntity && isInvulnerable(livingEntity, null));
   }

   public static class GhostWalkEffect extends MobEffect {
      public GhostWalkEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
         super(typeIn, liquidColorIn);
         this.setRegistryName(id);
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            PlayerAbilitiesData.setAbilityOnCooldown(player, GhostWalkAbility.class);
         }

         super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
      }
   }
}
