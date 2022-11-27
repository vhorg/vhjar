package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemLegendaryTreasure extends Item {
   private final VaultRarity vaultRarity;

   public ItemLegendaryTreasure(CreativeModeTab group, ResourceLocation id, VaultRarity vaultRarity) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
      this.vaultRarity = vaultRarity;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      if (worldIn.isClientSide) {
         return super.use(worldIn, playerIn, handIn);
      } else if (handIn != InteractionHand.MAIN_HAND) {
         return super.use(worldIn, playerIn, handIn);
      } else {
         ItemStack stack = playerIn.getMainHandItem();
         if (stack.getItem() instanceof ItemLegendaryTreasure item) {
            ItemStack toDrop = switch (item.getRarity()) {
               case COMMON -> ModConfigs.LEGENDARY_TREASURE_NORMAL.getRandom();
               case RARE -> ModConfigs.LEGENDARY_TREASURE_RARE.getRandom();
               case EPIC -> ModConfigs.LEGENDARY_TREASURE_EPIC.getRandom();
               case OMEGA -> ModConfigs.LEGENDARY_TREASURE_OMEGA.getRandom();
            };
            playerIn.drop(toDrop, false);
            stack.shrink(1);
            ItemRelicBoosterPack.successEffects(worldIn, playerIn.position());
         }

         return super.use(worldIn, playerIn, handIn);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (stack.getItem() instanceof ItemLegendaryTreasure item) {
         tooltip.add(new TextComponent(ChatFormatting.GOLD + "Right-Click to identify..."));
         tooltip.add(new TextComponent("Rarity: " + item.getRarity().color + item.getRarity()));
      }

      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }

   public Component getName(ItemStack stack) {
      return (Component)(stack.getItem() instanceof ItemLegendaryTreasure item
         ? new TextComponent(item.getRarity().color + "Legendary Treasure")
         : super.getName(stack));
   }

   public VaultRarity getRarity() {
      return this.vaultRarity;
   }
}
