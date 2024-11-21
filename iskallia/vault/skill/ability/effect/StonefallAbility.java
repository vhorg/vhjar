package iskallia.vault.skill.ability.effect;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StonefallParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractStonefallAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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
public class StonefallAbility extends AbstractStonefallAbility {
   public StonefallAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      int durationTicks,
      float knockbackMultiplier,
      float radius,
      float damageReduction
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, durationTicks, knockbackMultiplier, radius, damageReduction);
   }

   public StonefallAbility() {
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> !player.hasEffect(ModEffects.STONEFALL) && super.canDoAction(context)).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         player.addEffect(new MobEffectInstance(ModEffects.STONEFALL, this.getDurationTicks(player), 0, false, false, true));
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @SubscribeEvent
   public static void on(LivingFallEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MobEffectInstance effectInstance = player.getEffect(ModEffects.STONEFALL);
         if (effectInstance != null) {
            float dist = event.getDistance();
            Level level = player.level;
            if (!(dist < 3.0F)) {
               AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);

               for (StonefallAbility ability : abilities.getAll(StonefallAbility.class, Skill::isUnlocked)) {
                  float radius = ability.getRadius(player) + Mth.clamp(dist / 3.75F, 0.0F, 8.0F);
                  List<LivingEntity> nearby = EntityHelper.getNearby(level, player.blockPosition(), radius, LivingEntity.class);
                  nearby.removeIf(mob -> mob instanceof EternalEntity || mob instanceof ServerPlayer);
                  nearby.forEach(mob -> {
                     float distTo = mob.distanceTo(player);
                     float knockbackStrength = Mth.clamp(dist / (12.0F + distTo), 0.75F, 2.5F) * ability.getKnockbackMultiplier();
                     EntityHelper.knockbackWithStrength(mob, player, knockbackStrength);
                  });
                  event.setDamageMultiplier(Mth.clamp(1.0F - ability.getDamageReduction(), 0.0F, 1.0F));
                  ModNetwork.CHANNEL
                     .send(PacketDistributor.ALL.noArg(), new StonefallParticleMessage(new Vec3(player.getX(), player.getY() + 0.15F, player.getZ()), radius));
                  player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.1F, 0.75F);
                  player.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.1F, 0.75F);
               }
            }
         }
      }
   }

   public static class StonefallEffect extends MobEffect {
      public StonefallEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
