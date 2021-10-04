package iskallia.vault.item.gear.applicable;

import java.util.List;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class VaultRepairCoreItem extends TieredVaultItem {
   public VaultRepairCoreItem(ResourceLocation id, Properties properties, int vaultGearTier, ITextComponent... components) {
      super(id, properties, vaultGearTier, components);
   }

   public VaultRepairCoreItem(ResourceLocation id, Properties properties, int vaultGearTier, List<ITextComponent> components) {
      super(id, properties, vaultGearTier, components);
   }
}
