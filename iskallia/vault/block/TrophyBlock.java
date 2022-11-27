package iskallia.vault.block;

import iskallia.vault.block.entity.TrophyTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrophyBlock extends HorizontalDirectionalBlock implements EntityBlock {
   public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public TrophyBlock() {
      super(Properties.of(Material.METAL, MaterialColor.GOLD).strength(5.0F, 3600000.0F));
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.TROPHY_STATUE_TILE_ENTITY.create(pPos, pState);
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!world.isClientSide && stack.hasTag() && world.getBlockEntity(pos) instanceof TrophyTileEntity tileEntity) {
         CompoundTag nbt = stack.getOrCreateTag();
         CompoundTag blockEntityTag = nbt.getCompound("BlockEntityTag");
         WeekKey week = WeekKey.deserialize(blockEntityTag.getCompound("trophyWeek"));
         PlayerVaultStatsData.PlayerRecordEntry recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(blockEntityTag.getCompound("recordEntry"));
         tileEntity.setWeek(week);
         tileEntity.setRecordEntry(recordEntry);
         tileEntity.setChanged();
      }
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      if (!world.isClientSide) {
         BlockEntity tileEntity = world.getBlockEntity(pos);
         ItemStack itemStack = new ItemStack(this);
         if (tileEntity instanceof TrophyTileEntity tile) {
            CompoundTag statueNBT = new CompoundTag();
            tile.saveAdditional(statueNBT);
            CompoundTag stackNBT = new CompoundTag();
            stackNBT.put("BlockEntityTag", statueNBT);
            itemStack.setTag(stackNBT);
         }

         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
      ItemStack itemstack = super.getCloneItemStack(state, target, world, pos, player);
      if (world.getBlockEntity(pos) instanceof TrophyTileEntity tile) {
         CompoundTag compoundTag = new CompoundTag();
         tile.saveAdditional(compoundTag);
         if (!compoundTag.isEmpty()) {
            itemstack.addTagElement("BlockEntityTag", compoundTag);
         }
      }

      return itemstack;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pos, Random rand) {
      if (rand.nextInt(3) == 0) {
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;
         Vec3 rPos = new Vec3(
            pos.getX() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * (0.1 + rand.nextFloat() * 0.6),
            pos.getY() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 0.2,
            pos.getZ() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * (0.1 + rand.nextFloat() * 0.6)
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, rPos.x, rPos.y, rPos.z, 0.0, 0.0, 0.0);
         if (p != null) {
            p.setColor(-3229440);
         }
      }
   }
}
