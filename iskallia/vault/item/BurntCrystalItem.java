package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.block.OtherSidePortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.OtherSidePortalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Item.Properties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BurntCrystalItem extends Item {
   public BurntCrystalItem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      if (!context.func_195991_k().field_72995_K && context.func_195999_j() != null) {
         ItemStack stack = context.func_195999_j().func_184586_b(context.func_221531_n());
         if (stack.func_77973_b() == ModItems.BURNT_CRYSTAL) {
            BlockPos pos = context.func_195995_a();
            if (this.tryCreatePortal((ServerWorld)context.func_195991_k(), pos, context.func_196000_l())) {
               context.func_195991_k()
                  .func_184148_a(
                     null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), ModSounds.VAULT_PORTAL_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F
                  );
               context.func_195996_i().func_190918_g(1);
               return ActionResultType.SUCCESS;
            }
         }

         return super.func_195939_a(context);
      } else {
         return super.func_195939_a(context);
      }
   }

   private boolean tryCreatePortal(ServerWorld world, BlockPos pos, Direction facing) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(
         world,
         pos.func_177972_a(facing),
         Axis.X,
         (statex, reader, p) -> Arrays.stream(ModConfigs.OTHER_SIDE.getValidFrameBlocks()).anyMatch(b -> b == statex.func_177230_c())
      );
      ServerWorld target = world.func_73046_m().func_71218_a(world.func_234923_W_() == Vault.OTHER_SIDE_KEY ? World.field_234918_g_ : Vault.OTHER_SIDE_KEY);
      if (optional.isPresent()) {
         VaultPortalSize portal = optional.get();
         OtherSideData data = new OtherSideData(null);
         BlockPos start = portal.getBottomLeft();
         BlockPos match = forcePlace(world, start, target, portal);
         data.setLinkedPos(match);
         data.setLinkedDim(target.func_234923_W_());
         BlockState state = (BlockState)ModBlocks.OTHER_SIDE_PORTAL.func_176223_P().func_206870_a(OtherSidePortalBlock.field_176550_a, portal.getAxis());
         portal.placePortalBlocks(blockPos -> {
            world.func_180501_a(blockPos, state, 3);
            TileEntity te = world.func_175625_s(blockPos);
            if (te instanceof OtherSidePortalTileEntity) {
               OtherSidePortalTileEntity portalTE = (OtherSidePortalTileEntity)te;
               portalTE.setOtherSideData(data);
            }
         });
         return true;
      } else {
         return false;
      }
   }

   public static BlockPos forcePlace(ServerWorld source, BlockPos sourcePos, ServerWorld target, VaultPortalSize current) {
      BlockPos match = null;

      for (int i = 0; i < target.func_217301_I(); i++) {
         BlockPos p = new BlockPos(sourcePos.func_177958_n(), i, sourcePos.func_177952_p());
         Block block = target.func_180495_p(p).func_177230_c();
         if (block == ModBlocks.OTHER_SIDE_PORTAL) {
            match = p;
            break;
         }
      }

      if (match == null) {
         for (int ix = 0; ix < target.func_217301_I(); ix++) {
            int yUp = sourcePos.func_177956_o() + ix;
            int yDo = sourcePos.func_177956_o() - ix;
            if (place(source, sourcePos, target, new BlockPos(sourcePos.func_177958_n(), yUp, sourcePos.func_177952_p()), current, false)) {
               match = new BlockPos(sourcePos.func_177958_n(), yUp, sourcePos.func_177952_p());
               break;
            }

            if (place(source, sourcePos, target, new BlockPos(sourcePos.func_177958_n(), yDo, sourcePos.func_177952_p()), current, false)) {
               match = new BlockPos(sourcePos.func_177958_n(), yDo, sourcePos.func_177952_p());
               break;
            }
         }
      }

      if (match == null) {
         place(source, sourcePos, target, new BlockPos(sourcePos.func_177958_n(), 128, sourcePos.func_177952_p()), current, true);
         match = new BlockPos(sourcePos.func_177958_n(), 128, sourcePos.func_177952_p());
      }

      return match;
   }

   private static boolean place(ServerWorld source, BlockPos sourcePos, ServerWorld target, BlockPos targetPos, VaultPortalSize current, boolean force) {
      if (!force) {
         if (targetPos.func_177956_o() >= target.func_217301_I() || targetPos.func_177956_o() < 0) {
            return false;
         }

         if (!target.func_180495_p(targetPos).func_196958_f()
            || !target.func_180495_p(targetPos.func_177984_a()).func_196958_f()
            || !target.func_180495_p(targetPos.func_177977_b()).func_200132_m()) {
            return false;
         }
      }

      BlockState state = (BlockState)ModBlocks.OTHER_SIDE_PORTAL.func_176223_P().func_206870_a(OtherSidePortalBlock.field_176550_a, current.getAxis());
      OtherSideData data = new OtherSideData(null);
      data.setLinkedDim(source.func_234923_W_());
      data.setLinkedPos(sourcePos);
      BlockPos.func_218278_a(
            targetPos.func_177972_a(Direction.DOWN).func_177972_a(current.getRightDir().func_176734_d()),
            targetPos.func_177967_a(Direction.UP, current.getHeight()).func_177967_a(current.getRightDir(), current.getWidth())
         )
         .forEach(blockPos -> target.func_180501_a(blockPos, Blocks.field_235395_nI_.func_176223_P(), 3));
      BlockPos.func_218278_a(
            targetPos, targetPos.func_177967_a(Direction.UP, current.getHeight() - 1).func_177967_a(current.getRightDir(), current.getWidth() - 1)
         )
         .forEach(blockPos -> {
            target.func_180501_a(blockPos, state, 3);
            TileEntity te = target.func_175625_s(blockPos);
            if (te instanceof OtherSidePortalTileEntity) {
               OtherSidePortalTileEntity portalTE = (OtherSidePortalTileEntity)te;
               portalTE.setOtherSideData(data);
            }
         });
      return true;
   }
}
