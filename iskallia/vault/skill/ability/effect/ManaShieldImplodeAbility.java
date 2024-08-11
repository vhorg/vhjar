package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.ability.special.ManaShieldImplodeRadiusModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.ManaPlayer;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ManaShieldImplodeAbility extends InstantManaAbility {
   private float radius;
   private float percentManaDealt;

   public ManaShieldImplodeAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float radius, float percentManaDealt
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.radius = radius;
      this.percentManaDealt = percentManaDealt;
   }

   public ManaShieldImplodeAbility() {
   }

   public float getPercentManaDealt() {
      return this.percentManaDealt;
   }

   @Override
   public String getAbilityGroupName() {
      return "Mana Shield";
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               Vec3 pos = context.getSource().getPos().orElse(player.position());
               ManaPlayer manaPlayer = context.getSource().getMana().orElse(null);
               List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos);
               DamageSource damageSource = DamageSource.playerAttack(player);
               if (manaPlayer != null) {
                  float mana = manaPlayer.getMana();

                  for (LivingEntity entity : targetEntities) {
                     if (!entity.isInvulnerableTo(damageSource)) {
                        float damageModifier = this.getDamageModifier(this.getRadius(player), player.distanceTo(entity));
                        ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> entity.hurt(damageSource, mana * this.getPercentManaDealt() * damageModifier));
                     }
                  }

                  player.getLevel()
                     .sendParticles(
                        new SphericalParticleOptions(
                           (ParticleType<SphericalParticleOptions>)ModParticles.IMPLODE.get(), this.getRadius(player), new Vector3f(0.0F, 1.0F, 1.0F)
                        ),
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        400,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                     );
                  player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.2F);
                  player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.2F);
                  if (!player.isCreative()) {
                     manaPlayer.decreaseMana(mana);
                  }
               }

               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   protected float getDamageModifier(float radius, float dist) {
      if (dist >= 0.0F && dist < radius / 5.0F * 1.0F) {
         return 1.0F;
      } else if (dist >= radius / 5.0F * 1.0F && dist < radius / 5.0F * 2.0F) {
         return 0.8F;
      } else if (dist >= radius / 5.0F * 2.0F && dist < radius / 5.0F * 3.0F) {
         return 0.6F;
      } else if (dist >= radius / 5.0F * 3.0F && dist < radius / 5.0F * 4.0F) {
         return 0.4F;
      } else {
         return dist >= radius / 5.0F * 4.0F ? 0.2F : 0.2F;
      }
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Entity attacker) {
      float realRadius = this.getUnmodifiedRadius();
      if (attacker instanceof Player player) {
         for (ConfiguredModification<FloatValueConfig, ManaShieldImplodeRadiusModification> mod : SpecialAbilityModification.getModifications(
            player, ManaShieldImplodeRadiusModification.class
         )) {
            realRadius = mod.modification().adjustRadius(mod.config(), realRadius);
         }
      }

      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, realRadius);
      }

      return realRadius;
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos) {
      float radius = this.getRadius(attacker);
      return world.getNearbyEntities(
         LivingEntity.class,
         TargetingConditions.forCombat().range(radius).selector(entity -> !(entity instanceof Player) && !(entity instanceof EternalEntity)),
         attacker,
         AABBHelper.create(pos, radius)
      );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.radius), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.percentManaDealt), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
      this.percentManaDealt = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.percentManaDealt)).ifPresent(tag -> nbt.put("percentManaDealt", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
      this.percentManaDealt = Adapters.FLOAT.readNbt(nbt.get("percentManaDealt")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         Adapters.FLOAT.writeJson(Float.valueOf(this.percentManaDealt)).ifPresent(element -> json.add("percentManaDealt", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
      this.percentManaDealt = Adapters.FLOAT.readJson(json.get("percentManaDealt")).orElse(0.0F);
   }
}
