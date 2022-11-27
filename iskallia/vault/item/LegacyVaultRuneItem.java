package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.gen.VaultRoomNames;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LegacyVaultRuneItem extends Item {
   private final String roomName;

   public LegacyVaultRuneItem(CreativeModeTab group, ResourceLocation id, String roomName) {
      super(new Properties().tab(group).stacksTo(8));
      this.roomName = roomName;
      this.setRegistryName(id);
   }

   public String getRoomName() {
      return this.roomName;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      Component displayName = VaultRoomNames.getName(this.getRoomName());
      if (displayName != null) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(new TextComponent("Combine with a vault crystal to add").withStyle(ChatFormatting.GRAY));
         tooltip.add(new TextComponent("a room to the vault: ").withStyle(ChatFormatting.GRAY).append(displayName));
         if (ModConfigs.VAULT_RUNE != null) {
            ModConfigs.VAULT_RUNE
               .getMinimumLevel(this)
               .ifPresent(
                  minLevel -> {
                     tooltip.add(TextComponent.EMPTY);
                     tooltip.add(
                        new TextComponent("Only usable after level ")
                           .withStyle(ChatFormatting.GRAY)
                           .append(new TextComponent(String.valueOf(minLevel)).withStyle(ChatFormatting.AQUA))
                     );
                  }
               );
         }
      }
   }
}
