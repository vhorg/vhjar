package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultCharmUpgrade extends BasicItem {
   private final VaultCharmUpgrade.Tier tier;

   public VaultCharmUpgrade(ResourceLocation id, VaultCharmUpgrade.Tier tier, Properties properties) {
      super(id, properties);
      this.tier = tier;
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      return new StringTextComponent("Vault Charm Upgrade (" + this.tier.getName() + ")");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringTextComponent.field_240750_d_);
      tooltip.addAll(getTooltipForTier(this.tier));
   }

   private static List<ITextComponent> getTooltipForTier(VaultCharmUpgrade.Tier tier) {
      List<ITextComponent> tooltip = new ArrayList<>();
      if (ModConfigs.VAULT_CHARM != null) {
         int slotCount = tier.getSlotAmount();
         tooltip.add(new StringTextComponent("Increases the amount of slots"));
         tooltip.add(new StringTextComponent("that items can be added to the"));
         tooltip.add(new StringTextComponent("Vault Charm Whitelist to " + TextFormatting.YELLOW + slotCount));
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
         switch (this) {
            case ONE:
               return TWO;
            case TWO:
               return THREE;
            case THREE:
               return FOUR;
            default:
               return null;
         }
      }
   }
}
