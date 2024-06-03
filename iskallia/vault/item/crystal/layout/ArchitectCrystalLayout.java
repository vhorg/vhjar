package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.objective.ArchitectObjective;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.core.world.generator.layout.ArchitectVaultLayout;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ArchitectCrystalLayout extends CrystalLayout {
   private Integer tunnelSpan;
   private ArchitectRoomEntry.List entries = new ArchitectRoomEntry.List();
   private Float completion;

   public ArchitectCrystalLayout() {
   }

   public ArchitectCrystalLayout(int tunnelSpan, Collection<ArchitectRoomEntry> entries) {
      this.tunnelSpan = tunnelSpan;
      this.entries.addAll(entries);
   }

   public ArchitectCrystalLayout add(ArchitectRoomEntry entry) {
      for (ArchitectRoomEntry e : this.entries) {
         e.mergeWith(entry);
      }

      if (entry.get(ArchitectRoomEntry.COUNT) > 0) {
         this.entries.add(entry);
      }

      return this;
   }

   public boolean addCompletion(float completion) {
      if (this.completion == null) {
         return false;
      } else {
         this.completion = this.completion + completion;
         return true;
      }
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      if (vault.has(Vault.WORLD)) {
         if (this.tunnelSpan != null && this.completion != null) {
            vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
               if (generator instanceof GridGenerator grid) {
                  grid.set(GridGenerator.LAYOUT, new ArchitectVaultLayout(this.tunnelSpan, this.entries, this.completion));
               }
            });
         } else {
            vault.get(Vault.OBJECTIVES).add(ArchitectObjective.create(this.entries));
         }
      }
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      if (this.completion != null) {
         tooltip.add(
            new TextComponent("Layout: ")
               .append(
                  new TextComponent("Architect")
                     .withStyle(Style.EMPTY.withColor(4766456))
                     .append(new TextComponent(" | %.1f%%".formatted(Math.min(this.completion, 1.0F) * 100.0F)).withStyle(ChatFormatting.GRAY))
               )
         );
      }

      for (ArchitectRoomEntry entry : this.entries) {
         int count = entry.get(ArchitectRoomEntry.COUNT);
         String roomStr = count > 1 ? "Rooms" : "Room";
         Component txt = new TextComponent(" • ")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.valueOf(count)).withStyle(ChatFormatting.GRAY))
            .append(" ")
            .append(entry.getName())
            .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
         tooltip.add(txt);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      if (this.tunnelSpan != null) {
         nbt.putInt("tunnel_span", this.tunnelSpan);
      }

      ListTag list = new ListTag();

      for (ArchitectRoomEntry entry : this.entries) {
         list.add(entry.serializeNBT());
      }

      nbt.put("entries", list);
      if (this.completion != null) {
         nbt.putFloat("completion", this.completion);
      }

      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.tunnelSpan = nbt.contains("tunnel_span") ? nbt.getInt("tunnel_span") : null;
      this.entries.clear();
      ListTag list = nbt.getList("entries", 10);

      for (int i = 0; i < list.size(); i++) {
         this.entries.add(ArchitectRoomEntry.fromNBT(list.getCompound(i)));
      }

      this.completion = nbt.contains("completion") ? nbt.getFloat("completion") : null;
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      if (this.tunnelSpan != null) {
         json.addProperty("tunnel_span", this.tunnelSpan);
      }

      JsonArray list = new JsonArray();

      for (ArchitectRoomEntry entry : this.entries) {
         list.add(entry.serializeJson());
      }

      json.add("entries", list);
      if (this.completion != null) {
         json.addProperty("completion", this.completion);
      }

      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.tunnelSpan = json.has("tunnel_span") ? json.get("tunnel_span").getAsInt() : null;
      this.entries.clear();
      if (json.has("entries")) {
         for (JsonElement element : json.getAsJsonArray("entries")) {
            this.entries.add(ArchitectRoomEntry.fromJson(element.getAsJsonObject()));
         }
      }

      this.completion = json.has("completion") ? json.get("completion").getAsFloat() : null;
   }
}
