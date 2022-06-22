package iskallia.vault.item;

import iskallia.vault.world.data.PlayerFavourData;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

public class GodEssenceItem extends BasicItem {
   protected PlayerFavourData.VaultGodType godType;

   public GodEssenceItem(ResourceLocation id, PlayerFavourData.VaultGodType godType, Properties properties) {
      super(id, properties);
      this.godType = godType;
   }

   public PlayerFavourData.VaultGodType getGodType() {
      return this.godType;
   }

   @Nonnull
   public ITextComponent func_200295_i(@Nonnull ItemStack stack) {
      return ((IFormattableTextComponent)super.func_200295_i(stack)).func_240699_a_(this.godType.getChatColor());
   }
}
