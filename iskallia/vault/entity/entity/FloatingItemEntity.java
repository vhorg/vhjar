package iskallia.vault.entity.entity;

import iskallia.vault.client.util.ColorizationHelper;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.init.ModEntities;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class FloatingItemEntity extends ItemEntity {
   private static final EntityDataAccessor<Integer> COLOR1 = SynchedEntityData.defineId(FloatingItemEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR2 = SynchedEntityData.defineId(FloatingItemEntity.class, EntityDataSerializers.INT);

   public FloatingItemEntity(EntityType<? extends ItemEntity> type, Level world) {
      super(type, world);
      this.addTag("PreventMagnetMovement");
   }

   public FloatingItemEntity(Level worldIn, double x, double y, double z) {
      this(ModEntities.FLOATING_ITEM, worldIn);
      this.setPos(x, y, z);
      this.setYRot(this.random.nextFloat() * 360.0F);
      this.setDeltaMovement(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
   }

   public FloatingItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
      this.lifespan = Integer.MAX_VALUE;
   }

   public static FloatingItemEntity create(Level world, BlockPos pos, ItemStack stack) {
      return new FloatingItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(COLOR1, 16777215);
      this.getEntityData().define(COLOR2, 16777215);
   }

   public FloatingItemEntity setColor(int color) {
      this.setColor(color, color);
      return this;
   }

   public FloatingItemEntity setColor(int color1, int color2) {
      this.entityData.set(COLOR1, color1);
      this.entityData.set(COLOR2, color2);
      return this;
   }

   public void tick() {
      this.setDeltaMovement(Vec3.ZERO);
      super.tick();
      if (this.isAlive() && this.getCommandSenderWorld().isClientSide()) {
         this.playEffects();
      }
   }

   public void move(MoverType type, Vec3 velocity) {
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      ParticleEngine mgr = Minecraft.getInstance().particleEngine;
      Vec3 thisPos = this.position().add(0.0, this.getBbHeight() / 4.0F, 0.0);
      int color1 = (Integer)this.getEntityData().get(COLOR1);
      int color2 = (Integer)this.getEntityData().get(COLOR2);
      if (color1 == 16777215 && color2 == 16777215) {
         Optional<Color> override = ColorizationHelper.getCustomColorOverride(this.getItem());
         if (override.isPresent()) {
            color1 = override.get().getRGB();
            color2 = color1;
         } else {
            color1 = ColorizationHelper.getColor(this.getItem()).map(Color::getRGB).orElse(16777215);
            this.entityData.set(COLOR1, color1);
            int r = Math.min((color1 >> 16 & 0xFF) * 2, 255);
            int g = Math.min((color1 >> 8 & 0xFF) * 2, 255);
            int b = Math.min((color1 >> 0 & 0xFF) * 2, 255);
            color2 = r << 16 | g << 8 | b;
            this.entityData.set(COLOR2, color2);
         }
      }

      if (this.random.nextInt(3) == 0) {
         Vec3 rPos = thisPos.add(
            (this.random.nextFloat() - this.random.nextFloat()) * this.random.nextFloat() * 8.0F,
            (this.random.nextFloat() - this.random.nextFloat()) * this.random.nextFloat() * 8.0F,
            (this.random.nextFloat() - this.random.nextFloat()) * this.random.nextFloat() * 8.0F
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, rPos.x, rPos.y, rPos.z, 0.0, 0.0, 0.0);
         if (p != null) {
            p.setColor(ColorUtil.blendColors(color1, color2, this.random.nextFloat()));
         }
      }

      if (this.random.nextBoolean()) {
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(
            ParticleTypes.FIREWORK,
            thisPos.x,
            thisPos.y,
            thisPos.z,
            (this.random.nextFloat() - this.random.nextFloat()) * 0.2,
            (this.random.nextFloat() - this.random.nextFloat()) * 0.2,
            (this.random.nextFloat() - this.random.nextFloat()) * 0.2
         );
         if (p != null) {
            p.setColor(ColorUtil.blendColors(color1, color2, this.random.nextFloat()));
         }
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
