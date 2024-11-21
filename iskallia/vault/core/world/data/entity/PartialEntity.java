package iskallia.vault.core.world.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class PartialEntity implements EntityPlacement<PartialEntity> {
   private Vec3 pos;
   private BlockPos blockPos;
   private PartialCompoundNbt nbt;

   protected PartialEntity(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      this.pos = pos;
      this.blockPos = blockPos;
      this.nbt = nbt;
   }

   public static PartialEntity of(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      return new PartialEntity(pos, blockPos, nbt);
   }

   public static PartialEntity of(Vec3 pos, BlockPos blockPos, ResourceLocation id, PartialCompoundNbt nbt) {
      if (id != null) {
         CompoundTag tag = nbt.asWhole().orElse(new CompoundTag());
         tag.putString("id", id.toString());
         return new PartialEntity(pos, blockPos, PartialCompoundNbt.of(tag));
      } else {
         return new PartialEntity(pos, blockPos, nbt);
      }
   }

   public static PartialEntity of(Entity entity) {
      return new PartialEntity(entity.position(), entity.blockPosition(), PartialCompoundNbt.of(entity));
   }

   public ResourceLocation getId() {
      return this.getNbt().asWhole().map(nbt -> ResourceLocation.tryParse(nbt.getString("id"))).orElse(null);
   }

   public Vec3 getPos() {
      return this.pos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public PartialCompoundNbt getNbt() {
      return this.nbt;
   }

   public void setPos(Vec3 pos) {
      this.pos = pos;
   }

   public void setBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public void setNbt(PartialCompoundNbt nbt) {
      this.nbt = nbt;
   }

   public boolean isSubsetOf(PartialEntity other) {
      return this.nbt.isSubsetOf(other.nbt);
   }

   @Override
   public boolean isSubsetOf(Entity entity) {
      return this.isSubsetOf(of(entity));
   }

   public void fillInto(PartialEntity other) {
      if (this.pos != null) {
         other.pos = this.pos;
      }

      if (this.blockPos != null) {
         other.blockPos = this.blockPos.immutable();
      }

      this.nbt.fillInto(other.nbt);
   }

   @Override
   public void place(CommonLevelAccessor world) {
   }

   @Override
   public boolean test(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      return this.nbt.isSubsetOf(nbt);
   }

   public PartialEntity copy() {
      return new PartialEntity(this.pos, this.blockPos.immutable(), this.nbt.copy());
   }

   @Override
   public String toString() {
      return this.nbt.toString();
   }

   public static Optional<PartialEntity> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialEntity parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialEntity parse(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && isCharValid(reader.peek())) {
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(null, null, string.isEmpty() ? null : new ResourceLocation(string), PartialCompoundNbt.parse(reader));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid entity identifier '" + string + "' in entity '" + reader.getString() + "'");
         }
      } else {
         return of(null, null, null, PartialCompoundNbt.parse(reader));
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   public static class Adapter implements ISimpleAdapter<PartialEntity, Tag, JsonElement> {
      public void writeBits(PartialEntity value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            Adapters.BLOCK_POS.asNullable().writeBits(value.blockPos, buffer);
            buffer.writeBoolean(value.pos == null);
            if (value.pos != null) {
               Adapters.DOUBLE.writeBits(Double.valueOf(value.pos.x), buffer);
               Adapters.DOUBLE.writeBits(Double.valueOf(value.pos.y), buffer);
               Adapters.DOUBLE.writeBits(Double.valueOf(value.pos.z), buffer);
            }

            Adapters.COMPOUND_NBT.asNullable().writeBits(value.nbt.asWhole().orElse(null), buffer);
         }
      }

      @Override
      public Optional<PartialEntity> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            BlockPos blockPos = Adapters.BLOCK_POS.asNullable().readBits(buffer).orElse(null);
            Vec3 pos = null;
            if (!buffer.readBoolean()) {
               pos = new Vec3(
                  Adapters.DOUBLE.readBits(buffer).orElseThrow(),
                  Adapters.DOUBLE.readBits(buffer).orElseThrow(),
                  Adapters.DOUBLE.readBits(buffer).orElseThrow()
               );
            }

            CompoundTag nbt = Adapters.COMPOUND_NBT.asNullable().readBits(buffer).orElse(null);
            return Optional.of(PartialEntity.of(pos, blockPos, PartialCompoundNbt.of(nbt)));
         }
      }

      public Optional<Tag> writeNbt(@Nullable PartialEntity value) {
         if (value == null) {
            return Optional.empty();
         } else {
            CompoundTag nbt = new CompoundTag();
            if (value.pos != null) {
               ListTag posNBT = new ListTag();
               posNBT.add(DoubleTag.valueOf(value.pos.x));
               posNBT.add(DoubleTag.valueOf(value.pos.y));
               posNBT.add(DoubleTag.valueOf(value.pos.z));
               nbt.put("pos", posNBT);
            }

            Adapters.BLOCK_POS.writeNbt(value.blockPos).ifPresent(tag -> nbt.put("blockPos", tag));
            nbt.put("nbt", (Tag)value.nbt.asWhole().orElse(new CompoundTag()));
            return Optional.of(nbt);
         }
      }

      @Override
      public Optional<PartialEntity> readNbt(@Nullable Tag nbt) {
         if (nbt instanceof CompoundTag compound) {
            if (!compound.contains("nbt")) {
               return Optional.of(PartialEntity.of(null, null, PartialCompoundNbt.of(compound)));
            } else {
               Vec3 pos = null;
               if (compound.contains("pos", 9)) {
                  ListTag posNBT = compound.getList("pos", 6);
                  pos = new Vec3(posNBT.getDouble(0), posNBT.getDouble(1), posNBT.getDouble(2));
               }

               return Optional.of(
                  PartialEntity.of(pos, Adapters.BLOCK_POS.readNbt(compound.get("blockPos")).orElse(null), PartialCompoundNbt.of(compound.getCompound("nbt")))
               );
            }
         } else {
            return Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable PartialEntity value) {
         if (value == null) {
            return Optional.empty();
         } else {
            JsonObject json = new JsonObject();
            if (value.pos != null) {
               JsonArray posNBT = new JsonArray();
               posNBT.add(value.pos.x);
               posNBT.add(value.pos.y);
               posNBT.add(value.pos.z);
               json.add("pos", posNBT);
            }

            Adapters.BLOCK_POS.writeJson(value.blockPos).ifPresent(tag -> json.add("blockPos", tag));
            Adapters.COMPOUND_NBT.writeJson(value.nbt.asWhole().orElse(new CompoundTag())).ifPresent(tag -> json.add("nbt", tag));
            return Optional.of(json);
         }
      }

      @Override
      public Optional<PartialEntity> readJson(@Nullable JsonElement json) {
         if (json instanceof JsonObject object) {
            if (!object.has("nbt")) {
               return Optional.of(PartialEntity.of(null, null, PartialCompoundNbt.of(Adapters.COMPOUND_NBT.readJson(object).orElse(null))));
            } else {
               Vec3 pos = null;
               if (object.get("pos") instanceof JsonArray array) {
                  pos = new Vec3(array.get(0).getAsDouble(), array.get(1).getAsDouble(), array.get(2).getAsDouble());
               }

               return Optional.of(
                  PartialEntity.of(
                     pos,
                     Adapters.BLOCK_POS.readJson(object.get("blockPos")).orElse(null),
                     PartialCompoundNbt.of(Adapters.COMPOUND_NBT.readJson(object.get("nbt")).orElse(null))
                  )
               );
            }
         } else {
            return Optional.empty();
         }
      }
   }
}
