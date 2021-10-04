package iskallia.vault.fluid.block;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.fluid.VoidFluid;
import iskallia.vault.init.ModEffects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VoidFluidBlock extends FlowingFluidBlock {
   public VoidFluidBlock(Supplier<? extends VoidFluid> supplier, Properties properties) {
      super(supplier, properties);
   }

   public void func_196262_a(BlockState state, World world, BlockPos pos, Entity entity) {
      super.func_196262_a(state, world, pos, entity);
      entity.func_70066_B();
      if (!world.field_72995_K && entity instanceof PlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         affectPlayer(player);
      } else if (entity instanceof ItemEntity && state.func_204520_s().func_206889_d()) {
         ItemEntity itemEntity = (ItemEntity)entity;
         ItemStack itemStack = itemEntity.func_92059_d();
         Item item = itemStack.func_77973_b();
         if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            if (blockItem.func_179223_d() instanceof VaultOreBlock) {
               world.func_180501_a(pos, Blocks.field_150350_a.func_176223_P(), 3);
               transformOre(itemEntity, (VaultOreBlock)blockItem.func_179223_d());
            }
         }
      }
   }

   public static void affectPlayer(ServerPlayerEntity player) {
      if (!player.func_70644_a(ModEffects.TIMER_ACCELERATION) || player.func_70660_b(ModEffects.TIMER_ACCELERATION).func_76459_b() < 40) {
         int duration = 100;
         int amplifier = 1;
         EffectInstance acceleration = new EffectInstance(ModEffects.TIMER_ACCELERATION, duration, amplifier);
         EffectInstance blindness = new EffectInstance(Effects.field_76440_q, duration, amplifier);
         player.func_195064_c(acceleration);
         player.func_195064_c(blindness);
      }
   }

   public static void transformOre(ItemEntity itemEntity, VaultOreBlock oreBlock) {
      World world = itemEntity.field_70170_p;
      BlockPos pos = itemEntity.func_233580_cy_();
      ItemStack itemStack = itemEntity.func_92059_d();
      itemStack.func_190918_g(1);
      if (itemStack.func_190916_E() <= 0) {
         itemEntity.func_70106_y();
      }

      if (!world.field_72995_K) {
         ServerWorld serverWorld = (ServerWorld)world;
         serverWorld.func_184148_a(
            null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), SoundEvents.field_187629_cO, SoundCategory.MASTER, 1.0F, (float)Math.random()
         );
         serverWorld.func_195598_a(
            ParticleTypes.field_197607_R, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, 100, 0.0, 0.0, 0.0, Math.PI
         );
      }

      ItemEntity gemEntity = createGemEntity(world, oreBlock, pos);
      world.func_217376_c(gemEntity);
   }

   @Nonnull
   private static ItemEntity createGemEntity(World world, VaultOreBlock oreBlock, BlockPos pos) {
      double x = pos.func_177958_n() + 0.5F;
      double y = pos.func_177956_o() + 0.5F;
      double z = pos.func_177952_p() + 0.5F;
      ItemStack itemStack = new ItemStack(oreBlock.getAssociatedGem(), 2);
      ItemEntity itemEntity = new ItemEntity(world, x, y, z, itemStack);
      itemEntity.func_174867_a(40);
      float mag = world.field_73012_v.nextFloat() * 0.2F;
      float angle = world.field_73012_v.nextFloat() * (float) (Math.PI * 2);
      itemEntity.func_213293_j(-MathHelper.func_76126_a(angle) * mag, 0.2F, MathHelper.func_76134_b(angle) * mag);
      return itemEntity;
   }
}
