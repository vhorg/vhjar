package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.vault.Vault;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class CrystalLayout implements INBTSerializable<CompoundTag> {
   public abstract void configure(Vault var1);

   public abstract Component getName();

   public abstract JsonObject serializeJson();

   public abstract void deserializeJson(JsonObject var1);

   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Layout: ").append(this.getName()));
   }

   public static CrystalLayout fromNBT(CompoundTag nbt) {
      String var2 = nbt.getString("type");

      CrystalLayout layout = (CrystalLayout)(switch (var2) {
         case "infinite" -> new ClassicInfiniteCrystalLayout();
         case "circle" -> new ClassicCircleCrystalLayout();
         case "polygon" -> new ClassicPolygonCrystalLayout();
         case "spiral" -> new ClassicSpiralCrystalLayout();
         case "diy" -> new DIYCrystalLayout();
         default -> null;
      });
      layout.deserializeNBT(nbt);
      return layout;
   }

   public static class Adapter implements JsonSerializer<CrystalLayout>, JsonDeserializer<CrystalLayout> {
      public static final CrystalLayout.Adapter INSTANCE = new CrystalLayout.Adapter();

      public CrystalLayout deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String var6 = object.get("type").getAsString();

         CrystalLayout layout = (CrystalLayout)(switch (var6) {
            case "infinite" -> new ClassicInfiniteCrystalLayout();
            case "circle" -> new ClassicCircleCrystalLayout();
            case "polygon" -> new ClassicPolygonCrystalLayout();
            case "spiral" -> new ClassicSpiralCrystalLayout();
            case "diy" -> new DIYCrystalLayout();
            default -> null;
         });
         layout.deserializeJson(object);
         return layout;
      }

      public JsonElement serialize(CrystalLayout value, Type typeOfSrc, JsonSerializationContext context) {
         return value.serializeJson();
      }
   }
}
