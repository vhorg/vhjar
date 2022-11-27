package iskallia.vault.util;

import net.minecraft.ChatFormatting;

public enum VaultRarity {
   COMMON(ChatFormatting.WHITE),
   RARE(ChatFormatting.YELLOW),
   EPIC(ChatFormatting.LIGHT_PURPLE),
   OMEGA(ChatFormatting.GREEN);

   public final ChatFormatting color;

   private VaultRarity(ChatFormatting color) {
      this.color = color;
   }
}
