package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.FinalVaultKeystoneItem;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.block.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FinalVaultKeystoneSocketBlock extends Block {
   public static BooleanProperty ACTIVATED = BooleanProperty.func_177716_a("activated");
   public static EnumProperty<PlayerFavourData.VaultGodType> ASSOCIATED_GOD = EnumProperty.func_177709_a("associated_god", PlayerFavourData.VaultGodType.class);
   private static BlockPattern portalShape;
   public static final VoxelShape SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

   public FinalVaultKeystoneSocketBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200943_b(3.6E7F).func_200947_a(SoundType.field_185851_d));
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(ACTIVATED, false))
            .func_206870_a(ASSOCIATED_GOD, PlayerFavourData.VaultGodType.BENEVOLENT)
      );
   }

   public BlockPattern getOrCreatePortalShape() {
      if (portalShape == null) {
         portalShape = BlockPatternBuilder.func_177660_a()
            .func_177659_a(new String[]{"?xx?", "x??x", "x??x", "?xx?"})
            .func_177662_a('?', CachedBlockInfo.func_177510_a(BlockStateMatcher.field_185928_a))
            .func_177662_a(
               'x',
               CachedBlockInfo.func_177510_a(
                  BlockStateMatcher.func_177638_a(ModBlocks.FINAL_VAULT_KEYSTONE_SOCKET).func_201028_a(ACTIVATED, o -> Objects.equals(o, true))
               )
            )
            .func_177661_b();
      }

      return portalShape;
   }

   @Nonnull
   public VoxelShape func_220053_a(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
      return SHAPE;
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{ACTIVATED}).func_206894_a(new Property[]{ASSOCIATED_GOD});
   }

   @Nonnull
   public ActionResultType func_225533_a_(
      @Nonnull BlockState state,
      @Nonnull World world,
      @Nonnull BlockPos pos,
      @Nonnull PlayerEntity player,
      @Nonnull Hand hand,
      @Nonnull BlockRayTraceResult hit
   ) {
      if (!world.field_72995_K) {
         ItemStack heldItem = player.func_184586_b(hand);
         Item item = heldItem.func_77973_b();
         if (item instanceof FinalVaultKeystoneItem) {
            FinalVaultKeystoneItem keystoneItem = (FinalVaultKeystoneItem)item;
            PlayerFavourData.VaultGodType blockGodType = (PlayerFavourData.VaultGodType)state.func_177229_b(ASSOCIATED_GOD);
            boolean activated = (Boolean)state.func_177229_b(ACTIVATED);
            if (!activated && keystoneItem.getAssociatedGod() == blockGodType) {
               if (!player.func_184812_l_()) {
                  heldItem.func_190918_g(1);
               }

               BlockState newState = (BlockState)state.func_206870_a(ACTIVATED, true);
               world.func_180501_a(pos, newState, 3);
               PatternHelper patternHelper = this.getOrCreatePortalShape().func_177681_a(world, pos);
               if (patternHelper != null) {
                  int portalSize = 2;
                  BlockPos portalStart = patternHelper.func_181117_a().func_177982_a(-portalSize, 0, -portalSize);

                  for (int x = 0; x < portalSize; x++) {
                     for (int z = 0; z < portalSize; z++) {
                        world.func_180501_a(portalStart.func_177982_a(x, 0, z), Blocks.field_196605_bc.func_176223_P(), 2);
                     }
                  }

                  world.func_175669_a(1038, portalStart.func_177982_a(1, 0, 1), 0);
               }
            }
         }
      }

      return super.func_225533_a_(state, world, pos, player, hand, hit);
   }
}
