package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public abstract class EntityFilterTalent extends LearnableSkill {
   private EntityPredicate[] filter;
   private static final ArrayAdapter<EntityPredicate> FILTER = Adapters.ofArray(EntityPredicate[]::new, Adapters.ENTITY_PREDICATE);

   public EntityFilterTalent(int unlockLevel, int learnPointCost, int regretPointCost, EntityPredicate[] filter) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.filter = filter;
   }

   protected EntityFilterTalent() {
   }

   public boolean isValid(Entity entity) {
      for (EntityPredicate filter : this.filter) {
         if (filter.test(entity)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      FILTER.writeBits(this.filter, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.filter = FILTER.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         FILTER.writeNbt(this.filter).ifPresent(tag -> nbt.put("filter", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.filter = FILTER.readNbt(nbt.get("filter")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         FILTER.writeJson(this.filter).ifPresent(element -> json.add("filter", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.filter = FILTER.readJson(json.get("filter")).orElseThrow();
   }
}
