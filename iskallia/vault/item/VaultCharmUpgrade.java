package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultCharmUpgrade extends BasicItem {
   private final VaultCharmUpgrade.Tier tier;

   public VaultCharmUpgrade(ResourceLocation id, VaultCharmUpgrade.Tier tier, Properties properties) {
      super(id, properties);
      this.tier = tier;
   }

   public Component getName(ItemStack stack) {
      return new TextComponent("Vault Junk Upgrade (" + this.tier.getName() + ")");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      tooltip.add(TextComponent.EMPTY);
      tooltip.addAll(getTooltipForTier(this.tier));
   }

   private static List<Component> getTooltipForTier(VaultCharmUpgrade.Tier tier) {
      List<Component> tooltip = new ArrayList<>();
      if (ModConfigs.VAULT_CHARM != null) {
         int slotCount = tier.getSlotAmount();
         tooltip.add(new TextComponent("Increases the amount of slots"));
         tooltip.add(new TextComponent("that items can be added to the"));
         tooltip.add(new TextComponent("Vault Junk Whitelist to " + ChatFormatting.YELLOW + slotCount));
      }

      return tooltip;
   }

   public VaultCharmUpgrade.Tier getTier() {
      return this.tier;
   }

   public static enum Tier {
      ONE("Tier 1", 1),
      TWO("Tier 2", 2),
      THREE("Tier 3", 3),
      FOUR("Tier 4", 4);

      private final String name;
      private final int tier;

      private Tier(String name, int tier) {
         this.name = name;
         this.tier = tier;
      }

      public String getName() {
         return this.name;
      }

      public int getTier() {
         return this.tier;
      }

      public int getSlotAmount() {
         return ModConfigs.VAULT_CHARM.getMultiplierForTier(this.tier) * 9;
      }

      public static VaultCharmUpgrade.Tier getTierBySize(int size) {
         return getByValue(
            ModConfigs.VAULT_CHARM
               .getMultipliers()
               .entrySet()
               .stream()
               .filter(entrySet -> entrySet.getValue() * 9 == size)
               .map(Entry::getKey)
               .findFirst()
               .orElse(-1)
         );
      }

      public static VaultCharmUpgrade.Tier getByValue(int value) {
         return Arrays.stream(values()).filter(tier -> tier.getTier() == value).findFirst().orElse(null);
      }

      public VaultCharmUpgrade.Tier getNext() {
         return switch (this) {
            case ONE -> TWO;
            case TWO -> THREE;
            case THREE -> FOUR;
            default -> null;
         };
      }
   }
}
