package iskallia.vault.event;

import iskallia.vault.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AnvilKeyCraftEvent {
   @SubscribeEvent
   public static void onCraftKey(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() == ModItems.BLANK_KEY) {
         ItemStack output = ItemStack.EMPTY;
         Item right = event.getRight().getItem();
         if (right == ModItems.ASHIUM_CLUSTER) {
            output = new ItemStack(ModItems.ASHIUM_KEY);
         } else if (right == ModItems.BOMIGNITE_CLUSTER) {
            output = new ItemStack(ModItems.BOMIGNITE_KEY);
         } else if (right == ModItems.GORGINITE_CLUSTER) {
            output = new ItemStack(ModItems.GORGINITE_KEY);
         } else if (right == ModItems.ISKALLIUM_CLUSTER) {
            output = new ItemStack(ModItems.ISKALLIUM_KEY);
         } else if (right == ModItems.PETZANITE_CLUSTER) {
            output = new ItemStack(ModItems.PETZANITE_KEY);
         } else if (right == ModItems.PUFFIUM_CLUSTER) {
            output = new ItemStack(ModItems.PUFFIUM_KEY);
         } else if (right == ModItems.SPARKLETINE_CLUSTER) {
            output = new ItemStack(ModItems.SPARKLETINE_KEY);
         } else if (right == ModItems.TUBIUM_CLUSTER) {
            output = new ItemStack(ModItems.TUBIUM_KEY);
         } else if (right == ModItems.UPALINE_CLUSTER) {
            output = new ItemStack(ModItems.UPALINE_KEY);
         } else if (right == ModItems.XENIUM_CLUSTER) {
            output = new ItemStack(ModItems.XENIUM_KEY);
         }

         if (!output.isEmpty()) {
            int cost = Math.min(event.getLeft().getCount(), event.getRight().getCount());
            output.setCount(cost);
            event.setOutput(output);
            event.setCost(cost);
            event.setMaterialCost(cost);
         }
      }
   }
}
