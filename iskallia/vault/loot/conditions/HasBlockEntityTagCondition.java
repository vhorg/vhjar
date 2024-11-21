package iskallia.vault.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import iskallia.vault.init.ModLootConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class HasBlockEntityTagCondition implements LootItemCondition {
   private final String tagName;

   public HasBlockEntityTagCondition(String tagName) {
      this.tagName = tagName;
   }

   public boolean test(LootContext context) {
      BlockEntity blockEntity = (BlockEntity)context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
      if (blockEntity == null) {
         return false;
      } else {
         CompoundTag nbt = blockEntity.saveWithoutMetadata();
         return nbt.contains(this.tagName);
      }
   }

   @NotNull
   public LootItemConditionType getType() {
      return (LootItemConditionType)ModLootConditions.HAS_BLOCK_ENTITY_TAG.get();
   }

   public static class ConditionSerializer implements Serializer<HasBlockEntityTagCondition> {
      public void serialize(JsonObject json, HasBlockEntityTagCondition condition, @NotNull JsonSerializationContext context) {
         json.addProperty("tag", condition.tagName);
      }

      @NotNull
      public HasBlockEntityTagCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
         return new HasBlockEntityTagCondition(GsonHelper.getAsString(json, "tag"));
      }
   }
}
