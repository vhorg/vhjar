package iskallia.vault.antique.reward;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import iskallia.vault.core.random.RandomSource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AntiqueReward {
   private ResourceLocation providerId;

   public abstract List<ItemStack> generateReward(RandomSource var1, ServerPlayer var2, int var3);

   public abstract void deserialize(JsonDeserializationContext var1, JsonObject var2);

   public abstract JsonObject serialize(JsonSerializationContext var1);

   public final <T> T cast() {
      return (T)this;
   }

   public abstract static class Provider extends ForgeRegistryEntry<AntiqueReward.Provider> {
      public Provider(ResourceLocation id) {
         this.setRegistryName(id);
      }

      public final AntiqueReward provideReward() {
         AntiqueReward condition = this.makeReward();
         condition.providerId = this.getRegistryName();
         return condition;
      }

      protected abstract AntiqueReward makeReward();

      public static AntiqueReward.Provider make(ResourceLocation id, final Supplier<AntiqueReward> newInst) {
         return new AntiqueReward.Provider(id) {
            @Override
            public AntiqueReward makeReward() {
               return newInst.get();
            }
         };
      }
   }

   public static class Serializer implements JsonDeserializer<AntiqueReward>, JsonSerializer<AntiqueReward> {
      public static final AntiqueReward.Serializer INSTANCE = new AntiqueReward.Serializer();

      private Serializer() {
      }

      public AntiqueReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String typeStr = GsonHelper.getAsString(object, "type");
         ResourceLocation typeId = ResourceLocation.tryParse(typeStr);
         if (typeId == null) {
            throw new JsonSyntaxException("Unknown reward type: " + typeStr);
         } else {
            AntiqueReward.Provider provider = (AntiqueReward.Provider)AntiqueRewardTypeRegistry.getRegistry().getValue(typeId);
            if (provider == null) {
               throw new JsonSyntaxException("Unknown reward type: " + typeStr);
            } else {
               AntiqueReward reward = provider.provideReward();
               reward.deserialize(context, object);
               return reward;
            }
         }
      }

      public JsonElement serialize(AntiqueReward src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = src.serialize(context);
         object.addProperty("type", src.providerId.toString());
         return object;
      }
   }
}
