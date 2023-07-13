package iskallia.vault.core.world.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class PartialEntityGroup implements EntityPlacement<PartialEntityGroup> {
   private ResourceLocation id;
   private PartialCompoundNbt nbt;

   public PartialEntityGroup(ResourceLocation id, PartialCompoundNbt nbt) {
      this.id = id;
      this.nbt = nbt;
   }

   public static PartialEntityGroup of(ResourceLocation id, PartialCompoundNbt entity) {
      return new PartialEntityGroup(id, entity);
   }

   public boolean isSubsetOf(PartialEntityGroup other) {
      return (this.id == null || this.id.equals(other.id)) && this.nbt.isSubsetOf(other.nbt);
   }

   @Override
   public boolean isSubsetOf(Entity entity) {
      return false;
   }

   public void fillInto(PartialEntityGroup other) {
      if (this.id != null) {
         other.id = this.id;
      }

      this.nbt.fillInto(other.nbt);
   }

   @Override
   public void place(CommonLevelAccessor world) {
   }

   @Override
   public boolean test(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      return this.nbt.isSubsetOf(nbt) && ModConfigs.ENTITY_GROUPS.isInGroup(this.id, pos, blockPos, nbt);
   }

   public PartialEntityGroup copy() {
      return new PartialEntityGroup(this.id, this.nbt.copy());
   }

   @Override
   public String toString() {
      return (this.id != null ? "@" + this.id : "") + this.nbt.toString();
   }

   public static Optional<PartialEntityGroup> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialEntityGroup parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialEntityGroup parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '@') {
         throw new IllegalArgumentException("Invalid entity group '" + reader.getString() + "' does not start with @");
      } else {
         reader.skip();
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(new ResourceLocation(string), PartialCompoundNbt.parse(reader));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid group identifier '" + string + "' in entity group '" + reader.getString() + "'");
         }
      }
   }

   public ResourceLocation getId() {
      return this.id;
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PartialEntityGroup that = (PartialEntityGroup)o;
         return Objects.equals(this.id, that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }
}
