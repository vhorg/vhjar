package iskallia.vault.dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityAttrDump extends JsonDump {
   @Override
   public String fileName() {
      return "entity_attr.json";
   }

   @Override
   public JsonObject dumpToJSON() {
      JsonObject root = new JsonObject();
      ForgeRegistries.ENTITIES.getValues().forEach(entity -> {
         try {
            this.baseAttrsFor(entity, root);
         } catch (Throwable var4) {
         }
      });
      return root;
   }

   private <T extends LivingEntity> void baseAttrsFor(EntityType<T> entityType, JsonObject root) {
      JsonArray attributesJson = new JsonArray();
      ResourceLocation entityId = entityType.getRegistryName();
      this.getAttributes(entityType).forEach((attr, attrInstance) -> {
         JsonObject jsonEntry = new JsonObject();
         jsonEntry.addProperty("attributeId", attr.getRegistryName().toString());
         jsonEntry.addProperty("value", attrInstance.getValue());
         attributesJson.add(jsonEntry);
      });
      if (entityId == null) {
         throw new InternalError();
      } else {
         root.add(entityId.toString(), attributesJson);
      }
   }

   private <T extends LivingEntity> Map<Attribute, AttributeInstance> getAttributes(EntityType<T> entityType) {
      try {
         AttributeSupplier attributes = DefaultAttributes.getSupplier(entityType);
         Field attributeMapField = AttributeSupplier.class.getDeclaredField("attributeMap");
         attributeMapField.setAccessible(true);
         return (Map<Attribute, AttributeInstance>)attributeMapField.get(attributes);
      } catch (NoSuchFieldException | IllegalAccessException var4) {
         return new HashMap<>();
      }
   }
}
