package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.NovaParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractStonefallAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class StonefallSnowAbility extends AbstractStonefallAbility {
   private float damageMultiplier;

   public StonefallSnowAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int durationTicks,
      float knockbackMultiplier,
      float radius,
      float damageReduction,
      float damageMultiplier
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, durationTicks, knockbackMultiplier, radius, damageReduction);
      this.damageMultiplier = damageMultiplier;
   }

   public StonefallSnowAbility() {
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         player.clearFire();
         player.addEffect(new MobEffectInstance(ModEffects.STONEFALL_SHOCKWAVE, this.getDurationTicks(), 0, false, false, true));
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(player -> !player.hasEffect(ModEffects.STONEFALL_SHOCKWAVE) && super.canDoAction(context))
         .orElse(false);
   }

   @SubscribeEvent
   public static void on(LivingFallEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MobEffectInstance effectInstance = player.getEffect(ModEffects.STONEFALL_SHOCKWAVE);
         if (effectInstance != null) {
            AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);
            float dist = event.getDistance();
            float damageMultiplier = event.getDamageMultiplier();
            Level level = player.level;
            if (!(dist < 3.0F)) {
               for (StonefallSnowAbility ability : abilities.getAll(StonefallSnowAbility.class, Skill::isUnlocked)) {
                  float radius = ability.getRadius() + Mth.clamp(dist / 3.75F, 0.0F, 8.0F);
                  List<LivingEntity> nearby = EntityHelper.getNearby(level, player.blockPosition(), radius, LivingEntity.class);
                  nearby.removeIf(mob -> mob instanceof EternalEntity || mob instanceof ServerPlayer);
                  float damage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE) * ability.getDamageMultiplier();
                  nearby.forEach(mob -> ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                     EntityHelper.knockbackWithStrength(mob, player, 0.2F);
                     mob.hurt(DamageSource.playerAttack(player), damage);
                  }));
                  event.setDamageMultiplier(Mth.clamp(1.0F - ability.getDamageReduction(), 0.0F, 1.0F));
                  ModNetwork.CHANNEL
                     .send(PacketDistributor.ALL.noArg(), new NovaParticleMessage(new Vec3(player.getX(), player.getY() + 0.15F, player.getZ()), radius));
                  player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
                  player.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
               }
            }
         }
      }
   }

   private static int calculateFallDamage(LivingEntity fallingEntity, float pDistance, float pDamageMultiplier) {
      MobEffectInstance mobeffectinstance = fallingEntity.getEffect(MobEffects.JUMP);
      float f = mobeffectinstance == null ? 0.0F : mobeffectinstance.getAmplifier() + 1;
      return Mth.ceil((pDistance - 3.0F - f) * pDamageMultiplier);
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   protected void doSound(SkillContext context) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageMultiplier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageMultiplier = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageMultiplier)).ifPresent(tag -> nbt.put("damageMultiplier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageMultiplier = Adapters.FLOAT.readNbt(nbt.get("damageMultiplier")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageMultiplier)).ifPresent(element -> json.add("damageMultiplier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageMultiplier = Adapters.FLOAT.readJson(json.get("damageMultiplier")).orElse(0.0F);
   }

   public static class StonefallShockwaveEffect extends MobEffect {
      public StonefallShockwaveEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
