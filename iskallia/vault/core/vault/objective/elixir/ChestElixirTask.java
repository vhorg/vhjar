package iskallia.vault.core.vault.objective.elixir;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;

public class ChestElixirTask extends ElixirTask {
   public static final SupplierKey<ElixirTask> KEY = SupplierKey.of("chest", ElixirTask.class).with(Version.v1_12, ChestElixirTask::new);
   public static final FieldRegistry FIELDS = ElixirTask.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<VaultChestType> TYPE = FieldKey.of("type", VaultChestType.class)
      .with(Version.v1_12, Adapters.ofEnum(VaultChestType.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);

   protected ChestElixirTask() {
   }

   public ChestElixirTask(VaultChestType type, int elixir) {
      this.set(TYPE, type);
      this.set(ELIXIR, Integer.valueOf(elixir));
   }

   @Override
   public SupplierKey<ElixirTask> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      if (this.get(ELIXIR) != 0) {
         CommonEvents.CHEST_LOOT_GENERATION.post().register(this, data -> {
            if (data.getPlayer().level == world) {
               if (data.getPlayer().getUUID().equals(listener)) {
                  if (data.getState().getBlock() instanceof VaultChestBlock chest) {
                     if (chest.getType() == this.get(TYPE)) {
                        this.summonOrbs(world, data.getPos(), this.get(ELIXIR));
                        objective.addProgress(listener, this.get(ELIXIR));
                     }
                  }
               }
            }
         });
      }
   }

   @Override
   public void releaseServer() {
      CommonEvents.CHEST_LOOT_GENERATION.release(this);
   }

   public static class Config extends ElixirTask.Config<ChestElixirTask> {
      @Expose
      @SerializedName("chest_type")
      protected VaultChestType type;

      public Config(WeightedList<IntRoll> rolls, VaultChestType type) {
         super(rolls);
         this.type = type;
      }

      protected ChestElixirTask create() {
         return new ChestElixirTask();
      }

      protected ChestElixirTask configure(ChestElixirTask task, RandomSource random) {
         task.set(ChestElixirTask.TYPE, this.type);
         return super.configure(task, random);
      }
   }
}
