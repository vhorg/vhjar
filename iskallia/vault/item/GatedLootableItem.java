package iskallia.vault.item;

import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.data.PlayerResearchesData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class GatedLootableItem extends BasicItem {
   Component[] tooltip;

   public GatedLootableItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public GatedLootableItem(ResourceLocation id, Properties properties, Component... tooltip) {
      super(id, properties);
      this.tooltip = tooltip;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (!world.isClientSide) {
         ItemStack heldStack = player.getItemInHand(hand);
         ServerLevel serverWorld = (ServerLevel)world;
         ResearchTree researches = PlayerResearchesData.get(serverWorld).getResearches(player);
         List<String> unlocked = new ArrayList<>(researches.getResearchesDone());
         WeightedList<ProductEntry> list = null;

         while (list == null && !unlocked.isEmpty()) {
            String researchName = unlocked.remove(world.random.nextInt(unlocked.size()));
            list = ModConfigs.MOD_BOX.POOL.get(researchName);
         }

         ItemStack stack = ItemStack.EMPTY;
         ProductEntry productEntry;
         if (list != null && !list.isEmpty()) {
            productEntry = list.getRandom(world.random);
         } else {
            productEntry = ModConfigs.MOD_BOX.POOL.get("None").getRandom(world.random);
         }

         if (productEntry != null) {
            stack = productEntry.generateItemStack();
         }

         if (stack.isEmpty()) {
            ItemRelicBoosterPack.failureEffects(world, player.position());
         } else {
            while (stack.getCount() > 0) {
               int amount = Math.min(stack.getCount(), stack.getMaxStackSize());
               ItemStack copy = stack.copy();
               copy.setCount(amount);
               stack.shrink(amount);
               player.drop(copy, false, false);
            }

            heldStack.shrink(1);
            ItemRelicBoosterPack.successEffects(world, player.position());
         }
      }

      return super.use(world, player, hand);
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (this.tooltip != null) {
         tooltip.addAll(Arrays.asList(this.tooltip));
      }
   }
}
