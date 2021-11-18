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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GatedLootableItem extends BasicItem {
   ITextComponent[] tooltip;

   public GatedLootableItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public GatedLootableItem(ResourceLocation id, Properties properties, ITextComponent... tooltip) {
      super(id, properties);
      this.tooltip = tooltip;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         ItemStack heldStack = player.func_184586_b(hand);
         ServerWorld serverWorld = (ServerWorld)world;
         ResearchTree researches = PlayerResearchesData.get(serverWorld).getResearches(player);
         List<String> unlocked = new ArrayList<>(researches.getResearchesDone());
         WeightedList<ProductEntry> list = null;

         while (list == null && !unlocked.isEmpty()) {
            String researchName = unlocked.remove(world.field_73012_v.nextInt(unlocked.size()));
            list = ModConfigs.MOD_BOX.POOL.get(researchName);
         }

         ItemStack stack = ItemStack.field_190927_a;
         ProductEntry productEntry;
         if (list != null && !list.isEmpty()) {
            productEntry = list.getRandom(world.field_73012_v);
         } else {
            productEntry = ModConfigs.MOD_BOX.POOL.get("None").getRandom(world.field_73012_v);
         }

         if (productEntry != null) {
            stack = productEntry.generateItemStack();
         }

         if (stack.func_190926_b()) {
            ItemRelicBoosterPack.failureEffects(world, player.func_213303_ch());
         } else {
            while (stack.func_190916_E() > 0) {
               int amount = Math.min(stack.func_190916_E(), stack.func_77976_d());
               ItemStack copy = stack.func_77946_l();
               copy.func_190920_e(amount);
               stack.func_190918_g(amount);
               player.func_146097_a(copy, false, false);
            }

            heldStack.func_190918_g(1);
            ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
         }
      }

      return super.func_77659_a(world, player, hand);
   }

   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      if (this.tooltip != null) {
         tooltip.addAll(Arrays.asList(this.tooltip));
      }
   }
}
