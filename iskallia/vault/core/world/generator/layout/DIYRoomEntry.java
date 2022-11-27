package iskallia.vault.core.world.generator.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class DIYRoomEntry extends DataObject<DIYRoomEntry> implements INBTSerializable<CompoundTag> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<DIYRoomEntry.Type> TYPE = FieldKey.of("type", DIYRoomEntry.Type.class)
      .with(Version.v1_0, Adapter.ofEnum(DIYRoomEntry.Type.class), DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> POOL = FieldKey.of("pool", ResourceLocation.class)
      .with(Version.v1_0, Adapter.ofIdentifier(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class).with(Version.v1_0, Adapter.ofSegmentedInt(7), DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> COLOR = FieldKey.of("color", Integer.class).with(Version.v1_0, Adapter.ofInt(), DISK.all()).register(FIELDS);

   private DIYRoomEntry() {
   }

   public static DIYRoomEntry ofType(DIYRoomEntry.Type type, int count) {
      return new DIYRoomEntry().set(TYPE, type).set(COUNT, Integer.valueOf(count));
   }

   public static DIYRoomEntry ofPool(ResourceLocation pool, int count) {
      return new DIYRoomEntry().set(POOL, pool).set(COUNT, Integer.valueOf(count));
   }

   public static DIYRoomEntry fromNBT(CompoundTag nbt) {
      DIYRoomEntry entry = new DIYRoomEntry();
      entry.deserializeNBT(nbt);
      return entry;
   }

   public static DIYRoomEntry fromJson(JsonObject object) {
      DIYRoomEntry entry = new DIYRoomEntry();
      entry.deserializeJson(object);
      return entry;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public Component getName() {
      if (this.has(TYPE)) {
         return this.get(TYPE).getDisplay();
      } else {
         TemplatePoolKey pool = VaultRegistry.TEMPLATE_POOL.getKey(this.get(POOL));
         return (Component)(pool == null
            ? new TextComponent("UNKNOWN")
            : new TextComponent(pool.getName()).withStyle(Style.EMPTY.withColor(this.getOr(COLOR, Integer.valueOf(16777215)))));
      }
   }

   public void mergeWith(DIYRoomEntry other) {
      if (this.has(TYPE) && other.has(TYPE) && this.get(TYPE) == other.get(TYPE)) {
         this.modify(COUNT, i -> i + other.get(COUNT));
         other.set(COUNT, Integer.valueOf(0));
      } else if (this.has(POOL) && other.has(POOL) && this.get(POOL).equals(other.get(POOL))) {
         this.modify(COUNT, i -> i + other.get(COUNT));
         other.set(COUNT, Integer.valueOf(0));
      }
   }

   public TemplatePoolKey getPool(DIYVaultLayout layout) {
      if (this.has(TYPE)) {
         return switch ((DIYRoomEntry.Type)this.get(TYPE)) {
            case COMMON -> (TemplatePoolKey)layout.get(DIYVaultLayout.COMMON_ROOM_POOL);
            case CHALLENGE -> (TemplatePoolKey)layout.get(DIYVaultLayout.CHALLENGE_ROOM_POOL);
            case OMEGA -> (TemplatePoolKey)layout.get(DIYVaultLayout.OMEGA_ROOM_POOL);
         };
      } else {
         return VaultRegistry.TEMPLATE_POOL.getKey(this.get(POOL));
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      if (this.has(TYPE)) {
         nbt.putString("type", this.get(TYPE).name());
      }

      if (this.has(POOL)) {
         nbt.putString("pool", this.get(POOL).toString());
      }

      nbt.putInt("count", this.get(COUNT));
      if (this.has(COLOR)) {
         nbt.putInt("color", this.get(COLOR));
      }

      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      if (nbt.contains("type")) {
         this.set(TYPE, Enum.valueOf(DIYRoomEntry.Type.class, nbt.getString("type")));
      }

      if (nbt.contains("pool")) {
         this.set(POOL, new ResourceLocation(nbt.getString("pool")));
      }

      this.set(COUNT, Integer.valueOf(nbt.getInt("count")));
      if (nbt.contains("color", 3)) {
         this.set(COLOR, Integer.valueOf(nbt.getInt("color")));
      }
   }

   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      if (this.has(TYPE)) {
         object.addProperty("type", this.get(TYPE).name());
      }

      if (this.has(POOL)) {
         object.addProperty("pool", this.get(POOL).toString());
      }

      object.addProperty("count", this.get(COUNT));
      if (this.has(COLOR)) {
         object.addProperty("color", this.getOr(COLOR, Integer.valueOf(16777215)));
      }

      return object;
   }

   public void deserializeJson(JsonObject object) {
      if (object.has("type")) {
         this.set(TYPE, Enum.valueOf(DIYRoomEntry.Type.class, object.get("type").getAsString()));
      }

      if (object.has("pool")) {
         this.set(POOL, new ResourceLocation(object.get("pool").getAsString()));
      }

      this.set(COUNT, Integer.valueOf(object.get("count").getAsInt()));
      if (object.has("color")) {
         this.set(COLOR, Integer.valueOf(object.get("color").getAsInt()));
      }
   }

   public static class List extends DataList<DIYRoomEntry.List, DIYRoomEntry> {
      public List() {
         super(new ArrayList<>(), Adapter.ofCompound(DIYRoomEntry::new));
      }

      public java.util.List<TemplatePoolKey> flatten(DIYVaultLayout layout) {
         java.util.List<TemplatePoolKey> result = new ArrayList<>();

         for (DIYRoomEntry entry : this) {
            TemplatePoolKey pool = entry.getPool(layout);
            if (pool != null) {
               for (int i = 0; i < entry.get(DIYRoomEntry.COUNT); i++) {
                  result.add(pool);
               }
            }
         }

         return result;
      }

      public int getTotalCount() {
         int total = 0;

         for (DIYRoomEntry entry : this) {
            total += entry.get(DIYRoomEntry.COUNT);
         }

         return total;
      }
   }

   public static enum Type {
      COMMON,
      CHALLENGE,
      OMEGA;

      public Component getDisplay() {
         return switch (this) {
            case COMMON -> new TextComponent("Common").withStyle(ChatFormatting.WHITE);
            case CHALLENGE -> new TextComponent("Challenge").withStyle(ChatFormatting.LIGHT_PURPLE);
            case OMEGA -> new TextComponent("Omega").withStyle(ChatFormatting.GREEN);
         };
      }
   }
}
