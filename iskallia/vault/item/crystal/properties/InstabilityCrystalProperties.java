package iskallia.vault.item.crystal.properties;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class InstabilityCrystalProperties extends CrystalProperties {
   private float instability = 0.0F;

   public float getInstability() {
      return this.instability;
   }

   public void setInstability(float instability) {
      this.instability = instability;
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      super.addText(tooltip, minIndex, flag, time);
      if (this.instability > 0.0F) {
         TextComponent instabilityComponent = new TextComponent("%.1f%%".formatted(this.instability * 100.0F));
         instabilityComponent.setStyle(Style.EMPTY.withColor(this.getInstabilityTextColor(Math.round(this.instability * 100.0F))));
         tooltip.add(tooltip.size() - 1, new TextComponent("Instability: ").append(instabilityComponent));
      }
   }

   private TextColor getInstabilityTextColor(float instability) {
      float threshold = 0.5F;
      float hueDarkGreen = 0.3334F;
      float hueGold = 0.1111F;
      float hue;
      float saturation;
      float value;
      if (instability <= 0.5F) {
         float p = instability / 0.5F;
         hue = (1.0F - p) * 0.3334F + p * 0.1111F;
         saturation = 1.0F;
         value = (1.0F - p) * 0.8F + p;
      } else {
         float p = (instability - 0.5F) / 0.5F;
         hue = (1.0F - p) * 0.1111F;
         saturation = 1.0F - p + p * 0.8F;
         value = 1.0F - p + p * 0.8F;
      }

      return TextColor.fromRgb(Color.HSBtoRGB(hue, saturation, value));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.instability)).ifPresent(tag -> nbt.put("instability", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.instability = Adapters.FLOAT.readNbt(nbt.get("instability")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(nbt -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.instability)).ifPresent(tag -> nbt.add("instability", tag));
         return (JsonObject)nbt;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.instability = Adapters.FLOAT.readJson(json.get("instability")).orElse(0.0F);
   }
}
