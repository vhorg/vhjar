package iskallia.vault.entity.boss.stage;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.Optional;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class MeleeStage<T extends MeleeStageAttributes> implements IBossStage {
   public static final String NAME = "melee";
   protected final ArtifactBossEntity boss;
   protected final T meleeStageAttributes;

   public MeleeStage(ArtifactBossEntity boss, T meleeStageAttributes) {
      this.boss = boss;
      this.meleeStageAttributes = meleeStageAttributes;
   }

   @Override
   public void tick() {
   }

   @Override
   public boolean isFinished() {
      return this.boss.isCloseToDeath();
   }

   @Override
   public boolean makesBossInvulnerable() {
      return false;
   }

   @Override
   public Set<Flag> getControlFlags() {
      return Set.of();
   }

   @Override
   public void init() {
      this.boss.setScaledHealth(this.meleeStageAttributes.getHealth() * this.boss.getPlayerCount());
      this.boss.setScaledDamage(this.meleeStageAttributes.getBaseAttackDamage());
      this.boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.meleeStageAttributes.getBaseSpeed());
   }

   @Override
   public void start() {
   }

   @Override
   public void stop() {
   }

   @Override
   public void finish() {
   }

   @Override
   public String getName() {
      return "melee";
   }

   @Override
   public Optional<AnimationBuilder> getAnimation() {
      return Optional.empty();
   }

   @Override
   public Optional<ResourceLocation> getTextureLocation() {
      return Optional.empty();
   }

   @Override
   public Tuple<Integer, Integer> getBossBarTextureVs() {
      return new Tuple(0, 319);
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return this.meleeStageAttributes.getMeleeAttacks();
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks() {
      return this.meleeStageAttributes.getRageAttacks();
   }

   @Override
   public float getProgress() {
      return this.boss.getHealth() / this.boss.getMaxHealth();
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = IBossStage.super.serialize();
      tag.put("MeleeStageAttributes", this.meleeStageAttributes.serialize());
      return tag;
   }

   public static MeleeStage fromAttributes(ArtifactBossEntity artifactBossEntity, CompoundTag attributesTag) {
      return new MeleeStage<>(artifactBossEntity, MeleeStageAttributes.from(attributesTag));
   }

   public static MeleeStage from(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      return fromAttributes(artifactBossEntity, tag.getCompound("MeleeStageAttributes"));
   }
}
