package iskallia.vault.block;

import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.container.OmegaStatueContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.StatueType;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class OmegaVariantStatueBlock extends LootStatueBlock {
   public OmegaVariantStatueBlock() {
      super(
         StatueType.OMEGA_VARIANT,
         Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.0F, 3600000.0F).func_226896_b_()
      );
      this.func_180632_j((BlockState)((BlockState)this.func_176194_O().func_177621_b()).func_206870_a(FACING, Direction.SOUTH));
   }

   @Override
   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.field_72995_K && placer instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)placer;
         TileEntity tileEntity = worldIn.func_175625_s(pos);
         if (tileEntity instanceof LootStatueTileEntity) {
            LootStatueTileEntity lootStatue = (LootStatueTileEntity)tileEntity;
            if (stack.func_77942_o()) {
               CompoundNBT nbt = stack.func_77978_p();
               CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");
               this.setStatueTileData(lootStatue, blockEntityTag);
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

   @Override
   protected void setStatueTileData(LootStatueTileEntity lootStatue, CompoundNBT blockEntityTag) {
      StatueType statueType = StatueType.values()[blockEntityTag.func_74762_e("StatueType")];
      String playerNickname = blockEntityTag.func_74779_i("PlayerNickname");
      lootStatue.setStatueType(StatueType.OMEGA_VARIANT);
      lootStatue.setCurrentTick(blockEntityTag.func_74762_e("CurrentTick"));
      lootStatue.getSkin().updateSkin(playerNickname);
      lootStatue.setLootItem(ItemStack.func_199557_a(blockEntityTag.func_74775_l("LootItem")));
   }

   @Override
   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }
}
