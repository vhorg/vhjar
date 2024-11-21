package iskallia.vault.core.world.generator.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class ArchitectRoomEntry extends DataObject<ArchitectRoomEntry> implements INBTSerializable<CompoundTag> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<ArchitectRoomEntry.Type> TYPE = FieldKey.of("type", ArchitectRoomEntry.Type.class)
      .with(Version.v1_0, Adapters.ofEnum(ArchitectRoomEntry.Type.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> POOL = FieldKey.of("pool", ResourceLocation.class)
      .with(Version.v1_0, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class).with(Version.v1_0, Adapters.INT_SEGMENTED_7, DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> COLOR = FieldKey.of("color", Integer.class).with(Version.v1_0, Adapters.INT, DISK.all()).register(FIELDS);

   private ArchitectRoomEntry() {
   }

   public static ArchitectRoomEntry ofType(ArchitectRoomEntry.Type type, int count) {
      return new ArchitectRoomEntry().set(TYPE, type).set(COUNT, Integer.valueOf(count));
   }

   public static ArchitectRoomEntry ofPool(ResourceLocation pool, int count) {
      return new ArchitectRoomEntry().set(POOL, pool).set(COUNT, Integer.valueOf(count));
   }

   public static ArchitectRoomEntry fromNBT(CompoundTag nbt) {
      ArchitectRoomEntry entry = new ArchitectRoomEntry();
      entry.deserializeNBT(nbt);
      return entry;
   }

   public static ArchitectRoomEntry fromJson(JsonObject object) {
      ArchitectRoomEntry entry = new ArchitectRoomEntry();
      entry.deserializeJson(object);
      return entry;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public Component getName() {
      String name;
      if (this.has(TYPE)) {
         name = this.get(TYPE).getName();
      } else {
         TemplatePoolKey pool = VaultRegistry.TEMPLATE_POOL.getKey(this.get(POOL));
         name = pool == null ? "Unknown" : pool.getName();
      }

      return new TextComponent(name).withStyle(Style.EMPTY.withColor(this.getOr(COLOR, Integer.valueOf(16777215))));
   }

   public void mergeWith(ArchitectRoomEntry other) {
      if (this.has(TYPE) && other.has(TYPE) && this.get(TYPE) == other.get(TYPE)) {
         this.modify(COUNT, i -> i + other.get(COUNT));
         other.set(COUNT, Integer.valueOf(0));
      } else if (this.has(POOL) && other.has(POOL) && this.get(POOL).equals(other.get(POOL))) {
         this.modify(COUNT, i -> i + other.get(COUNT));
         other.set(COUNT, Integer.valueOf(0));
      }
   }

   public TemplatePoolKey getPool(VaultGridLayout layout) {
      if (this.has(TYPE)) {
         if (layout instanceof ArchitectVaultLayout) {
            return switch ((ArchitectRoomEntry.Type)this.get(TYPE)) {
               case COMMON -> (TemplatePoolKey)layout.get(ArchitectVaultLayout.COMMON_ROOM_POOL);
               case CHALLENGE -> (TemplatePoolKey)layout.get(ArchitectVaultLayout.CHALLENGE_ROOM_POOL);
               case OMEGA -> (TemplatePoolKey)layout.get(ArchitectVaultLayout.OMEGA_ROOM_POOL);
            };
         }

         if (layout instanceof ClassicVaultLayout) {
            switch ((ArchitectRoomEntry.Type)this.get(TYPE)) {
               case COMMON:
               case CHALLENGE:
               case OMEGA:
                  return layout.get(ClassicVaultLayout.ROOM_POOL);
               default:
                  throw new IncompatibleClassChangeError();
            }
         }
      }

      return VaultRegistry.TEMPLATE_POOL.getKey(this.get(POOL));
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
         this.set(TYPE, Enum.valueOf(ArchitectRoomEntry.Type.class, nbt.getString("type")));
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
         this.set(TYPE, Enum.valueOf(ArchitectRoomEntry.Type.class, object.get("type").getAsString()));
      }

      if (object.has("pool")) {
         this.set(POOL, new ResourceLocation(object.get("pool").getAsString()));
      }

      this.set(COUNT, Integer.valueOf(object.get("count").getAsInt()));
      if (object.has("color")) {
         this.set(COLOR, Integer.valueOf(object.get("color").getAsInt()));
      }
   }

   public static class List extends DataList<ArchitectRoomEntry.List, ArchitectRoomEntry> {
      public List() {
         super(new ArrayList<>(), CompoundAdapter.of(ArchitectRoomEntry::new));
      }

      public java.util.List<TemplatePoolKey> flatten(VaultGridLayout layout) {
         java.util.List<TemplatePoolKey> result = new ArrayList<>();

         for (ArchitectRoomEntry entry : this) {
            TemplatePoolKey pool = entry.getPool(layout);
            if (pool != null) {
               for (int i = 0; i < entry.get(ArchitectRoomEntry.COUNT); i++) {
                  result.add(pool);
               }
            }
         }

         return result;
      }

      public int getTotalCount() {
         int total = 0;

         for (ArchitectRoomEntry entry : this) {
            total += entry.get(ArchitectRoomEntry.COUNT);
         }

         return total;
      }
   }

   public static enum Type {
      COMMON,
      CHALLENGE,
      OMEGA;

      public String getName() {
         return switch (this) {
            case COMMON -> "Common";
            case CHALLENGE -> "Challenge";
            case OMEGA -> "Omega";
         };
      }
   }
}
