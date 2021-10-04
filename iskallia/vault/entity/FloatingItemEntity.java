package iskallia.vault.entity;

import iskallia.vault.client.util.ColorizationHelper;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class FloatingItemEntity extends ItemEntity {
   private static final DataParameter<Integer> COLOR1 = EntityDataManager.func_187226_a(FloatingItemEntity.class, DataSerializers.field_187192_b);
   private static final DataParameter<Integer> COLOR2 = EntityDataManager.func_187226_a(FloatingItemEntity.class, DataSerializers.field_187192_b);

   public FloatingItemEntity(EntityType<? extends ItemEntity> type, World world) {
      super(type, world);
      this.func_184211_a("PreventMagnetMovement");
   }

   public FloatingItemEntity(World worldIn, double x, double y, double z) {
      this(ModEntities.FLOATING_ITEM, worldIn);
      this.func_70107_b(x, y, z);
      this.field_70177_z = this.field_70146_Z.nextFloat() * 360.0F;
      this.func_213293_j(this.field_70146_Z.nextDouble() * 0.2 - 0.1, 0.2, this.field_70146_Z.nextDouble() * 0.2 - 0.1);
   }

   public FloatingItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.func_92058_a(stack);
      this.lifespan = Integer.MAX_VALUE;
   }

   public static FloatingItemEntity create(World world, BlockPos pos, ItemStack stack) {
      return new FloatingItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, stack);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(COLOR1, 16777215);
      this.func_184212_Q().func_187214_a(COLOR2, 16777215);
   }

   public void setColor(int color) {
      this.setColor(color, color);
   }

   public void setColor(int color1, int color2) {
      this.field_70180_af.func_187227_b(COLOR1, color1);
      this.field_70180_af.func_187227_b(COLOR2, color2);
   }

   public void func_70071_h_() {
      this.func_213317_d(Vector3d.field_186680_a);
      super.func_70071_h_();
      if (this.func_70089_S() && this.func_130014_f_().func_201670_d()) {
         this.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
      Vector3d thisPos = this.func_213303_ch().func_72441_c(0.0, this.func_213302_cg() / 4.0F, 0.0);
      int color1 = (Integer)this.func_184212_Q().func_187225_a(COLOR1);
      int color2 = (Integer)this.func_184212_Q().func_187225_a(COLOR2);
      if (color1 == 16777215 && color2 == 16777215) {
         color1 = ColorizationHelper.getColor(this.func_92059_d()).map(Color::getRGB).orElse(16777215);
         this.field_70180_af.func_187227_b(COLOR1, color1);
         int r = Math.min((color1 >> 16 & 0xFF) * 2, 255);
         int g = Math.min((color1 >> 8 & 0xFF) * 2, 255);
         int b = Math.min((color1 >> 0 & 0xFF) * 2, 255);
         color2 = r << 16 | g << 8 | b;
         this.field_70180_af.func_187227_b(COLOR2, color2);
      }

      if (this.field_70146_Z.nextInt(3) == 0) {
         Vector3d rPos = thisPos.func_72441_c(
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * this.field_70146_Z.nextFloat() * 8.0F,
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * this.field_70146_Z.nextFloat() * 8.0F,
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * this.field_70146_Z.nextFloat() * 8.0F
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
            ParticleTypes.field_197629_v, rPos.field_72450_a, rPos.field_72448_b, rPos.field_72449_c, 0.0, 0.0, 0.0
         );
         if (p != null) {
            p.field_187149_H = 0.0F;
            p.func_187146_c(MiscUtils.blendColors(color1, color2, this.field_70146_Z.nextFloat()));
         }
      }

      if (this.field_70146_Z.nextBoolean()) {
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
            ParticleTypes.field_197629_v,
            thisPos.field_72450_a,
            thisPos.field_72448_b,
            thisPos.field_72449_c,
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2,
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2,
            (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2
         );
         if (p != null) {
            p.field_187149_H = 0.0F;
            p.func_187146_c(MiscUtils.blendColors(color1, color2, this.field_70146_Z.nextFloat()));
         }
      }
   }

   public void func_70100_b_(PlayerEntity player) {
      boolean wasAlive = this.func_70089_S();
      super.func_70100_b_(player);
      if (wasAlive && !this.func_70089_S()) {
         player.func_130014_f_()
            .func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_193807_ew,
               SoundCategory.PLAYERS,
               0.6F,
               1.0F
            );
      }
   }

   public boolean func_189652_ae() {
      return true;
   }

   public IPacket<?> func_213297_N() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
