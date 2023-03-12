package iskallia.vault.core.vault.objective.elixir;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.RegistryValueAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.entity.ElixirOrbEntity;
import iskallia.vault.init.ModConfigs;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public abstract class ElixirTask extends DataObject<ElixirTask> implements ISupplierKey<ElixirTask> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> ELIXIR = FieldKey.of("elixir", Integer.class)
      .with(Version.v1_12, Adapters.INT_SEGMENTED_3, DISK.all())
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void summonOrbs(VirtualWorld world, Vec3 pos, int amount) {
      int size = (amount < 0 ? -1 : 1) * ModConfigs.ELIXIR.getSize(Math.abs(amount));

      for (int i = 0; i < Math.abs(size) / 2 + 3; i++) {
         ElixirOrbEntity entity = new ElixirOrbEntity(world, pos.x, pos.y, pos.z, size, 80);
         world.addFreshEntity(entity);
      }

      world.playSound(null, new BlockPos(pos), SoundEvents.HONEY_BLOCK_BREAK, SoundSource.BLOCKS, 0.4F, 0.7F);
   }

   public void summonOrbs(VirtualWorld world, BlockPos pos, int amount) {
      this.summonOrbs(world, new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), amount);
   }

   public abstract void initServer(VirtualWorld var1, Vault var2, ElixirObjective var3, UUID var4);

   public abstract void releaseServer();

   public abstract static class Config<T extends ElixirTask> {
      @Expose
      protected WeightedList<IntRoll> rolls;

      public Config(WeightedList<IntRoll> rolls) {
         this.rolls = rolls;
      }

      protected abstract T create();

      protected T configure(T task, RandomSource random) {
         this.rolls.getRandom(random).ifPresent(roll -> task.set(ElixirTask.ELIXIR, Integer.valueOf(roll.get(random))));
         return task;
      }

      public T generate(RandomSource random) {
         T task = this.create();
         this.configure(task, random);
         return task;
      }

      public static class Serializer implements JsonSerializer<ElixirTask.Config>, JsonDeserializer<ElixirTask.Config> {
         public static final ElixirTask.Config.Serializer INSTANCE = new ElixirTask.Config.Serializer();

         public ElixirTask.Config deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            String var4 = json.getAsJsonObject().get("type").getAsString();

            return switch (var4) {
               case "chest" -> (ElixirTask.Config)context.deserialize(json, ChestElixirTask.Config.class);
               case "coin_stacks" -> (ElixirTask.Config)context.deserialize(json, CoinStacksElixirTask.Config.class);
               case "ore" -> (ElixirTask.Config)context.deserialize(json, OreElixirTask.Config.class);
               case "mob" -> (ElixirTask.Config)context.deserialize(json, MobElixirTask.Config.class);
               default -> null;
            };
         }

         public JsonElement serialize(ElixirTask.Config value, Type type, JsonSerializationContext context) {
            JsonObject result = context.serialize(value, value.getClass()).getAsJsonObject();
            if (value instanceof ChestElixirTask.Config) {
               result.addProperty("type", "chest");
            }

            if (value instanceof CoinStacksElixirTask.Config) {
               result.addProperty("type", "coin_stacks");
            }

            if (value instanceof OreElixirTask.Config) {
               result.addProperty("type", "ore");
            }

            if (value instanceof MobElixirTask.Config) {
               result.addProperty("type", "mob");
            }

            return result;
         }
      }
   }

   public static class List extends DataList<ElixirTask.List, ElixirTask> {
      public List() {
         super(new ArrayList<>(), RegistryValueAdapter.of(() -> VaultRegistry.ELIXIR_TASK, ISupplierKey::getKey, Supplier::get));
      }
   }
}
