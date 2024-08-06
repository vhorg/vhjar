package iskallia.vault.task.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EntityTaskSource extends TaskSource {
   private RandomSource random;
   private final Set<UUID> uuids;

   protected EntityTaskSource(RandomSource random) {
      this.random = random;
      this.uuids = new HashSet<>();
   }

   public static EntityTaskSource empty() {
      return new EntityTaskSource(JavaRandom.ofNanoTime());
   }

   public static EntityTaskSource ofUuids(RandomSource random, UUID... uuids) {
      return new EntityTaskSource(random).add(uuids);
   }

   public static TaskSource ofEntities(RandomSource random, Entity... entities) {
      return new EntityTaskSource(random).add(entities);
   }

   public EntityTaskSource add(UUID... uuids) {
      this.uuids.addAll(Arrays.asList(uuids));
      return this;
   }

   public TaskSource add(Entity... entities) {
      for (Entity entity : entities) {
         this.uuids.add(entity.getUUID());
      }

      return this;
   }

   public EntityTaskSource remove(UUID... uuids) {
      Arrays.asList(uuids).forEach(this.uuids::remove);
      return this;
   }

   public TaskSource remove(Entity... entities) {
      for (Entity entity : entities) {
         this.uuids.remove(entity.getUUID());
      }

      return this;
   }

   @Override
   public RandomSource getRandom() {
      return this.random;
   }

   public int getCount() {
      return this.uuids.size();
   }

   public Set<UUID> getUuids() {
      return this.uuids;
   }

   public <T extends Entity> Set<T> getEntities(Class<T> filter) {
      return this.getEntities(ServerLifecycleHooks.getCurrentServer(), filter);
   }

   public <T extends Entity> Set<T> getEntities(MinecraftServer server, Class<T> filter) {
      return this.uuids.stream().map(uuid -> {
         ServerPlayer player = server.getPlayerList().getPlayer(uuid);
         if (player != null && filter.isAssignableFrom(player.getClass())) {
            return player;
         } else {
            for (ServerLevel world : server.getAllLevels()) {
               Entity entity = world.getEntity(uuid);
               if (entity != null && filter.isAssignableFrom(entity.getClass())) {
                  return entity;
               }
            }

            return null;
         }
      }).filter(Objects::nonNull).collect(Collectors.toSet());
   }

   public boolean matches(UUID uuid) {
      return this.uuids.contains(uuid);
   }

   public boolean matches(Entity entity) {
      return this.uuids.contains(entity.getUUID());
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.RANDOM.writeBits(this.random, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.uuids.size()), buffer);

      for (UUID uuid : this.uuids) {
         Adapters.UUID.writeBits(uuid, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.random = Adapters.RANDOM.readBits(buffer).orElseThrow();
      this.uuids.clear();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         this.uuids.add(Adapters.UUID.readBits(buffer).orElseThrow());
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         nbt.put("random", Adapters.RANDOM.writeNbt(this.random).orElseThrow());
         ListTag uuids = new ListTag();

         for (UUID uuid : this.uuids) {
            Adapters.UUID.writeNbt(uuid).ifPresent(uuids::add);
         }

         nbt.put("uuids", uuids);
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.random = Adapters.RANDOM.readNbt(nbt.get("random")).orElseThrow();
      this.uuids.clear();
      ListTag uuids = (ListTag)nbt.get("uuids");

      for (int i = 0; i < (uuids == null ? 0 : uuids.size()); i++) {
         Adapters.UUID.readNbt(uuids.get(i)).ifPresent(this.uuids::add);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         json.add("random", Adapters.RANDOM.writeJson(this.random).orElseThrow());
         JsonArray uuids = new JsonArray();

         for (UUID uuid : this.uuids) {
            Adapters.UUID.writeJson(uuid).ifPresent(uuids::add);
         }

         json.add("uuids", uuids);
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.random = Adapters.RANDOM.readJson(json.get("random")).orElseThrow();
      this.uuids.clear();
      JsonArray uuids = json.getAsJsonArray("uuids");

      for (int i = 0; i < (uuids == null ? 0 : uuids.size()); i++) {
         Adapters.UUID.readJson(uuids.get(i)).ifPresent(this.uuids::add);
      }
   }
}
