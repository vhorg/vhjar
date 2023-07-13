package iskallia.vault.gear;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;

public enum VaultGearRarity {
   SCRAPPY(ChatFormatting.GRAY, 2, 2, 1, 1, 0, 1, 1),
   COMMON(ChatFormatting.AQUA, 3, 3, 2, 2, 1, 2, 2),
   RARE(ChatFormatting.YELLOW, 4, 4, 3, 3, 2, 3, 3),
   EPIC(ChatFormatting.LIGHT_PURPLE, 5, 5, 4, 4, 3, 4, 4),
   OMEGA(ChatFormatting.GREEN, 6, 6, 5, 5, 4, 5, 5),
   UNIQUE(TextColor.fromRgb(-1213660), 0, 0, 0, 0, 0, 0, 0);

   private final TextColor color;
   private final int armorModifierCount;
   private final int weaponModifierCount;
   private final int idolModifierCount;
   private final int shieldModifierCount;
   private final int jewelModifierCount;
   private final int magnetModifierCount;
   private final int wandModifierCount;

   private VaultGearRarity(
      ChatFormatting color,
      int armorModifierCount,
      int weaponModifierCount,
      int idolModifierCount,
      int shieldModifierCount,
      int jewelModifierCount,
      int magnetModifierCount,
      int wandModifierCount
   ) {
      this(
         TextColor.fromLegacyFormat(color),
         armorModifierCount,
         weaponModifierCount,
         idolModifierCount,
         shieldModifierCount,
         jewelModifierCount,
         magnetModifierCount,
         wandModifierCount
      );
   }

   private VaultGearRarity(
      TextColor color,
      int armorModifierCount,
      int weaponModifierCount,
      int idolModifierCount,
      int shieldModifierCount,
      int jewelModifierCount,
      int magnetModifierCount,
      int wandModifierCount
   ) {
      this.color = color;
      this.armorModifierCount = armorModifierCount;
      this.weaponModifierCount = weaponModifierCount;
      this.idolModifierCount = idolModifierCount;
      this.shieldModifierCount = shieldModifierCount;
      this.jewelModifierCount = jewelModifierCount;
      this.magnetModifierCount = magnetModifierCount;
      this.wandModifierCount = wandModifierCount;
   }

   public TextColor getColor() {
      return this.color;
   }

   public int getArmorModifierCount() {
      return this.armorModifierCount;
   }

   public int getWeaponModifierCount() {
      return this.weaponModifierCount;
   }

   public int getIdolModifierCount() {
      return this.idolModifierCount;
   }

   public int getShieldModifierCount() {
      return this.shieldModifierCount;
   }

   public int getJewelModifierCount() {
      return this.jewelModifierCount;
   }

   public int getMagnetModifierCount() {
      return this.magnetModifierCount;
   }

   public int getWandModifierCount() {
      return this.wandModifierCount;
   }

   public Component getDisplayName() {
      Style style = Style.EMPTY.withColor(this.getColor());
      String name = StringUtils.capitalize(this.name().toLowerCase());
      return new TextComponent(name).withStyle(style);
   }
}
