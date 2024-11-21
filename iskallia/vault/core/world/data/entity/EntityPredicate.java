package iskallia.vault.core.world.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface EntityPredicate {
   EntityPredicate FALSE = (pos, blockPos, nbt) -> false;
   EntityPredicate TRUE = (pos, blockPos, nbt) -> true;

   boolean test(Vec3 var1, BlockPos var2, PartialCompoundNbt var3);

   default boolean test(PartialEntity entity) {
      return this.test(entity.getPos(), entity.getBlockPos(), entity.getNbt());
   }

   default boolean test(Entity entity) {
      return this.test(entity.position(), entity.blockPosition(), PartialCompoundNbt.of(entity.serializeNBT()));
   }

   static Optional<EntityPredicate> of(String string, boolean logErrors) {
      if (string.isEmpty()) {
         return Optional.of(TRUE);
      } else {
         return (switch (string.charAt(0)) {
            case '#' -> PartialEntityTag.parse(string, logErrors);
            case '@' -> PartialEntityGroup.parse(string, logErrors);
            default -> PartialEntity.parse(string, logErrors);
         }).map(o -> (EntityPredicate)o);
      }
   }

   public static class Adapter implements ISimpleAdapter<EntityPredicate, Tag, JsonElement> {
      private static ArrayAdapter<EntityPredicate> LIST = Adapters.ofArray(EntityPredicate[]::new, new EntityPredicate.Adapter());

      public void writeBits(@Nullable EntityPredicate value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            if (value instanceof OrEntityPredicate or) {
               buffer.writeBoolean(true);
               LIST.writeBits(or.getChildren(), buffer);
            } else {
               buffer.writeBoolean(false);
               Adapters.UTF_8.writeBits(value == EntityPredicate.TRUE ? "" : value.toString(), buffer);
            }
         }
      }

      @Override
      public final Optional<EntityPredicate> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            return buffer.readBoolean()
               ? LIST.readBits(buffer).map(OrEntityPredicate::new)
               : Adapters.UTF_8.readBits(buffer).map(string -> EntityPredicate.of(string, true).orElse(EntityPredicate.FALSE));
         }
      }

      public Optional<Tag> writeNbt(@Nullable EntityPredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrEntityPredicate or ? LIST.writeNbt(or.getChildren()) : Optional.of(StringTag.valueOf(value.toString()));
         }
      }

      @Override
      public Optional<EntityPredicate> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else if (nbt instanceof ListTag list) {
            return LIST.readNbt(list).map(OrEntityPredicate::new);
         } else {
            return nbt instanceof StringTag string
               ? Optional.of(EntityPredicate.of(string.getAsString(), true).orElse(EntityPredicate.FALSE))
               : Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable EntityPredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrEntityPredicate or ? LIST.writeJson(or.getChildren()) : Optional.of(new JsonPrimitive(value.toString()));
         }
      }

      @Override
      public Optional<EntityPredicate> readJson(@Nullable JsonElement json) {
         if (json == null) {
            return Optional.empty();
         } else if (json instanceof JsonArray array) {
            return LIST.readJson(array).map(OrEntityPredicate::new);
         } else {
            return json instanceof JsonPrimitive primitive && primitive.isString()
               ? Optional.of(EntityPredicate.of(json.getAsString(), true).orElse(EntityPredicate.FALSE))
               : Optional.empty();
         }
      }
   }
}
