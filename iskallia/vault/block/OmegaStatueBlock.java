package iskallia.vault.block;

import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.container.OmegaStatueContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.StatueType;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class OmegaStatueBlock extends LootStatueBlock {
   public static final BooleanProperty MASTER = BooleanProperty.func_177716_a("master");

   public OmegaStatueBlock() {
      super(StatueType.OMEGA);
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.func_176194_O().func_177621_b()).func_206870_a(FACING, Direction.SOUTH))
            .func_206870_a(MASTER, Boolean.TRUE)
      );
   }

   @Nullable
   @Override
   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockPos pos = context.func_195995_a();
      if (pos.func_177956_o() > 255) {
         return null;
      } else {
         World world = context.func_195991_k();

         for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
               if (!world.func_180495_p(pos.func_177982_a(x, 0, z)).func_196953_a(context)) {
                  return null;
               }
            }
         }

         return (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f())).func_206870_a(MASTER, Boolean.TRUE);
      }
   }

   @Override
   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.field_72995_K && placer instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)placer;

         for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
               if (x != 0 || z != 0) {
                  BlockPos newBlockPos = pos.func_177982_a(x, 0, z);
                  worldIn.func_175656_a(newBlockPos, (BlockState)state.func_206870_a(MASTER, Boolean.FALSE));
                  TileEntity te = worldIn.func_175625_s(newBlockPos);
                  if (te instanceof LootStatueTileEntity) {
                     ((LootStatueTileEntity)te).setStatueType(StatueType.OMEGA);
                     ((LootStatueTileEntity)te).setMaster(false);
                     ((LootStatueTileEntity)te).setMasterPos(pos);
                     te.func_70296_d();
                     ((LootStatueTileEntity)te).sendUpdates();
                  }
               }
            }
         }

         if ((Boolean)state.func_177229_b(MASTER)) {
            TileEntity tileEntity = worldIn.func_175625_s(pos);
            if (tileEntity instanceof LootStatueTileEntity) {
               LootStatueTileEntity lootStatue = (LootStatueTileEntity)tileEntity;
               if (stack.func_77942_o()) {
                  CompoundNBT nbt = stack.func_77978_p();
                  CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");
                  String playerNickname = blockEntityTag.func_74779_i("PlayerNickname");
                  lootStatue.setStatueType(StatueType.OMEGA);
                  lootStatue.setCurrentTick(blockEntityTag.func_74762_e("CurrentTick"));
                  lootStatue.setMaster(true);
                  lootStatue.setMasterPos(pos);
                  lootStatue.setItemsRemaining(-1);
                  lootStatue.setTotalItems(0);
                  lootStatue.setPlayerScale(MathUtilities.randomFloat(2.0F, 4.0F));
                  lootStatue.getSkin().updateSkin(playerNickname);
                  if (nbt.func_74764_b("LootItem")) {
                     lootStatue.setLootItem(ItemStack.func_199557_a(blockEntityTag.func_74775_l("LootItem")));
                  }

                  lootStatue.func_70296_d();
                  lootStatue.sendUpdates();
                  if (lootStatue.getLootItem() == null || lootStatue.getLootItem().func_190926_b()) {
                     final CompoundNBT data = new CompoundNBT();
                     ListNBT itemList = new ListNBT();

                     for (ItemStack option : ModConfigs.STATUE_LOOT.getOmegaOptions()) {
                        itemList.add(option.serializeNBT());
                     }

                     data.func_218657_a("Items", itemList);
                     data.func_218657_a("Position", NBTUtil.func_186859_a(pos));
                     NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
                        public ITextComponent func_145748_c_() {
                           return new StringTextComponent("Omega Statue Options");
                        }

                        @Nullable
                        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                           return new OmegaStatueContainer(windowId, data);
                        }
                     }, buffer -> buffer.func_150786_a(data));
                  }
               }
            }
         }
      }
   }

   @Override
   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
      builder.func_206894_a(new Property[]{MASTER});
   }

   public void func_196243_a(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      TileEntity te = worldIn.func_175625_s(pos);
      if (te instanceof LootStatueTileEntity) {
         BlockPos masterPos = ((LootStatueTileEntity)te).getMasterPos();
         TileEntity master = worldIn.func_175625_s(masterPos);
         if (master instanceof LootStatueTileEntity) {
            for (int x = -1; x <= 1; x++) {
               for (int z = -1; z <= 1; z++) {
                  BlockPos newBlockPos = masterPos.func_177982_a(x, 0, z);
                  worldIn.func_175713_t(newBlockPos);
                  worldIn.func_180501_a(newBlockPos, Blocks.field_150350_a.func_176223_P(), 3);
               }
            }
         }
      }

      super.func_196243_a(state, worldIn, pos, newState, isMoving);
   }

   @Override
   public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      LootStatueTileEntity statue = this.getStatueTileEntity(worldIn, pos);
      if (statue != null) {
         LootStatueTileEntity master = this.getMaster(statue);
         if (master != null) {
            BlockPos masterPos = master.func_174877_v();
            return super.func_225533_a_(worldIn.func_180495_p(masterPos), worldIn, masterPos, player, handIn, hit);
         }
      }

      return ActionResultType.FAIL;
   }

   private LootStatueTileEntity getMaster(LootStatueTileEntity statue) {
      World world = statue.func_145831_w();
      if (world != null) {
         TileEntity master = statue.func_145831_w().func_175625_s(statue.getMasterPos());
         if (master instanceof LootStatueTileEntity) {
            return (LootStatueTileEntity)master;
         }
      }

      return null;
   }

   @Override
   public void func_176208_a(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      LootStatueTileEntity statue = this.getStatueTileEntity(world, pos);
      if (statue != null) {
         LootStatueTileEntity master = this.getMaster(statue);
         if (master != null) {
            BlockPos masterPos = master.func_174877_v();
            super.func_176208_a(world, masterPos, world.func_180495_p(masterPos), player);
         }
      }
   }
}
