package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPolygonLayout;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ClassicPolygonCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int[] vertices;

   public ClassicPolygonCrystalLayout() {
   }

   public ClassicPolygonCrystalLayout(int tunnelSpan, int[] vertices) {
      super(tunnelSpan);
      this.vertices = vertices;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicPolygonLayout(this.tunnelSpan, this.vertices));
         }
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Polygon").withStyle(ChatFormatting.GOLD)));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         ListTag list = new ListTag();

         for (int i = 0; i < this.vertices.length; i += 2) {
            CompoundTag element = new CompoundTag();
            element.putInt("x", this.vertices[i]);
            element.putInt("z", this.vertices[i + 1]);
            list.add(element);
         }

         nbt.put("vertices", list);
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      ListTag list = nbt.getList("vertices", 10);
      this.vertices = new int[list.size() * 2];

      for (int i = 0; i < list.size(); i++) {
         CompoundTag element = list.getCompound(i);
         this.vertices[i * 2] = element.getInt("x");
         this.vertices[i * 2 + 1] = element.getInt("z");
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         JsonArray list = new JsonArray();

         for (int i = 0; i < this.vertices.length; i += 2) {
            JsonObject element = new JsonObject();
            element.addProperty("x", this.vertices[i]);
            element.addProperty("z", this.vertices[i + 1]);
            list.add(element);
         }

         json.add("vertices", list);
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      JsonArray list = json.get("vertices").getAsJsonArray();
      this.vertices = new int[list.size() * 2];

      for (int i = 0; i < list.size(); i++) {
         JsonObject element = list.get(i).getAsJsonObject();
         this.vertices[i * 2] = element.get("x").getAsInt();
         this.vertices[i * 2 + 1] = element.get("z").getAsInt();
      }
   }
}
