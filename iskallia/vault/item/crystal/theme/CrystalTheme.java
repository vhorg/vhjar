package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class CrystalTheme implements INBTSerializable<CompoundTag> {
   public abstract void configure(Vault var1, RandomSource var2);

   public abstract void addText(List<Component> var1, TooltipFlag var2);

   public abstract JsonObject serializeJson();

   public abstract void deserializeJson(JsonObject var1);

   public static CrystalTheme fromNBT(CompoundTag nbt) {
      String var2 = nbt.getString("type");

      CrystalTheme layout = (CrystalTheme)(switch (var2) {
         case "value" -> new ValueCrystalTheme();
         case "pool" -> new PoolCrystalTheme();
         default -> null;
      });
      layout.deserializeNBT(nbt);
      return layout;
   }

   public static class Adapter implements JsonSerializer<CrystalTheme>, JsonDeserializer<CrystalTheme> {
      public static final CrystalTheme.Adapter INSTANCE = new CrystalTheme.Adapter();

      public CrystalTheme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String var6 = object.get("type").getAsString();

         CrystalTheme layout = (CrystalTheme)(switch (var6) {
            case "value" -> new ValueCrystalTheme();
            case "pool" -> new PoolCrystalTheme();
            default -> null;
         });
         layout.deserializeJson(object);
         return layout;
      }

      public JsonElement serialize(CrystalTheme value, Type typeOfSrc, JsonSerializationContext context) {
         return value.serializeJson();
      }
   }
}
