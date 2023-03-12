package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
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
   private int tunnelSpan;
   private ArchitectRoomEntry.List entries = new ArchitectRoomEntry.List();
   private float completion;

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

   public ArchitectCrystalLayout addCompletion(float completion) {
      this.completion += completion;
      return this;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      if (vault.has(Vault.WORLD)) {
         vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
            if (generator instanceof GridGenerator grid) {
               grid.set(GridGenerator.LAYOUT, new ArchitectVaultLayout(this.tunnelSpan, this.entries, this.completion));
            }
         });
      }
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         new TextComponent("Layout: ")
            .append(
               new TextComponent("Architect")
                  .withStyle(Style.EMPTY.withColor(4766456))
                  .append(new TextComponent(" | " + Math.min((float)Math.round(this.completion * 100.0F), 100.0F) + "%").withStyle(ChatFormatting.GRAY))
            )
      );

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
      nbt.putInt("tunnel_span", this.tunnelSpan);
      ListTag list = new ListTag();

      for (ArchitectRoomEntry entry : this.entries) {
         list.add(entry.serializeNBT());
      }

      nbt.put("entries", list);
      nbt.putFloat("completion", this.completion);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.tunnelSpan = nbt.getInt("tunnel_span");
      this.entries.clear();
      ListTag list = nbt.getList("entries", 10);

      for (int i = 0; i < list.size(); i++) {
         this.entries.add(ArchitectRoomEntry.fromNBT(list.getCompound(i)));
      }

      this.completion = nbt.getFloat("completion");
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      json.addProperty("tunnel_span", this.tunnelSpan);
      JsonArray list = new JsonArray();

      for (ArchitectRoomEntry entry : this.entries) {
         list.add(entry.serializeJson());
      }

      json.add("entries", list);
      json.addProperty("completion", this.completion);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.tunnelSpan = json.get("tunnel_span").getAsInt();
      this.entries.clear();
      if (json.has("entries")) {
         for (JsonElement element : json.getAsJsonArray("entries")) {
            this.entries.add(ArchitectRoomEntry.fromJson(element.getAsJsonObject()));
         }
      }

      this.completion = json.get("completion").getAsFloat();
   }
}
