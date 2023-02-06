package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.PaxelItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PaxelJewelItem extends BasicItem {
   private final PaxelItem.Perk perk;

   public PaxelJewelItem(ResourceLocation id, PaxelItem.Perk perk) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP));
      this.perk = perk;
   }

   @Override
   public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
      if (ModConfigs.isInitialized()) {
         MutableComponent t = new TextComponent(this.perk.getSerializedName())
            .withStyle(Style.EMPTY.withColor(ModConfigs.PAXEL_CONFIGS.getPerkUpgrade(this.perk).getColor()));
         pTooltipComponents.add(new TextComponent("Socket with a Vault Pickaxe to add ").append(t).withStyle(ChatFormatting.GRAY));
      }
   }

   public PaxelItem.Perk getPerk() {
      return this.perk;
   }
}
