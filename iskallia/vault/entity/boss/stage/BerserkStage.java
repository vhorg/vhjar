package iskallia.vault.entity.boss.stage;

import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.entity.boss.BloodOrbEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

public class BerserkStage extends MeleeStage<BerserkStageAttributes> {
   private static final ResourceLocation BOSS_TEXTURE = new ResourceLocation("the_vault", "textures/entity/boss/artifact_boss_idona.png");
   private static final TargetingConditions PLAYERS_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(50.0);
   public static final String NAME = "berserk";
   private static final UUID BERSERK_ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("cecf6bd8-612b-11ee-869d-325096b39f47");
   private static final UUID CRIT_CHANCE_MODIFIER_UUID = UUID.fromString("8b0e4506-6e41-4e14-b543-58fc79c8d3a3");
   private static final UUID CRIT_MULTIPLIER_MODIFIER_UUID = UUID.fromString("175a1074-7ef8-4d8a-afc7-e8dd1848259c");
   private static final UUID BERSERK_SPEED_MODIFIER_UUID = UUID.fromString("d4b6c97e-612b-11ee-82a2-325096b39f47");
   private static final int BLEED_EFFECT_APPLICATION_COOLDOWN = 20;
   private static final int BLOOD_ORB_SPAWN_RADIUS = 34;
   private Set<UUID> bloodOrbs = new HashSet<>();
   private int bloodOrbSpawnTimer = 0;
   private int bleedEffectTimer = 0;

   public BerserkStage(ArtifactBossEntity artifactBossEntity, BerserkStageAttributes meleeStageAttributes) {
      super(artifactBossEntity, meleeStageAttributes);
      this.setBloodOrbSpawnCooldown();
   }

   private void setBloodOrbSpawnCooldown() {
      int playerCount = this.boss.getPlayerCount();
      this.bloodOrbSpawnTimer = Math.max(
         20, (int)(this.meleeStageAttributes.getBloodOrbSpawnCooldown() - this.meleeStageAttributes.getBloodOrbSpawnCooldown() * 0.2 * playerCount)
      );
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = super.serialize();
      tag.put("BloodOrbs", this.serializeBloodOrbs());
      return tag;
   }

   private ListTag serializeBloodOrbs() {
      ListTag bloodOrbList = new ListTag();
      this.bloodOrbs.forEach(uuid -> bloodOrbList.add(NbtUtils.createUUID(uuid)));
      return bloodOrbList;
   }

   public static BerserkStage fromAttributes(ArtifactBossEntity artifactBossEntity, CompoundTag attributesTag) {
      return new BerserkStage(artifactBossEntity, BerserkStageAttributes.from(attributesTag));
   }

   public static BerserkStage from(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      BerserkStage berserkStage = fromAttributes(artifactBossEntity, tag.getCompound("MeleeStageAttributes"));
      berserkStage.bloodOrbs = deserializeBloodOrbs(tag.getList("BloodOrbs", 11));
      return berserkStage;
   }

   private static Set<UUID> deserializeBloodOrbs(ListTag bloodOrbs) {
      Set<UUID> bloodOrbSet = new HashSet<>();
      bloodOrbs.forEach(uuidTag -> bloodOrbSet.add(NbtUtils.loadUUID(uuidTag)));
      return bloodOrbSet;
   }

   @Override
   public Optional<ResourceLocation> getTextureLocation() {
      return Optional.of(BOSS_TEXTURE);
   }

   @Override
   public void onHurt() {
      super.onHurt();
      float healthBasedMultiplier = 1.0F - this.boss.getHealth() / this.boss.getMaxHealth();
      double damageMultiplier = healthBasedMultiplier * (this.meleeStageAttributes.getMaxBaseDamageMultiplier() - 1.0);
      this.updateModifier(Attributes.ATTACK_DAMAGE, BERSERK_ATTACK_DAMAGE_MODIFIER_UUID, "BerserkAttackDamageAdjustment", damageMultiplier);
      double speedMultiplier = healthBasedMultiplier * (this.meleeStageAttributes.getMaxSpeedMultiplier() - 1.0);
      this.updateModifier(Attributes.MOVEMENT_SPEED, BERSERK_SPEED_MODIFIER_UUID, "BerserkSpeedAdjustment", speedMultiplier);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.boss.level instanceof ServerLevel serverLevel) {
         this.bloodOrbs.removeIf(uuid -> serverLevel.getEntity(uuid) == null);
         if (this.bloodOrbSpawnTimer > 0) {
            this.bloodOrbSpawnTimer--;
         }

         if (this.bloodOrbSpawnTimer <= 0) {
            if (this.bloodOrbs.size() < this.meleeStageAttributes.getMaxBloodOrbs()) {
               Vec3 spawnPosition = this.boss.getSpawnPosition();
               double x = spawnPosition.x() + this.boss.level.random.nextInt(-34, 34);
               double y = spawnPosition.y();
               double z = spawnPosition.z() + this.boss.level.random.nextInt(-34, 34);
               Vec3 bloodOrbPos = new Vec3(x, y, z);
               BloodOrbEntity bloodOrbEntity = new BloodOrbEntity(this.boss.level);
               bloodOrbEntity.setPos(bloodOrbPos);
               bloodOrbEntity.setNoGravity(false);
               serverLevel.addFreshEntity(bloodOrbEntity);
               this.bloodOrbs.add(bloodOrbEntity.getUUID());
            }

            this.setBloodOrbSpawnCooldown();
         }

         if (this.bleedEffectTimer > 0) {
            this.bleedEffectTimer--;
         }

         if (this.bleedEffectTimer <= 0 && !this.bloodOrbs.isEmpty()) {
            this.boss
               .level
               .getNearbyPlayers(PLAYERS_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(60.0))
               .forEach(player -> player.addEffect(new MobEffectInstance(ModEffects.BLEED, 60, this.bloodOrbs.size() - 1), null));
            this.bleedEffectTimer = 20;
         }
      }
   }

   private void updateModifier(Attribute attribute, UUID modifierUuid, String modifierName, double amount) {
      AttributeInstance attributeInstance = this.boss.getAttribute(attribute);
      if (attributeInstance != null) {
         if (attributeInstance.getModifier(modifierUuid) != null) {
            attributeInstance.removeModifier(modifierUuid);
         }

         attributeInstance.addTransientModifier(new AttributeModifier(modifierUuid, modifierName, amount, Operation.MULTIPLY_BASE));
      }
   }

   @Override
   public void init() {
      super.init();
      this.boss
         .getAttribute(ModAttributes.CRIT_CHANCE)
         .addTransientModifier(new AttributeModifier(CRIT_CHANCE_MODIFIER_UUID, "BerserkCritChance", 1.0, Operation.ADDITION));
      this.boss
         .getAttribute(ModAttributes.CRIT_MULTIPLIER)
         .addTransientModifier(new AttributeModifier(CRIT_MULTIPLIER_MODIFIER_UUID, "BerserkCritMultiplier", 1.5, Operation.ADDITION));
   }

   @Override
   public void finish() {
      super.finish();
      this.removeModifier(ModAttributes.CRIT_CHANCE, CRIT_CHANCE_MODIFIER_UUID);
      this.removeModifier(ModAttributes.CRIT_MULTIPLIER, CRIT_MULTIPLIER_MODIFIER_UUID);
      this.removeModifier(Attributes.ATTACK_DAMAGE, BERSERK_ATTACK_DAMAGE_MODIFIER_UUID);
      this.removeModifier(Attributes.MOVEMENT_SPEED, BERSERK_SPEED_MODIFIER_UUID);
      this.bloodOrbs.forEach(bloodOrb -> {
         if (this.boss.level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(bloodOrb);
            if (entity != null) {
               entity.kill();
            }
         }
      });
   }

   private void removeModifier(Attribute attribute, UUID modifierUUID) {
      AttributeInstance ai = this.boss.getAttribute(attribute);
      if (ai != null) {
         ai.removeModifier(modifierUUID);
      }
   }

   @Override
   public String getName() {
      return "berserk";
   }

   @Override
   public Tuple<Integer, Integer> getBossBarTextureVs() {
      return new Tuple(186, 363);
   }
}
