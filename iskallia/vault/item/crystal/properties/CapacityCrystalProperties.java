package iskallia.vault.item.crystal.properties;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.MysticExpertise;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CapacityCrystalProperties extends CrystalProperties {
   private Integer volume = null;
   private int size = 0;

   public Optional<Integer> getVolume() {
      return Optional.ofNullable(this.volume);
   }

   public int getSize() {
      return this.size;
   }

   public Optional<Integer> getCapacity() {
      return this.volume == null ? Optional.empty() : Optional.of(this.volume - this.size);
   }

   public CapacityCrystalProperties setVolume(int volume) {
      this.volume = volume;
      return this;
   }

   public CapacityCrystalProperties setSize(int size) {
      this.size = size;
      return this;
   }

   public boolean canAccept(int size) {
      return this.volume != null && size <= this.volume - this.size;
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      super.onInventoryTick(world, entity, slot, selected);
      if (this.volume == null && this.level != null && entity instanceof ServerPlayer sPlayer) {
         ModConfigs.VAULT_CRYSTAL
            .getRandomProperties(VaultMod.id("default"), this.level)
            .ifPresent(
               properties -> {
                  if (properties instanceof CapacityCrystalProperties capacityProperties) {
                     capacityProperties.getVolume()
                        .ifPresent(
                           volume -> {
                              int additionalVolume = PlayerExpertisesData.get(sPlayer.getLevel())
                                 .getExpertises(sPlayer)
                                 .getAll(MysticExpertise.class, Skill::isUnlocked)
                                 .stream()
                                 .mapToInt(MysticExpertise::getAdditionalCrystalVolume)
                                 .sum();
                              this.volume = volume + additionalVolume;
                           }
                        );
                  }
               }
            );
      }
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      super.addText(tooltip, minIndex, flag, time);
      if (this.volume == null) {
         tooltip.add(minIndex + 1, new TextComponent("Capacity: ???").withStyle(ChatFormatting.GRAY));
      } else if (this.volume > 0) {
         int color = this.getCapacityTextColor(1.0F - (float)this.size / this.volume.intValue()).getValue();
         tooltip.add(
            minIndex + 1,
            new TextComponent("Capacity: ")
               .append(new TextComponent(String.valueOf(Math.max(this.volume - this.size, 0))).setStyle(Style.EMPTY.withColor(color)))
               .append(new TextComponent("/").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent(String.valueOf(this.volume)).withStyle(ChatFormatting.WHITE))
         );
      }
   }

   private TextColor getCapacityTextColor(float fullness) {
      float threshold = 0.5F;
      float hueDarkGreen = 0.3334F;
      float hueGold = 0.1111F;
      float hue;
      float saturation;
      float value;
      if (fullness >= 0.5F) {
         float p = (fullness - 0.5F) / 0.5F;
         hue = (1.0F - p) * 0.1111F + p * 0.3334F;
         saturation = 1.0F;
         value = (1.0F - p) * 0.8F + p;
      } else {
         float p = fullness / 0.5F;
         hue = (1.0F - p) * 0.1111F;
         saturation = 1.0F - p + p * 0.8F;
         value = 1.0F - p + p * 0.8F;
      }

      return TextColor.fromRgb(Color.HSBtoRGB(hue, saturation, value));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT_SEGMENTED_7.writeNbt(this.volume).ifPresent(tag -> nbt.put("volume", tag));
         Adapters.INT_SEGMENTED_7.writeNbt(Integer.valueOf(this.size)).ifPresent(tag -> nbt.put("size", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.volume = Adapters.INT_SEGMENTED_7.readNbt(nbt.get("volume")).orElse(null);
      this.size = Adapters.INT_SEGMENTED_7.readNbt(nbt.get("size")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(nbt -> {
         Adapters.INT_SEGMENTED_7.writeJson(this.volume).ifPresent(tag -> nbt.add("volume", tag));
         Adapters.INT_SEGMENTED_7.writeJson(Integer.valueOf(this.size)).ifPresent(tag -> nbt.add("size", tag));
         return (JsonObject)nbt;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.volume = Adapters.INT_SEGMENTED_7.readJson(json.get("volume")).orElse(null);
      this.size = Adapters.INT_SEGMENTED_7.readJson(json.get("size")).orElse(0);
   }
}
