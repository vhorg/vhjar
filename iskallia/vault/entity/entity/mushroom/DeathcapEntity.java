package iskallia.vault.entity.entity.mushroom;

import iskallia.vault.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DeathcapEntity extends MushroomEntity {
   protected int smolcapsSpawned = 0;

   public DeathcapEntity(EntityType<? extends Monster> entityType, Level world) {
      super(entityType, world);
   }

   @Override
   public int getTier() {
      return -1;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("SmolcapsSpawned", this.smolcapsSpawned);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("SmolcapsSpawned", 3)) {
         this.smolcapsSpawned = pCompound.getInt("SmolcapsSpawned");
      }
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide() && this.smolcapsSpawned < 15 && this.tickCount % 100 == 0) {
         ServerLevel serverWorld = (ServerLevel)this.level;
         Player nearestPlayer = serverWorld.getNearestPlayer(this, 8.0);
         if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
            SmolcapEntity smolcap = (SmolcapEntity)ModEntities.SMOLCAP.create(this.level);
            if (smolcap != null) {
               Component component = this.getCustomName();
               boolean flag = this.isNoAi();
               if (this.isPersistenceRequired()) {
                  smolcap.setPersistenceRequired();
               }

               smolcap.setCustomName(component);
               smolcap.setNoAi(flag);
               smolcap.setInvulnerable(this.isInvulnerable());
               smolcap.moveTo(this.getEyePosition());
               Vec3 deathcapEyePos = this.getEyePosition();
               Vec3 playerEyePosition = nearestPlayer.getEyePosition();
               float speed = 0.1F;
               Vec3 diff = playerEyePosition.subtract(deathcapEyePos).multiply(speed, speed, speed);
               smolcap.push(diff.x, diff.y + 0.1, diff.z);
               serverWorld.playSound(null, this.getOnPos(), SoundEvents.NETHER_WART_PLANTED, SoundSource.HOSTILE, 1.5F, 1.0F);
               this.level.addFreshEntity(smolcap);
               this.smolcapsSpawned++;
            }
         }
      }
   }
}
