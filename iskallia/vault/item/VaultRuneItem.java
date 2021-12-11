package iskallia.vault.item;

import iskallia.vault.world.vault.gen.VaultRoomNames;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultRuneItem extends Item {
   private final String roomName;

   public VaultRuneItem(ItemGroup group, ResourceLocation id, String roomName) {
      super(new Properties().func_200916_a(group).func_200917_a(8));
      this.roomName = roomName;
      this.setRegistryName(id);
   }

   public String getRoomName() {
      return this.roomName;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      ITextComponent displayName = VaultRoomNames.getName(this.getRoomName());
      if (displayName != null) {
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(new StringTextComponent("Combine with a vault crystal to add").func_240699_a_(TextFormatting.GRAY));
         tooltip.add(new StringTextComponent("a room to the vault: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(displayName));
      }
   }
}
