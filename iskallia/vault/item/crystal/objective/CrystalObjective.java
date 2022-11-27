package iskallia.vault.item.crystal.objective;

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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class CrystalObjective implements INBTSerializable<CompoundTag> {
   public abstract void configure(Vault var1, RandomSource var2);

   public abstract Component getName();

   public abstract JsonObject serializeJson();

   public abstract void deserializeJson(JsonObject var1);

   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Objective: ").append(this.getName()));
   }

   public static CrystalObjective fromNBT(CompoundTag nbt) {
      String var2 = nbt.getString("type");

      CrystalObjective objective = (CrystalObjective)(switch (var2) {
         case "boss" -> new BossCrystalObjective();
         case "cake" -> new CakeCrystalObjective();
         case "scavenger" -> new ScavengerCrystalObjective();
         case "speedrun" -> new SpeedrunCrystalObjective();
         case "monolith" -> new MonolithCrystalObjective();
         default -> null;
      });
      objective.deserializeNBT(nbt);
      return objective;
   }

   public static String getId(CrystalObjective objective) {
      if (objective instanceof BossCrystalObjective) {
         return "boss";
      } else if (objective instanceof CakeCrystalObjective) {
         return "cake";
      } else if (objective instanceof ScavengerCrystalObjective) {
         return "scavenger";
      } else if (objective instanceof SpeedrunCrystalObjective) {
         return "speedrun";
      } else {
         return objective instanceof MonolithCrystalObjective ? "monolith" : "";
      }
   }

   public static class Adapter implements JsonSerializer<CrystalObjective>, JsonDeserializer<CrystalObjective> {
      public static final CrystalObjective.Adapter INSTANCE = new CrystalObjective.Adapter();

      public CrystalObjective deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String var6 = object.get("type").getAsString();

         CrystalObjective layout = (CrystalObjective)(switch (var6) {
            case "boss" -> new BossCrystalObjective();
            case "cake" -> new CakeCrystalObjective();
            case "scavenger" -> new ScavengerCrystalObjective();
            case "speedrun" -> new SpeedrunCrystalObjective();
            case "monolith" -> new MonolithCrystalObjective();
            default -> null;
         });
         layout.deserializeJson(object);
         return layout;
      }

      public JsonElement serialize(CrystalObjective value, Type typeOfSrc, JsonSerializationContext context) {
         return value.serializeJson();
      }
   }
}
