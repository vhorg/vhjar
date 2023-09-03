package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.world.data.ServerVaults;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class PylonBuff<C extends PylonBuff.Config<?>> implements INBTSerializable<CompoundTag> {
   protected UUID uuid = UUID.randomUUID();
   protected UUID playerUuid;
   protected UUID vaultUuid;
   protected C config;

   public PylonBuff(C config) {
      this.config = config;
   }

   public C getConfig() {
      return this.config;
   }

   public void setPlayer(Player player) {
      this.playerUuid = player.getUUID();
   }

   public void setVault(Vault vault) {
      this.vaultUuid = vault.get(Vault.ID);
   }

   public boolean isDone() {
      return this.vaultUuid != null
         ? ServerVaults.get(this.vaultUuid).map(vault -> !vault.get(Vault.LISTENERS).contains(this.playerUuid)).orElse(false)
         : false;
   }

   public void initServer(MinecraftServer server) {
   }

   public void releaseServer() {
   }

   public void onAdd(MinecraftServer server) {
   }

   public void onTick(MinecraftServer server) {
   }

   public void onRemove(MinecraftServer server) {
   }

   public Optional<ServerPlayer> getPlayer(MinecraftServer server) {
      return Optional.ofNullable(server.getPlayerList().getPlayer(this.playerUuid));
   }

   public void write(CompoundTag object) {
      object.putString("uuid", this.uuid.toString());
      object.putString("playerUuid", this.playerUuid.toString());
      if (this.vaultUuid != null) {
         object.putString("vaultUuid", this.vaultUuid.toString());
      }

      object.put("config", this.config.serializeNBT());
   }

   public void read(CompoundTag object) {
      this.uuid = UUID.fromString(object.getString("uuid"));
      this.playerUuid = UUID.fromString(object.getString("playerUuid"));
      if (object.contains("vaultUuid")) {
         this.vaultUuid = UUID.fromString(object.getString("vaultUuid"));
      }
   }

   public final CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      this.write(nbt);
      return nbt;
   }

   public final void deserializeNBT(CompoundTag nbt) {
      this.read(nbt);
   }

   public static PylonBuff<?> fromNBT(CompoundTag nbt) {
      return PylonBuff.Config.fromNBT(nbt.getCompound("config")).build(nbt);
   }

   public abstract static class Config<B extends PylonBuff<?>> implements INBTSerializable<CompoundTag> {
      protected boolean uber;
      protected int color;
      protected int uberColor;
      protected String description;

      public int getDuration() {
         return 0;
      }

      public int getColor() {
         return this.color;
      }

      public int getUberColor() {
         return this.uberColor;
      }

      public boolean getUber() {
         return this.uber;
      }

      public String getDescription() {
         return this.description;
      }

      public abstract B build();

      public B build(CompoundTag nbt) {
         B buff = this.build();
         buff.deserializeNBT(nbt);
         return buff;
      }

      protected abstract void write(JsonObject var1);

      protected abstract void read(JsonObject var1);

      protected abstract void write(CompoundTag var1);

      protected abstract void read(CompoundTag var1);

      public final JsonObject serializeJson() {
         JsonObject json = new JsonObject();
         json.addProperty("uber", this.uber);
         json.addProperty("color", this.color);
         json.addProperty("uberColor", this.color);
         json.addProperty("description", this.description);
         this.write(json);
         return json;
      }

      public final void deserializeJson(JsonObject json) {
         this.uber = json.get("uber").getAsBoolean();
         this.color = json.get("color").getAsInt();
         if (json.has("uberColor")) {
            this.uberColor = json.get("uberColor").getAsInt();
         } else {
            this.uberColor = this.color;
         }

         this.description = json.get("description").getAsString();
         this.read(json);
      }

      public final CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putBoolean("uber", this.uber);
         nbt.putString("color", Long.toHexString(Integer.toUnsignedLong(this.color)));
         nbt.putString("uberColor", Long.toHexString(Integer.toUnsignedLong(this.uberColor)));
         nbt.putString("description", this.description);
         this.write(nbt);
         return nbt;
      }

      public final void deserializeNBT(CompoundTag nbt) {
         this.uber = nbt.getBoolean("uber");
         this.color = (int)Long.parseLong(nbt.getString("color"), 16);
         if (nbt.contains("uberColor")) {
            this.uberColor = (int)Long.parseLong(nbt.getString("uberColor"), 16);
         } else {
            this.uberColor = this.color;
         }

         this.description = nbt.getString("description");
         this.read(nbt);
      }

      public static PylonBuff.Config<?> fromNBT(CompoundTag nbt) {
         PylonBuff.Config<?> config = PylonBuff.Serializer.REGISTRY.get(nbt.getString("type")).get();
         config.deserializeNBT(nbt);
         return config;
      }
   }

   public static class Serializer implements JsonSerializer<PylonBuff.Config<?>>, JsonDeserializer<PylonBuff.Config<?>> {
      private static final Map<String, Supplier<PylonBuff.Config<?>>> REGISTRY = new HashMap<>();

      public JsonElement serialize(PylonBuff.Config<?> value, Type type, JsonSerializationContext context) {
         return value.serializeJson();
      }

      public PylonBuff.Config<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
         String key = json.getAsJsonObject().get("type").getAsString();
         PylonBuff.Config<?> config = REGISTRY.get(key).get();
         config.deserializeJson(json.getAsJsonObject());
         return config;
      }

      static {
         REGISTRY.put("effect", EffectPylonBuff.Config::new);
         REGISTRY.put("mana", ManaPylonBuff.Config::new);
         REGISTRY.put("time", TimePylonBuff.Config::new);
         REGISTRY.put("attribute", AttributePylonBuff.Config::new);
         REGISTRY.put("stat", StatPylonBuff.Config::new);
         REGISTRY.put("potion", PotionPylonBuff.Config::new);
      }
   }
}
