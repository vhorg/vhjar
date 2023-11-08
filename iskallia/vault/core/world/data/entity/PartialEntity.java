package iskallia.vault.core.world.data.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
}
