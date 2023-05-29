package iskallia.vault.core.vault.objective.elixir;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;
import net.minecraftforge.eventbus.api.EventPriority;

public class OreElixirTask extends ElixirTask {
   public static final SupplierKey<ElixirTask> KEY = SupplierKey.of("ore", ElixirTask.class).with(Version.v1_12, OreElixirTask::new);

   protected OreElixirTask() {
   }

   public OreElixirTask(int elixir) {
      this.set(ELIXIR, Integer.valueOf(elixir));
   }

   @Override
   public SupplierKey<ElixirTask> getKey() {
      return KEY;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      if (this.get(ELIXIR) != 0) {
         CommonEvents.PLAYER_MINE.register(this, EventPriority.LOW, data -> {
            if (data.getPlayer().level == world) {
               if (data.getPlayer().getUUID().equals(listener)) {
                  if (data.getState().getBlock() instanceof VaultOreBlock) {
                     if ((Boolean)data.getState().getValue(VaultOreBlock.GENERATED)) {
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
      CommonEvents.PLAYER_MINE.release(this);
   }

   public static class Config extends ElixirTask.Config<OreElixirTask> {
      public Config(WeightedList<IntRoll> rolls) {
         super(rolls);
      }

      protected OreElixirTask create() {
         return new OreElixirTask();
      }
   }
}
