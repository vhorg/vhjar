package iskallia.vault.block;

import iskallia.vault.VaultMod;
import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.antique.condition.DropConditionContext;
import iskallia.vault.antique.condition.DropConditionContextFactory;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.AntiqueItem;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VaultCrateBlock extends Block implements EntityBlock {
   public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.create("horizontal_facing", Plane.HORIZONTAL);
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public VaultCrateBlock() {
      super(Properties.of(Material.METAL, MaterialColor.METAL).strength(2.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH)).setValue(FACING, Direction.UP));
   }

   public static Stream<Antique> generateCrateAntiques(VaultCrateBlock.Type type, int level) {
      DropConditionContext ctx = DropConditionContextFactory.makeRewardCrate(level, getCrateBlock(type));
      return AntiqueRegistry.getAntiquesMatchingCondition(ctx);
   }

   public static ItemStack getCrateWithLootWithAntiques(VaultCrateBlock.Type type, int level, NonNullList<ItemStack> items) {
      generateCrateAntiques(type, level).forEach(antique -> items.add(AntiqueItem.createStack(antique)));
      return getCrateWithLoot(type, items);
   }

   public static ItemStack getCrateWithLoot(VaultCrateBlock.Type type, NonNullList<ItemStack> items) {
      Block block = getCrateBlock(type);
      if (items.size() > 54) {
         VaultMod.LOGGER.error("Attempted to get a crate with more than 54 items. Check crate loot table.");
         items = NonNullList.of(ItemStack.EMPTY, items.stream().limit(54L).toArray(ItemStack[]::new));
      }

      ItemStack crate = new ItemStack(block);
      CompoundTag nbt = new CompoundTag();
      ContainerHelper.saveAllItems(nbt, items);
      if (!nbt.isEmpty()) {
         crate.addTagElement("BlockEntityTag", nbt);
      }

      return crate;
   }

   public static Block getCrateBlock(VaultCrateBlock.Type type) {
      return switch (type) {
         case BOSS -> ModBlocks.VAULT_CRATE;
         case SCAVENGER -> ModBlocks.VAULT_CRATE_SCAVENGER;
         case CAKE -> ModBlocks.VAULT_CRATE_CAKE;
         case ARENA -> ModBlocks.VAULT_CRATE_ARENA;
         case CHAMPION -> ModBlocks.VAULT_CRATE_CHAMPION;
         case BOUNTY -> ModBlocks.VAULT_CRATE_BOUNTY;
         case MONOLITH -> ModBlocks.VAULT_CRATE_MONOLITH;
         case ELIXIR -> ModBlocks.VAULT_CRATE_ELIXIR;
         case PARADOX -> ModBlocks.VAULT_CRATE_PARADOX;
         case BINGO -> ModBlocks.VAULT_CRATE_BINGO;
         case OFFERING_BOSS -> ModBlocks.VAULT_CRATE_OFFERING_BOSS;
      };
   }

   public static ItemStack getCrateWithLootOversized(VaultCrateBlock.Type type, NonNullList<OverSizedItemStack> items) {
      Block block = getCrateBlock(type);
      if (items.size() > 54) {
         VaultMod.LOGGER.error("Attempted to get a crate with more than 54 items. Check crate loot table.");
         items = NonNullList.of(OverSizedItemStack.EMPTY, items.stream().limit(54L).toArray(OverSizedItemStack[]::new));
      }

      ItemStack crate = new ItemStack(block);
      CompoundTag nbt = new CompoundTag();
      NBTHelper.writeCollection(nbt, "items", items, CompoundTag.class, OverSizedItemStack::serialize);
      if (!nbt.isEmpty()) {
         crate.addTagElement("BlockEntityTag", nbt);
      }

      return crate;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VAULT_CRATE_TILE_ENTITY.create(pPos, pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HORIZONTAL_FACING, FACING});
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (player.isShiftKeyDown() && !world.isClientSide) {
         if (world.getBlockEntity(pos) instanceof VaultCrateTileEntity crate) {
            crate.getItems().forEach(oversizedStack -> oversizedStack.splitByStackSize().forEach(split -> popResource(world, pos, split)));
            crate.getItems().clear();
         }

         world.removeBlock(pos, false);
         Vec3 vec3 = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
         world.playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.CRATE_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
         BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
         ((ServerLevel)world).sendParticles(particle, vec3.x, vec3.y, vec3.z, 400, 1.0, 1.0, 1.0, 0.5);
         ((ServerLevel)world).sendParticles(ParticleTypes.SCRAPE, vec3.x, vec3.y, vec3.z, 50, 1.0, 1.0, 1.0, 0.5);
      }

      return InteractionResult.SUCCESS;
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      super.playerWillDestroy(world, pos, state, player);
      if (world.getBlockEntity(pos) instanceof VaultCrateTileEntity crate) {
         ItemStack itemstack = new ItemStack(this);
         CompoundTag compoundnbt = crate.saveWithoutMetadata();
         if (!compoundnbt.isEmpty()) {
            itemstack.addTagElement("BlockEntityTag", compoundnbt);
         }

         ItemEntity itementity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemstack);
         itementity.setDefaultPickUpDelay();
         world.addFreshEntity(itementity);
      }
   }

   @javax.annotation.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction placeDir = context.getNearestLookingDirection().getOpposite();
      Direction horizontalDir = Direction.NORTH;
      if (placeDir.getAxis().isVertical()) {
         for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
               horizontalDir = direction;
               break;
            }
         }
      }

      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, placeDir)).setValue(HORIZONTAL_FACING, horizontalDir);
   }

   private VaultCrateTileEntity getCrateTileEntity(Level worldIn, BlockPos pos) {
      BlockEntity var4 = worldIn.getBlockEntity(pos);
      return var4 instanceof VaultCrateTileEntity ? (VaultCrateTileEntity)var4 : null;
   }

   public static enum Type {
      BOSS,
      SCAVENGER,
      CAKE,
      ARENA,
      CHAMPION,
      BOUNTY,
      MONOLITH,
      ELIXIR,
      PARADOX,
      BINGO,
      OFFERING_BOSS;
   }
}
