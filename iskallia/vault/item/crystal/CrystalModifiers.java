package iskallia.vault.item.crystal;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class CrystalModifiers extends ArrayList<VaultModifierStack> implements INBTSerializable<ListTag> {
   public ListTag serializeNBT() {
      ListTag nbt = new ListTag();

      for (VaultModifierStack stack : this) {
         nbt.add(stack.serializeNBT());
      }

      return nbt;
   }

   public void deserializeNBT(ListTag nbt) {
      this.clear();

      for (int i = 0; i < nbt.size(); i++) {
         this.add(VaultModifierStack.of(nbt.getCompound(i)));
      }
   }

   public static class Adapter implements JsonSerializer<CrystalModifiers>, JsonDeserializer<CrystalModifiers> {
      public static final CrystalModifiers.Adapter INSTANCE = new CrystalModifiers.Adapter();

      public CrystalModifiers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         CrystalModifiers modifiers = new CrystalModifiers();

         for (JsonElement element : json.getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            modifiers.add(
               VaultModifierStack.of(VaultModifierRegistry.get(new ResourceLocation(object.get("modifier").getAsString())), object.get("count").getAsInt())
            );
         }

         return modifiers;
      }

      public JsonElement serialize(CrystalModifiers value, Type typeOfSrc, JsonSerializationContext context) {
         JsonArray array = new JsonArray();

         for (VaultModifierStack stack : value) {
            JsonObject element = new JsonObject();
            element.addProperty("modifier", stack.getModifierId().toString());
            element.addProperty("count", stack.getSize());
            array.add(element);
         }

         return array;
      }
   }
}
