package iskallia.vault.entity.entity.elite;

import iskallia.vault.entity.champion.LeechOnHitAffix;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class EliteZombieEntity extends Zombie {
   private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier("Speed modifier", 0.5, Operation.MULTIPLY_TOTAL);
   private final LeechOnHitAffix leechOnHit = new LeechOnHitAffix("Leeching", 5.0F);
   private static final int MAX_TOTAL_MINIONS = 30;
   private static final int MAX_MINIONS_PER_SPAWN = 7;
   private static final int SPAWN_RADIUS = 10;
   private static final int NUMBER_OF_SPAWN_TRIES = 5;
   private Set<UUID> minionIds = new HashSet<>();

   public EliteZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      float healthBeforeHurt = this.getHealth();
      boolean wasHurt = super.hurt(pSource, pAmount);
      if (wasHurt && this.getHealth() > 0.0F && this.level instanceof ServerLevel serverLevel) {
         int spawnTries = (int)Math.ceil(healthBeforeHurt / this.getMaxHealth() / 0.2) - (int)Math.ceil(this.getHealth() / this.getMaxHealth() / 0.2);

         for (int i = 0; i < spawnTries; i++) {
            this.spawnMinions(serverLevel);
         }
      }

      return wasHurt;
   }

   public void tick() {
      super.tick();
      if (this.level instanceof ServerLevel serverLevel && this.level.getGameTime() % 10L == 0L && !this.isNoAi()) {
         this.checkMinionsAlive(serverLevel);
         this.applySupportEffects();
      }
   }

   private void applySupportEffects() {
      if (!this.minionIds.isEmpty()) {
         this.addEffect(new MobEffectInstance(ModEffects.IMMORTALITY, 20, 0, true, false));
      } else {
         this.removeEffect(ModEffects.IMMORTALITY);
      }

      AttributeInstance movementSpeedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (this.minionIds.isEmpty() && movementSpeedAttribute.hasModifier(SPEED_MODIFIER)) {
         movementSpeedAttribute.removeModifier(SPEED_MODIFIER);
      } else if (!this.minionIds.isEmpty() && !movementSpeedAttribute.hasModifier(SPEED_MODIFIER)) {
         movementSpeedAttribute.addTransientModifier(SPEED_MODIFIER);
      }
   }

   private void spawnMinions(ServerLevel serverLevel) {
      int maxToSpawn = Math.min(30 - this.minionIds.size(), 7);
      int minToSpawn = (int)Math.round(maxToSpawn / 4.0);
      int numberToSpawn = minToSpawn + serverLevel.getRandom().nextInt(maxToSpawn - minToSpawn + 1);

      for (int i = 0; i < numberToSpawn; i++) {
         for (int j = 0; j < 5; j++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            float distance = this.random.nextFloat() * 10.0F;
            double x = this.getX() + Math.cos(angle) * distance;
            double z = this.getZ() + Math.sin(angle) * distance;
            double y = this.getY();

            while (serverLevel.getBlockState(new BlockPos(x, y - 1.0, z)).isAir()) {
               y--;
            }

            Zombie raisedZombie = (Zombie)ModEntities.RAISED_ZOMBIE.create(serverLevel);
            raisedZombie.setBaby(true);
            raisedZombie.setPos(x, y, z);
            if (serverLevel.noCollision(raisedZombie)) {
               raisedZombie.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(raisedZombie.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
               raisedZombie.getTags().add("no_champion");
               serverLevel.addFreshEntity(raisedZombie);
               this.minionIds.add(raisedZombie.getUUID());
               serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 10, 0.5, 0.5, 0.5, 0.0);
               List<Chicken> list = serverLevel.getEntitiesOfClass(
                  Chicken.class, this.getBoundingBox().inflate(2.0, 2.0, 2.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN
               );
               if (!list.isEmpty()) {
                  Chicken chicken = list.get(0);
                  chicken.setChickenJockey(true);
                  raisedZombie.startRiding(chicken);
                  raisedZombie.setPos(chicken.position());
               } else {
                  Chicken chicken = (Chicken)ModEntities.RAISED_ZOMBIE_CHICKEN.create(serverLevel);
                  chicken.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(raisedZombie.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 1.5);
                  chicken.moveTo(raisedZombie.getX(), raisedZombie.getY(), raisedZombie.getZ(), raisedZombie.getYRot(), 0.0F);
                  chicken.finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(raisedZombie.getOnPos()), MobSpawnType.JOCKEY, null, null);
                  chicken.setChickenJockey(true);
                  raisedZombie.startRiding(chicken);
                  serverLevel.addFreshEntity(chicken);
               }
               break;
            }
         }
      }
   }

   private void checkMinionsAlive(ServerLevel serverLevel) {
      this.minionIds.removeIf(minionId -> serverLevel.getEntity(minionId) == null);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      return null;
   }

   @SubscribeEvent
   public static void onEntityAttack(LivingAttackEvent event) {
      if (event.getSource().getEntity() instanceof EliteZombieEntity eliteZombie && event.getEntityLiving() instanceof Player player) {
         eliteZombie.leechOnHit.onChampionHitPlayer(eliteZombie, player, event.getAmount());
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.put("MinionIds", this.minionIds.stream().map(NbtUtils::createUUID).collect(ListTag::new, AbstractList::add, AbstractCollection::addAll));
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.minionIds.clear();
      tag.getList("MinionIds", 11).forEach(nbt -> this.minionIds.add(NbtUtils.loadUUID(nbt)));
   }
}
