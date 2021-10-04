package iskallia.vault.item.gear.applicable;

import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BasicTooltipItem;
import java.util.List;
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

public class TieredVaultItem extends BasicTooltipItem {
   private final int vaultGearTier;

   public TieredVaultItem(ResourceLocation id, Properties properties, int vaultGearTier, ITextComponent... components) {
      super(id, properties, components);
      this.vaultGearTier = vaultGearTier;
   }

   public TieredVaultItem(ResourceLocation id, Properties properties, int vaultGearTier, List<ITextComponent> components) {
      super(id, properties, components);
      this.vaultGearTier = vaultGearTier;
   }

   public int getVaultGearTier() {
      return this.vaultGearTier;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      ITextComponent display = this.getTierDisplayLock();
      if (display != null) {
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(new StringTextComponent("Only usable on Vault Gear ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(display));
      }
   }

   @Nullable
   public ITextComponent getTierDisplayLock() {
      if (ModConfigs.VAULT_GEAR == null) {
         return null;
      } else {
         VaultGearConfig.General.TierConfig cfg = ModConfigs.VAULT_GEAR.getTierConfig(this.getVaultGearTier());
         return cfg != null && !cfg.getDisplay().getString().isEmpty()
            ? new StringTextComponent("Tier: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(cfg.getDisplay())
            : null;
      }
   }
}
