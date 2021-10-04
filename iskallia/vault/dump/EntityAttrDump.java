package iskallia.vault.dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
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
         jsonEntry.addProperty("value", attrInstance.func_111126_e());
         attributesJson.add(jsonEntry);
      });
      if (entityId == null) {
         throw new InternalError();
      } else {
         root.add(entityId.toString(), attributesJson);
      }
   }

   private <T extends LivingEntity> Map<Attribute, ModifiableAttributeInstance> getAttributes(EntityType<T> entityType) {
      try {
         AttributeModifierMap attributes = GlobalEntityTypeAttributes.func_233835_a_(entityType);
         Field attributeMapField = AttributeModifierMap.class.getDeclaredField("attributeMap");
         attributeMapField.setAccessible(true);
         return (Map<Attribute, ModifiableAttributeInstance>)attributeMapField.get(attributes);
      } catch (NoSuchFieldException | IllegalAccessException var4) {
         return new HashMap<>();
      }
   }
}
