package iskallia.vault.block.base;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.influence.VaultGod;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FillableAltarBlock<T extends FillableAltarTileEntity> extends FacedBlock implements EntityBlock {
   protected static final Random rand = new Random();
   public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

   public FillableAltarBlock() {
      super(Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().noOcclusion());
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public abstract ParticleOptions getFlameParticle();

   public abstract VaultGod getAssociatedVaultGod();

   public abstract ItemStack getAssociatedVaultGodShard();

   public abstract InteractionResult rightClicked(BlockState var1, ServerLevel var2, BlockPos var3, T var4, ServerPlayer var5, ItemStack var6);

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity tileEntity = world.getBlockEntity(pos);
         ItemStack heldStack = player.getItemInHand(hand);
         if (tileEntity != null) {
            try {
               if (((FillableAltarTileEntity)tileEntity).isCompleted() && !((FillableAltarTileEntity)tileEntity).isConsumed()) {
                  ((FillableAltarTileEntity)tileEntity).placeReplacement(world, pos);
                  ((FillableAltarTileEntity)tileEntity).setConsumed();
                  CommonEvents.ALTAR_PROGRESS
                     .invoke(
                        (ServerLevel)world,
                        (ServerPlayer)player,
                        state,
                        pos,
                        (FillableAltarTileEntity)tileEntity,
                        ((FillableAltarTileEntity)tileEntity).currentProgress,
                        ((FillableAltarTileEntity)tileEntity).maxProgress,
                        true
                     );
                  ((FillableAltarTileEntity)tileEntity).sendUpdates();
                  return InteractionResult.SUCCESS;
               }

               if (!((FillableAltarTileEntity)tileEntity).isCompleted()) {
                  return this.rightClicked(state, (ServerLevel)world, pos, (T)tileEntity, (ServerPlayer)player, heldStack);
               }
            } catch (ClassCastException var10) {
            }
         }

         return InteractionResult.FAIL;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, Level world, BlockPos pos, Random rand) {
      this.addFlameParticle(world, pos, 1.0, 17.0, 15.0);
      this.addFlameParticle(world, pos, 15.0, 17.0, 15.0);
      this.addFlameParticle(world, pos, 15.0, 17.0, 1.0);
      this.addFlameParticle(world, pos, 1.0, 17.0, 1.0);
   }

   @OnlyIn(Dist.CLIENT)
   public void addFlameParticle(Level world, BlockPos pos, double xOffset, double yOffset, double zOffset) {
      double x = pos.getX() + xOffset / 16.0;
      double y = pos.getY() + yOffset / 16.0;
      double z = pos.getZ() + zOffset / 16.0;
      world.addParticle(this.getFlameParticle(), x, y, z, 0.0, 0.0, 0.0);
   }
}
