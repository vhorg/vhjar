package iskallia.vault.antique.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import iskallia.vault.VaultMod;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AntiqueCondition {
   private ResourceLocation providerId;

   public abstract boolean test(DropConditionContext var1);

   public abstract void deserialize(JsonDeserializationContext var1, JsonObject var2);

   public abstract JsonObject serialize(JsonSerializationContext var1);

   public final <T> T cast() {
      return (T)this;
   }

   public abstract static class Provider extends ForgeRegistryEntry<AntiqueCondition.Provider> {
      public Provider(ResourceLocation id) {
         this.setRegistryName(id);
      }

      public final AntiqueCondition provideCondition() {
         AntiqueCondition condition = this.makeCondition();
         condition.providerId = this.getRegistryName();
         return condition;
      }

      protected abstract AntiqueCondition makeCondition();

      public static AntiqueCondition.Provider make(ResourceLocation id, final Supplier<AntiqueCondition> newInst) {
         return new AntiqueCondition.Provider(id) {
            @Override
            public AntiqueCondition makeCondition() {
               return newInst.get();
            }
         };
      }
   }

   public static class Serializer implements JsonDeserializer<AntiqueCondition>, JsonSerializer<AntiqueCondition> {
      public static final AntiqueCondition.Serializer INSTANCE = new AntiqueCondition.Serializer();

      private Serializer() {
      }

      public AntiqueCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = GsonHelper.convertToJsonObject(json, "condition");
         String type = "or";
         if (object.has("type")) {
            type = GsonHelper.getAsString(object, "type");
         }

         if (!type.contains(":")) {
            type = VaultMod.sId(type);
         }

         ResourceLocation id = ResourceLocation.tryParse(type);
         if (id == null) {
            throw new JsonSyntaxException("Unknown condition type: " + type);
         } else {
            AntiqueCondition.Provider provider = (AntiqueCondition.Provider)AntiqueConditionRegistry.getRegistry().getValue(id);
            if (provider == null) {
               throw new JsonSyntaxException("Unknown condition type: " + type);
            } else {
               AntiqueCondition condition = provider.provideCondition();
               condition.deserialize(context, object);
               return condition;
            }
         }
      }

      public JsonElement serialize(AntiqueCondition src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = src.serialize(context);
         object.addProperty("type", src.providerId.toString());
         return object;
      }
   }
}
