package iskallia.vault.mixin;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockBehaviour.class})
public class BlockMixin {
   @Inject(
      method = {"getDrops"},
      cancellable = true,
      at = {@At("RETURN")}
   )
   public void onItemRightClick(BlockState pState, Builder pBuilder, CallbackInfoReturnable<List<ItemStack>> cir) {
      ItemStack stack = (ItemStack)pBuilder.getOptionalParameter(LootContextParams.TOOL);
      if (stack != null && stack.getItem() instanceof PaxelItem) {
         List<PaxelItem.Perk> perks = PaxelItem.getPerks(stack);
         if (perks.contains(PaxelItem.Perk.PULVERISING)) {
            Optional<ItemStack> opt = ModConfigs.PAXEL_CONFIGS.getPulverizedState(pState, pBuilder.getLevel().random);
            opt.ifPresent(itemStack -> cir.setReturnValue(List.of(itemStack)));
         }

         if (perks.contains(PaxelItem.Perk.SMELTING)) {
            List<ItemStack> list = (List<ItemStack>)cir.getReturnValue();
            List<ItemStack> newList = new ArrayList<>();

            for (ItemStack l : list) {
               Optional<SmeltingRecipe> optional = pBuilder.getLevel()
                  .getRecipeManager()
                  .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{l}), pBuilder.getLevel());
               if (optional.isPresent()) {
                  ItemStack itemstack = optional.get().getResultItem();
                  if (!itemstack.isEmpty()) {
                     ItemStack smelted = itemstack.copy();
                     smelted.setCount(l.getCount() * itemstack.getCount());
                     newList.add(smelted);
                     continue;
                  }
               }

               newList.add(l);
            }

            cir.setReturnValue(newList);
         }
      }
   }
}
