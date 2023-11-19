package iskallia.vault.entity.entity;

import iskallia.vault.init.ModEntities;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class AncientCopperConduitItemEntity extends ItemEntity {
   public AncientCopperConduitItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
      this(ModEntities.CONDUIT_ITEM, worldIn);
      this.setPos(x, y, z);
      this.setYRot(this.random.nextFloat() * 360.0F);
      this.setDeltaMovement(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
      this.setItem(stack);
   }

   public AncientCopperConduitItemEntity(EntityType<AncientCopperConduitItemEntity> entityEntityType, Level level) {
      super(entityEntityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public void tick() {
      super.tick();
      if (this.level instanceof ServerLevel serverLevel && this.isInWater() && this.level.getLevelData().isRaining()) {
         this.level.getLevelData().setRaining(false);
         this.level.setRainLevel(0.5F);
         serverLevel.getServer()
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F), serverLevel.dimension());
         LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.level);
         if (lightningbolt != null) {
            lightningbolt.moveTo(this.position());
            if (this.getOwner() != null) {
               Entity entity = this.level.getPlayerByUUID(this.getOwner());
               lightningbolt.setCause(entity instanceof ServerPlayer ? (ServerPlayer)entity : null);
            }

            this.level.addFreshEntity(lightningbolt);
            this.level.playSound((Player)null, this.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 1.0F, 1.0F);
         }

         this.remove(RemovalReason.DISCARDED);
      }
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
