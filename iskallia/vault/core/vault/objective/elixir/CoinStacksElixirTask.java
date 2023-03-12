package iskallia.vault.core.vault.objective.elixir;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;

public class CoinStacksElixirTask extends ElixirTask {
   public static final SupplierKey<ElixirTask> KEY = SupplierKey.of("coin_stacks", ElixirTask.class).with(Version.v1_12, CoinStacksElixirTask::new);

   protected CoinStacksElixirTask() {
   }

   public CoinStacksElixirTask(int elixir) {
      this.set(ELIXIR, Integer.valueOf(elixir));
   }

   @Override
   public SupplierKey<ElixirTask> getKey() {
      return KEY;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      if (this.get(ELIXIR) != 0) {
         CommonEvents.COIN_STACK_LOOT_GENERATION.post().register(this, data -> {
            if (data.getPlayer().level == world) {
               if (data.getPlayer().getUUID().equals(listener)) {
                  this.summonOrbs(world, data.getPos(), this.get(ELIXIR));
                  objective.addProgress(listener, this.get(ELIXIR));
               }
            }
         });
      }
   }

   @Override
   public void releaseServer() {
      CommonEvents.COIN_STACK_LOOT_GENERATION.release(this);
   }

   public static class Config extends ElixirTask.Config<CoinStacksElixirTask> {
      public Config(WeightedList<IntRoll> rolls) {
         super(rolls);
      }

      protected CoinStacksElixirTask create() {
         return new CoinStacksElixirTask();
      }
   }
}
