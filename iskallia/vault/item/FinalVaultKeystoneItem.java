package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class FinalVaultKeystoneItem extends Item {
   @Nonnull
   private final PlayerFavourData.VaultGodType associatedGod;

   public FinalVaultKeystoneItem(ResourceLocation id, PlayerFavourData.VaultGodType associatedGod) {
      super(new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.associatedGod = associatedGod;
      this.setRegistryName(id);
   }

   @Nonnull
   public PlayerFavourData.VaultGodType getAssociatedGod() {
      return this.associatedGod;
   }

   public void func_77624_a(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      IFormattableTextComponent godNameText = new StringTextComponent(this.associatedGod.getName()).func_240699_a_(this.associatedGod.getChatColor());
      IFormattableTextComponent godTitleText = new StringTextComponent(this.associatedGod.getTitle())
         .func_240699_a_(this.associatedGod.getChatColor())
         .func_240699_a_(TextFormatting.BOLD);
      tooltip.add(new StringTextComponent(""));
      tooltip.add(new StringTextComponent("Keystone of ").func_230529_a_(godNameText));
      tooltip.add(godTitleText);
   }
}
