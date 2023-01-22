package iskallia.vault.mixin;

import iskallia.vault.init.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {AnvilMenu.class},
   priority = 1001
)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {
   @Shadow
   @Final
   private DataSlot cost;
   @Shadow
   public int repairItemCountCost;

   public MixinAnvilMenu(@Nullable MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
      super(p_39773_, p_39774_, p_39775_, p_39776_);
   }

   @Overwrite
   protected void onTake(Player p_150474_, ItemStack p_150475_) {
      if (!p_150474_.getAbilities().instabuild) {
         p_150474_.giveExperienceLevels(-this.cost.get());
      }

      float breakChance = ForgeHooks.onAnvilRepair(p_150474_, p_150475_, this.inputSlots.getItem(0), this.inputSlots.getItem(1));
      if (this.inputSlots.getItem(0).getItem() == ModItems.BLANK_KEY && this.repairItemCountCost > 0) {
         this.inputSlots.getItem(0).shrink(this.repairItemCountCost);
      } else {
         this.inputSlots.setItem(0, ItemStack.EMPTY);
      }

      if (this.repairItemCountCost > 0) {
         ItemStack itemstack = this.inputSlots.getItem(1);
         if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
            itemstack.shrink(this.repairItemCountCost);
            this.inputSlots.setItem(1, itemstack);
         } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
         }
      } else {
         this.inputSlots.setItem(1, ItemStack.EMPTY);
      }

      this.cost.set(0);
      this.access.execute((p_150479_, p_150480_) -> {
         BlockState blockstate = p_150479_.getBlockState(p_150480_);
         if (!p_150474_.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL) && p_150474_.getRandom().nextFloat() < breakChance) {
            BlockState blockstate1 = AnvilBlock.damage(blockstate);
            if (blockstate1 == null) {
               p_150479_.removeBlock(p_150480_, false);
               p_150479_.levelEvent(1029, p_150480_, 0);
            } else {
               p_150479_.setBlock(p_150480_, blockstate1, 2);
               p_150479_.levelEvent(1030, p_150480_, 0);
            }
         } else {
            p_150479_.levelEvent(1030, p_150480_, 0);
         }
      });
   }
}
