package iskallia.vault.fluid.block;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.fluid.VoidFluid;
import iskallia.vault.init.ModEffects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VoidFluidBlock extends LiquidBlock {
   public VoidFluidBlock(Supplier<? extends VoidFluid> supplier, Properties properties) {
      super(supplier, properties);
   }

   public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
      super.entityInside(state, world, pos, entity);
      entity.clearFire();
      if (!world.isClientSide && entity instanceof Player) {
         ServerPlayer player = (ServerPlayer)entity;
         affectPlayer(player);
      } else if (entity instanceof ItemEntity itemEntity && state.getFluidState().isSource()) {
         ItemStack itemStack = itemEntity.getItem();
         if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof VaultOreBlock) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            transformOre(itemEntity, (VaultOreBlock)blockItem.getBlock());
         }
      }
   }

   public static void affectPlayer(ServerPlayer player) {
      if (!player.hasEffect(ModEffects.TIMER_ACCELERATION) || player.getEffect(ModEffects.TIMER_ACCELERATION).getDuration() < 40) {
         int duration = 100;
         int amplifier = 1;
         MobEffectInstance acceleration = new MobEffectInstance(ModEffects.TIMER_ACCELERATION, duration, amplifier);
         MobEffectInstance blindness = new MobEffectInstance(MobEffects.BLINDNESS, duration, amplifier);
         player.addEffect(acceleration);
         player.addEffect(blindness);
      }
   }

   public static void transformOre(ItemEntity itemEntity, VaultOreBlock oreBlock) {
      Level world = itemEntity.level;
      BlockPos pos = itemEntity.blockPosition();
      ItemStack itemStack = itemEntity.getItem();
      itemStack.shrink(1);
      if (itemStack.getCount() <= 0) {
         itemEntity.discard();
      }

      if (!world.isClientSide) {
         ServerLevel serverWorld = (ServerLevel)world;
         serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.MASTER, 1.0F, (float)Math.random());
         serverWorld.sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 100, 0.0, 0.0, 0.0, Math.PI);
      }

      ItemEntity gemEntity = createGemEntity(world, oreBlock, pos);
      world.addFreshEntity(gemEntity);
   }

   @Nonnull
   private static ItemEntity createGemEntity(Level world, VaultOreBlock oreBlock, BlockPos pos) {
      double x = pos.getX() + 0.5F;
      double y = pos.getY() + 0.5F;
      double z = pos.getZ() + 0.5F;
      ItemStack itemStack = new ItemStack(oreBlock.getAssociatedGem(), 2);
      ItemEntity itemEntity = new ItemEntity(world, x, y, z, itemStack);
      itemEntity.setPickUpDelay(40);
      float mag = world.random.nextFloat() * 0.2F;
      float angle = world.random.nextFloat() * (float) (Math.PI * 2);
      itemEntity.setDeltaMovement(-Mth.sin(angle) * mag, 0.2F, Mth.cos(angle) * mag);
      return itemEntity;
   }
}
