package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPolygonLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ClassicPolygonCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int[] vertices;

   protected ClassicPolygonCrystalLayout() {
   }

   public ClassicPolygonCrystalLayout(int tunnelSpan, int[] vertices) {
      super(tunnelSpan);
      this.vertices = vertices;
   }

   @Override
   public void configure(Vault vault) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicPolygonLayout(this.tunnelSpan, this.vertices));
         }
      });
   }

   @Override
   public Component getName() {
      return new TextComponent("Polygon").withStyle(ChatFormatting.GOLD);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("type", "polygon");
      ListTag list = new ListTag();

      for (int i = 0; i < this.vertices.length; i += 2) {
         CompoundTag element = new CompoundTag();
         element.putInt("x", this.vertices[i]);
         element.putInt("z", this.vertices[i + 1]);
         list.add(element);
      }

      nbt.put("vertices", list);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      ListTag list = nbt.getList("vertices", 10);
      this.vertices = new int[list.size() * 2];

      for (int i = 0; i < list.size(); i++) {
         CompoundTag element = list.getCompound(i);
         this.vertices[i * 2] = element.getInt("x");
         this.vertices[i * 2 + 1] = element.getInt("z");
      }
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = super.serializeJson();
      object.addProperty("type", "polygon");
      JsonArray list = new JsonArray();

      for (int i = 0; i < this.vertices.length; i += 2) {
         JsonObject element = new JsonObject();
         element.addProperty("x", this.vertices[i]);
         element.addProperty("z", this.vertices[i + 1]);
         list.add(element);
      }

      object.add("vertices", list);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject object) {
      super.deserializeJson(object);
      JsonArray list = object.get("vertices").getAsJsonArray();
      this.vertices = new int[list.size() * 2];

      for (int i = 0; i < list.size(); i++) {
         JsonObject element = list.get(i).getAsJsonObject();
         this.vertices[i * 2] = element.get("x").getAsInt();
         this.vertices[i * 2 + 1] = element.get("z").getAsInt();
      }
   }
}
