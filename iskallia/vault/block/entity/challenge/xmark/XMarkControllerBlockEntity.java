package iskallia.vault.block.entity.challenge.xmark;

import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;

public class XMarkControllerBlockEntity extends ChallengeControllerBlockEntity<XMarkChallengeManager> {
   private BlockCuboid zone;
   private final List<BlockPos> spawners;
   private double trapChance;
   private IntRoll waves;
   private int spawnDelay;
   private final List<ChallengeAction<?>> actions;
   public static ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);

   public XMarkControllerBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.X_MARK_CONTROLLER_TILE_ENTITY, pos, state);
      this.setRenderer(9470726, 9470726, 9470726);
      this.zone = null;
      this.spawners = new ArrayList<>();
      this.trapChance = 0.5;
      this.waves = IntRoll.ofConstant(2);
      this.spawnDelay = 100;
      this.actions = new ArrayList<>();
   }

   public XMarkChallengeManager createManager() {
      ChunkRandom random = ChunkRandom.any();
      random.setDecoratorSeed(
         ServerVaults.get(this.level).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.getBlockPos().getX(), this.getBlockPos().getZ(), 329045113
      );
      XMarkChallengeManager manager = new XMarkChallengeManager(this.uuid, this.level.dimension(), this.getBlockPos());
      manager.setZone(this.zone);
      manager.setTrapChance(this.trapChance);
      manager.getSpawner().add(this.spawners);
      manager.getSpawner().setWaveTarget(this.waves.get(random));
      manager.getSpawner().setSpawnDelay(this.spawnDelay);
      manager.addActions(this.actions);
      return manager;
   }

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
      ListTag spawners = new ListTag();

      for (BlockPos spawner : this.spawners) {
         Adapters.BLOCK_POS.writeNbt(spawner).ifPresent(spawners::add);
      }

      nbt.put("spawners", spawners);
      Adapters.DOUBLE.writeNbt(Double.valueOf(this.trapChance)).ifPresent(tag -> nbt.put("trapChance", tag));
      Adapters.INT_ROLL.writeNbt(this.waves).ifPresent(tag -> nbt.put("waves", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.spawnDelay)).ifPresent(tag -> nbt.put("spawnDelay", tag));
      ACTIONS.writeNbt(this.actions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("actions", tag));
   }

   @Override
   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.spawners.clear();
      if (nbt.get("spawners") instanceof ListTag spawners) {
         for (int i = 0; i < spawners.size(); i++) {
            Adapters.BLOCK_POS.readNbt(spawners.get(i)).ifPresent(this.spawners::add);
         }
      }

      this.trapChance = Adapters.DOUBLE.readNbt(nbt.get("trapChance")).orElse(0.0);
      this.waves = Adapters.INT_ROLL.readNbt(nbt.get("waves")).orElse(IntRoll.ofConstant(2));
      this.spawnDelay = Adapters.INT.readNbt(nbt.get("spawnDelay")).orElse(100);
      this.actions.clear();
      ACTIONS.readNbt(nbt.get("actions")).ifPresent(actions -> this.actions.addAll(Arrays.asList(actions)));
   }
}
