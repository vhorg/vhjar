package iskallia.vault.block.discoverable;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DiscoverTriggeringBlock extends Block {
   public static BooleanProperty DISCOVERED = BooleanProperty.create("discovered");
   protected List<Pair<DiscoverTriggeringBlock.DiscoveryStrategy, ResourceLocation>> discoveries;
   protected VoxelShape shape;
   public static final DiscoverTriggeringBlock.DiscoveryStrategy GEAR_MODEL_DISCOVERY = (player, discoverId) -> {
      DiscoveredModelsData modelsData = DiscoveredModelsData.get(player.getLevel());
      ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(discoverId).ifPresent(pair -> {
         Item item = (Item)pair.getSecond();
         modelsData.discoverModelAndBroadcast(item, discoverId, player);
      });
   };
   public static final DiscoverTriggeringBlock.DiscoveryStrategy ARMOR_MODEL_DISCOVERY = (player, modelId) -> {
      DiscoveredModelsData modelsData = DiscoveredModelsData.get(player.getLevel());
      ModDynamicModels.Armor.MODEL_REGISTRY
         .get(modelId)
         .map(ArmorModel::getPieces)
         .ifPresent(
            pieces -> pieces.forEach(
               (equipmentSlot, armorPieceModel) -> modelsData.discoverModelAndBroadcast(VaultArmorItem.forSlot(equipmentSlot), armorPieceModel.getId(), player)
            )
         );
   };

   public DiscoverTriggeringBlock(List<Pair<DiscoverTriggeringBlock.DiscoveryStrategy, ResourceLocation>> discoveries, Properties properties) {
      this(discoveries, properties, null);
   }

   public DiscoverTriggeringBlock(List<Pair<DiscoverTriggeringBlock.DiscoveryStrategy, ResourceLocation>> discoveries, Properties properties, VoxelShape shape) {
      super(properties);
      this.discoveries = discoveries;
      this.shape = shape;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISCOVERED, false));
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
      return this.shape == null ? super.getShape(state, pLevel, pPos, pContext) : this.shape;
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{DISCOVERED});
   }

   @Nullable
   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state == null ? null : (BlockState)state.setValue(DISCOVERED, true);
   }

   public void playerWillDestroy(Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         ItemStack itemStack = new ItemStack(this);
         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
         if (!(Boolean)state.getValue(DISCOVERED)) {
            for (Pair<DiscoverTriggeringBlock.DiscoveryStrategy, ResourceLocation> discovery : this.discoveries) {
               DiscoverTriggeringBlock.DiscoveryStrategy strategy = (DiscoverTriggeringBlock.DiscoveryStrategy)discovery.getFirst();
               ResourceLocation discoverId = (ResourceLocation)discovery.getSecond();
               strategy.discover((ServerPlayer)player, discoverId);
            }
         }
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   @FunctionalInterface
   interface DiscoveryStrategy {
      void discover(ServerPlayer var1, ResourceLocation var2);
   }
}
