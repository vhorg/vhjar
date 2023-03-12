package iskallia.vault.core.world.data;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class TileParser {
   public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType(
      new TranslatableComponent("argument.block.tag.disallowed")
   );
   public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(
      block -> new TranslatableComponent("argument.block.id.invalid", new Object[]{block})
   );
   public static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
      (block, property) -> new TranslatableComponent("argument.block.property.unknown", new Object[]{block, property})
   );
   public static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
      (block, property) -> new TranslatableComponent("argument.block.property.duplicate", new Object[]{property, block})
   );
   public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType(
      (block, property, value) -> new TranslatableComponent("argument.block.property.invalid", new Object[]{block, value, property})
   );
   public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType(
      (block, property) -> new TranslatableComponent("argument.block.property.novalue", new Object[]{block, property})
   );
   public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType(
      new TranslatableComponent("argument.block.property.unclosed")
   );
   private static final SimpleCommandExceptionType IDENTIFIER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid"));
   private final StringReader reader;
   private final boolean allowTag;
   private final Block fallback;
   private ResourceLocation blockId = new ResourceLocation("");
   private StateDefinition<Block, BlockState> stateFactory;
   private BlockState state;
   private PartialState partialState;
   private CompoundTag nbt;
   private TagKey<Block> tag;
   private final Map<Property<?>, Comparable<?>> blockProperties = Maps.newHashMap();
   private final Map<String, String> tagProperties = Maps.newHashMap();
   private boolean hasTag;
   private boolean hasBlock;
   private boolean hasState;
   private boolean hasNBT;

   public TileParser(StringReader reader, Block fallback, boolean allowTag) {
      this.reader = reader;
      this.fallback = fallback;
      this.allowTag = allowTag;
      this.parse();
   }

   public TagKey<Block> getTag() {
      return this.tag;
   }

   public Block getBlock() {
      return this.state.getBlock();
   }

   public BlockState getState() {
      return this.state;
   }

   public PartialState getPartialState() {
      return this.partialState;
   }

   public CompoundTag getNBT() {
      return this.nbt;
   }

   public PartialNBT getPartialNBT() {
      return PartialNBT.of(this.nbt);
   }

   public boolean hasTag() {
      return this.hasTag;
   }

   public boolean hasBlock() {
      return this.hasBlock;
   }

   public boolean hasState() {
      return this.hasState;
   }

   public boolean hasNBT() {
      return this.hasNBT;
   }

   protected TileParser parse() {
      try {
         if (this.reader.canRead() && this.reader.peek() == '#') {
            this.parseTag();
            if (this.reader.canRead() && this.reader.peek() == '[') {
               this.parseTagProperties();
            }
         } else {
            this.parseBlock();
            if (this.reader.canRead() && this.reader.peek() == '[') {
               this.parseState();
            }
         }

         if (this.reader.canRead() && this.reader.peek() == '{') {
            this.parseNBT();
         }
      } catch (CommandSyntaxException var2) {
         var2.printStackTrace();
      }

      return this;
   }

   public PartialTile toTile() {
      return new PartialTile(this.partialState, this.hasNBT ? PartialNBT.of(this.nbt) : PartialNBT.empty(), null);
   }

   public void parseTag() throws CommandSyntaxException {
      if (!this.allowTag) {
         throw DISALLOWED_TAG_EXCEPTION.create();
      } else {
         this.reader.expect('#');
         this.tag = TagKey.create(Registry.BLOCK_REGISTRY, readId(this.reader));
         this.hasTag = true;
      }
   }

   public void parseTagProperties() throws CommandSyntaxException {
      this.reader.skip();
      int i = -1;
      this.reader.skipWhitespace();

      while (this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int j = this.reader.getCursor();
         String string = this.reader.readString();
         if (this.tagProperties.containsKey(string)) {
            this.reader.setCursor(j);
            throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
         }

         this.reader.skipWhitespace();
         if (!this.reader.canRead() || this.reader.peek() != '=') {
            this.reader.setCursor(j);
            throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
         }

         this.reader.skip();
         this.reader.skipWhitespace();
         i = this.reader.getCursor();
         String string2 = this.reader.readString();
         this.tagProperties.put(string, string2);
         this.reader.skipWhitespace();
         if (this.reader.canRead()) {
            i = -1;
            if (this.reader.peek() != ',') {
               if (this.reader.peek() != ']') {
                  throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
               }
               break;
            }

            this.reader.skip();
         }
      }

      if (this.reader.canRead()) {
         this.reader.skip();
      } else {
         if (i >= 0) {
            this.reader.setCursor(i);
         }

         throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
      }
   }

   public void parseBlock() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      this.blockId = readId(this.reader);
      Block block = ForgeRegistries.BLOCKS.getHolder(this.blockId).<Block>map(Holder::value).orElseGet(() -> {
         this.reader.setCursor(i);
         INVALID_BLOCK_ID_EXCEPTION.createWithContext(this.reader, this.blockId.toString()).printStackTrace();
         return this.fallback;
      });
      if (block != null) {
         this.stateFactory = block.getStateDefinition();
         this.state = block.defaultBlockState();
         this.partialState = PartialState.of(block);
         this.hasBlock = true;
      }
   }

   public void parseState() throws CommandSyntaxException {
      this.reader.skip();
      this.reader.skipWhitespace();

      while (this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int i = this.reader.getCursor();
         String string = this.reader.readString();
         Property<?> property = this.stateFactory.getProperty(string);
         if (property == null) {
            this.reader.setCursor(i);
            throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
         }

         if (this.blockProperties.containsKey(property)) {
            this.reader.setCursor(i);
            throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
         }

         this.reader.skipWhitespace();
         if (!this.reader.canRead() || this.reader.peek() != '=') {
            throw EMPTY_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), string);
         }

         this.reader.skip();
         this.reader.skipWhitespace();
         int j = this.reader.getCursor();
         this.parsePropertyValue(property, this.reader.readString(), j);
         this.reader.skipWhitespace();
         if (this.reader.canRead()) {
            if (this.reader.peek() != ',') {
               if (this.reader.peek() != ']') {
                  throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
               }
               break;
            }

            this.reader.skip();
         }
      }

      this.hasState = true;
      if (this.reader.canRead()) {
         this.reader.skip();
      } else {
         throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(this.reader);
      }
   }

   private <T extends Comparable<T>> void parsePropertyValue(Property<T> property, String value, int cursor) throws CommandSyntaxException {
      Optional<T> optional = property.getValue(value);
      if (optional.isPresent()) {
         this.state = (BlockState)this.state.setValue(property, optional.get());
         this.partialState = this.partialState.with(property, optional.get());
         this.blockProperties.put(property, optional.get());
      } else {
         this.reader.setCursor(cursor);
         throw INVALID_PROPERTY_EXCEPTION.createWithContext(this.reader, this.blockId.toString(), property.getName(), value);
      }
   }

   public void parseNBT() throws CommandSyntaxException {
      this.nbt = new TagParser(this.reader).readStruct();
      this.hasNBT = true;
   }

   public static ResourceLocation readId(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();

      while (reader.canRead() && isCharValid(reader.peek())) {
         reader.skip();
      }

      String string = reader.getString().substring(i, reader.getCursor());

      try {
         return new ResourceLocation(string);
      } catch (ResourceLocationException var4) {
         reader.setCursor(i);
         throw IDENTIFIER_EXCEPTION.createWithContext(reader);
      }
   }

   public static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
