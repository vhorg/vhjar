package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public abstract class VaultGearModifierReader<T> {
   private final String modifierName;
   private final int rgbColor;

   protected VaultGearModifierReader(String modifierName, int rgbColor) {
      this.modifierName = modifierName;
      this.rgbColor = rgbColor;
   }

   public String getModifierName() {
      return this.modifierName;
   }

   public int getRgbColor() {
      return this.rgbColor;
   }

   public Style getColoredTextStyle() {
      return Style.EMPTY.withColor(this.getRgbColor());
   }

   @Nullable
   public abstract MutableComponent getDisplay(VaultGearAttributeInstance<T> var1, VaultGearModifier.AffixType var2);

   @Nullable
   public abstract MutableComponent getValueDisplay(T var1);

   @Nonnull
   public JsonObject serializeDisplay(VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
      JsonObject obj = new JsonObject();
      obj.addProperty("color", this.getRgbColor());
      JsonArray elements = new JsonArray();
      this.serializeTextElements(elements, instance, type);
      obj.add("elements", elements);
      return obj;
   }

   protected abstract void serializeTextElements(JsonArray var1, VaultGearAttributeInstance<T> var2, VaultGearModifier.AffixType var3);

   @Nullable
   public MutableComponent getDisplay(VaultGearAttributeInstance<T> instance, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
      return this.getDisplay(instance, type);
   }
}
