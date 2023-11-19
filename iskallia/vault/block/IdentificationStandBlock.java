package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.IdentificationStandTileEntity;
import iskallia.vault.block.entity.base.BookAnimatingTileEntity;
import iskallia.vault.client.gui.helper.ConfettiParticles;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.InventoryUtil;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IdentificationStandBlock extends FacedBlock implements EntityBlock {
   public IdentificationStandBlock() {
      super(Properties.of(Material.STONE).strength(1.5F, 1200.0F).noOcclusion().lightLevel(value -> 9));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return super.getShape(pState, pLevel, pPos, pContext);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if (level.getBlockEntity(blockPos) instanceof IdentificationStandTileEntity tileEntity && tileEntity.open == 1.0F && level.isClientSide) {
         this.spawnAnimationParticles(blockPos, random);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void spawnAnimationParticles(BlockPos blockPos, Random random) {
      Minecraft minecraft = Minecraft.getInstance();
      double x = blockPos.getX() + 0.5F;
      double y = blockPos.getY() + 1.25F;
      double z = blockPos.getZ() + 0.5F;
      if (random.nextInt(2) == 0) {
         for (int i = 0; i < 10; i++) {
            Particle particle = minecraft.particleEngine
               .createParticle(ParticleTypes.PORTAL, x, y, z, 1.5F * (random.nextFloat() - 0.5F), 0.4F * random.nextFloat(), 1.5F * (random.nextFloat() - 0.5F));
            if (particle != null) {
               int color = random.nextBoolean() ? 16766976 : 2306897;
               particle.setLifetime(40);
               particle.setColor((color >>> 16 & 0xFF) / 255.0F, (color >>> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void spawnIdentificationParticles(BlockPos blockPos, Random random) {
      Minecraft minecraft = Minecraft.getInstance();
      double x = blockPos.getX() + 0.5F;
      double y = blockPos.getY() + 1.25F;
      double z = blockPos.getZ() + 0.5F;

      for (int i = 0; i < 50; i++) {
         Particle particle = minecraft.particleEngine
            .createParticle(
               ParticleTypes.FIREWORK, x, y, z, 1.05F * (random.nextFloat() - 0.5F), 0.4F * random.nextFloat(), 1.05F * (random.nextFloat() - 0.5F)
            );
         if (particle != null) {
            int color = ConfettiParticles.PARTICLE_COLORS[random.nextInt(ConfettiParticles.PARTICLE_COLORS.length)];
            particle.setLifetime(35);
            particle.setColor((color >>> 16 & 0xFF) / 255.0F, (color >>> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
         }
      }
   }

   @Nonnull
   public InteractionResult use(
      @Nonnull BlockState blockState, Level level, @Nonnull BlockPos pos, @Nonnull Player player, InteractionHand hand, BlockHitResult hit
   ) {
      boolean identified = false;

      for (ItemStack itemStack : player.getInventory().items) {
         Item identifiableItem = itemStack.getItem();
         if (identifiableItem instanceof IdentifiableItem) {
            IdentifiableItem identifiableItemx = (IdentifiableItem)identifiableItem;
            VaultGearState state = identifiableItemx.getState(itemStack);
            if (state == VaultGearState.UNIDENTIFIED) {
               if (player instanceof ServerPlayer serverPlayer) {
                  identifiableItemx.instantIdentify(serverPlayer, itemStack);
               }

               identified = true;
            }
         }
      }

      for (InventoryUtil.ItemAccess itemAccess : InventoryUtil.findAllItemsInMainHand(player)) {
         ItemStack itemStackx = itemAccess.getStack();
         Item var19 = itemStackx.getItem();
         if (var19 instanceof IdentifiableItem) {
            IdentifiableItem identifiableItem = (IdentifiableItem)var19;
            VaultGearState state = identifiableItem.getState(itemStackx);
            if (state == VaultGearState.UNIDENTIFIED) {
               if (player instanceof ServerPlayer serverPlayer) {
                  identifiableItem.instantIdentify(serverPlayer, itemStackx);
                  itemAccess.setStack(itemStackx);
               }

               identified = true;
            }
         }
      }

      if (identified) {
         level.playSound(null, pos, ModSounds.IDENTIFICATION_SFX, SoundSource.BLOCKS, 0.5F, 1.0F);
         if (level.isClientSide) {
            this.spawnIdentificationParticles(pos, level.random);
         }

         return InteractionResult.SUCCESS;
      } else {
         level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.IDENTIFICATION_STAND_TILE_ENTITY.create(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> tileEntityType) {
      return level.isClientSide
         ? BlockHelper.getTicker(tileEntityType, ModBlocks.IDENTIFICATION_STAND_TILE_ENTITY, BookAnimatingTileEntity::bookAnimationTick)
         : super.getTicker(level, blockState, tileEntityType);
   }
}
