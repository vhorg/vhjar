package iskallia.vault.core.world.data.tile;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;

public class PartialBlockTag implements TilePlacement<PartialBlockTag> {
   private ResourceLocation id;
   private PartialBlockProperties properties;
   private PartialCompoundNbt entity;

   public PartialBlockTag(ResourceLocation id, PartialBlockProperties properties, PartialCompoundNbt entity) {
      this.id = id;
      this.properties = properties;
      this.entity = entity;
   }

   public static PartialBlockTag of(ResourceLocation id, PartialBlockProperties properties, PartialCompoundNbt entity) {
      return new PartialBlockTag(id, properties, entity);
   }

   public boolean isSubsetOf(PartialBlockTag other) {
      return (this.id == null || this.id.equals(other.id)) && this.properties.isSubsetOf(other.properties) && this.entity.isSubsetOf(other.entity);
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      throw new UnsupportedOperationException();
   }

   public void fillInto(PartialBlockTag other) {
      if (this.id != null) {
         other.id = this.id;
      }

      this.properties.fillInto(other.properties);
      this.entity.fillInto(other.entity);
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      this.properties.place(world, pos, flags);
      this.entity.place(world, pos, flags);
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.properties.isSubsetOf(state.getProperties())
         && this.entity.isSubsetOf(nbt)
         && state.asWhole().map(other -> other.getTags().anyMatch(tag -> tag.location().equals(this.id))).orElse(false);
   }

   public PartialBlockTag copy() {
      return new PartialBlockTag(this.id, this.properties.copy(), this.entity.copy());
   }

   @Override
   public String toString() {
      return (this.id != null ? "#" + this.id : "") + this.properties.toString() + this.entity.toString();
   }

   public static Optional<PartialBlockTag> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialBlockTag parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialBlockTag parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '#') {
         throw new IllegalArgumentException("Invalid block tag '" + reader.getString() + "' does not start with #");
      } else {
         reader.skip();
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(new ResourceLocation(string), PartialBlockProperties.parse(reader), PartialCompoundNbt.parse(reader));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid tag identifier '" + string + "' in block tag '" + reader.getString() + "'");
         }
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
