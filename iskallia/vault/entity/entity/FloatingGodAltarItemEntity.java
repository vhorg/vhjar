package iskallia.vault.entity.entity;

import iskallia.vault.client.particles.FloatingAltarItemParticle;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class FloatingGodAltarItemEntity extends ItemEntity {
   private static final EntityDataAccessor<Integer> COLOR1 = SynchedEntityData.defineId(FloatingGodAltarItemEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR2 = SynchedEntityData.defineId(FloatingGodAltarItemEntity.class, EntityDataSerializers.INT);
   private int color1 = 0;
   private int color2 = 0;

   public FloatingGodAltarItemEntity(EntityType<? extends ItemEntity> type, Level world) {
      super(type, world);
   }

   public FloatingGodAltarItemEntity(Level worldIn, double x, double y, double z) {
      this(ModEntities.FLOATING_ALTAR_ITEM, worldIn);
      this.setPos(x, y, z);
      this.setYRot(this.random.nextFloat() * 360.0F);
      this.setDeltaMovement(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
   }

   public FloatingGodAltarItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
      this.lifespan = Integer.MAX_VALUE;
   }

   public static FloatingGodAltarItemEntity create(Level world, BlockPos pos, ItemStack stack) {
      return new FloatingGodAltarItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(COLOR1, 16777215);
      this.getEntityData().define(COLOR2, 16777215);
   }

   public FloatingGodAltarItemEntity setColor(int color) {
      this.setColor(color, color);
      this.color1 = color;
      this.color2 = color;
      return this;
   }

   public boolean save(CompoundTag pCompound) {
      pCompound.putInt("color1", this.color1);
      pCompound.putInt("color2", this.color2);
      return super.save(pCompound);
   }

   public void load(CompoundTag pCompound) {
      this.setColor(pCompound.getInt("color1"), pCompound.getInt("color2"));
      super.load(pCompound);
   }

   public FloatingGodAltarItemEntity setColor(int color1, int color2) {
      this.entityData.set(COLOR1, color1);
      this.entityData.set(COLOR2, color2);
      this.color1 = color1;
      this.color2 = color2;
      return this;
   }

   public void tick() {
      this.setDeltaMovement(Vec3.ZERO);
      super.tick();
      if (this.isAlive() && this.level.isClientSide()) {
         this.playEffects();
      }
   }

   public void move(MoverType type, Vec3 velocity) {
   }

   public AABB getBoundingBoxForCulling() {
      return super.getBoundingBoxForCulling().inflate(3.0);
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      ParticleEngine mgr = Minecraft.getInstance().particleEngine;
      int color1 = (Integer)this.getEntityData().get(COLOR1);
      int color2 = (Integer)this.getEntityData().get(COLOR2);
      Particle particle = mgr.createParticle(
         (ParticleOptions)ModParticles.FLOATING_ALTAR_ITEM.get(),
         this.position().x(),
         this.position().y() + 0.25,
         this.position().z(),
         0.25,
         this.tickCount % 360,
         0.0
      );
      if (particle instanceof FloatingAltarItemParticle floatingAltarItemParticle) {
         floatingAltarItemParticle.setEntity(this);
         int col = ColorUtil.blendColors(color1, color2, this.random.nextFloat());
         float b = (col & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float r = (col >> 16 & 0xFF) / 255.0F;
         particle.setColor(r, g, b);
      }
   }

   public void playerTouch(Player player) {
      boolean wasAlive = this.isAlive();
      super.playerTouch(player);
      if (wasAlive && !this.isAlive()) {
         player.getCommandSenderWorld()
            .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.6F, 1.0F);
      }
   }

   public boolean isNoGravity() {
      return true;
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
