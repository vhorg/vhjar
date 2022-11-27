package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.DIYRoomEntry;
import iskallia.vault.core.world.generator.layout.DIYVaultLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class DIYCrystalLayout extends CrystalLayout {
   private int tunnelSpan;
   private List<DIYRoomEntry> entries = new ArrayList<>();

   protected DIYCrystalLayout() {
   }

   public DIYCrystalLayout(int tunnelSpan, Collection<DIYRoomEntry> entries) {
      this.tunnelSpan = tunnelSpan;
      this.entries.addAll(entries);
   }

   public DIYCrystalLayout add(DIYRoomEntry entry) {
      for (DIYRoomEntry e : this.entries) {
         e.mergeWith(entry);
      }

      if (entry.get(DIYRoomEntry.COUNT) > 0) {
         this.entries.add(entry);
      }

      return this;
   }

   @Override
   public void configure(Vault vault) {
      if (vault.has(Vault.WORLD)) {
         vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
            if (generator instanceof GridGenerator grid) {
               grid.set(GridGenerator.LAYOUT, new DIYVaultLayout(this.tunnelSpan, this.entries));
            }
         });
      }
   }

   @Override
   public Component getName() {
      return new TextComponent("DIY").withStyle(ChatFormatting.DARK_PURPLE);
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      super.addText(tooltip, flag);

      for (DIYRoomEntry entry : this.entries) {
         int count = entry.get(DIYRoomEntry.COUNT);
         String roomStr = count > 1 ? "Rooms" : "Room";
         Component txt = new TextComponent("- Has ")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.valueOf(count)).withStyle(ChatFormatting.GOLD))
            .append(" ")
            .append(entry.getName())
            .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
         tooltip.add(txt);
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "diy");
      nbt.putInt("tunnel_span", this.tunnelSpan);
      ListTag list = new ListTag();

      for (DIYRoomEntry entry : this.entries) {
         list.add(entry.serializeNBT());
      }

      nbt.put("entries", list);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.tunnelSpan = nbt.getInt("tunnel_span");
      this.entries.clear();
      ListTag list = nbt.getList("entries", 10);

      for (int i = 0; i < list.size(); i++) {
         this.entries.add(DIYRoomEntry.fromNBT(list.getCompound(i)));
      }
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "diy");
      object.addProperty("tunnel_span", this.tunnelSpan);
      JsonArray list = new JsonArray();

      for (DIYRoomEntry entry : this.entries) {
         list.add(entry.serializeJson());
      }

      object.add("entries", list);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.tunnelSpan = json.get("tunnel_span").getAsInt();
      this.entries.clear();

      for (JsonElement element : json.getAsJsonArray("entries")) {
         this.entries.add(DIYRoomEntry.fromJson(element.getAsJsonObject()));
      }
   }
}
