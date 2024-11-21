package iskallia.vault.block.entity.challenge.raid;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

public class RaidControllerBlockEntity extends ChallengeControllerBlockEntity<RaidChallengeManager> {
   private BlockCuboid zone;
   private final Map<List<BlockPos>, List<RaidActionEntry>> proxies;
   private final List<RaidSpawner.Block> spawners;
   private IntRoll waves;
   private final List<ChallengeAction<?>> actions;
   public static ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);
   public static ArrayAdapter<RaidActionEntry> ENTRIES = Adapters.ofArray(RaidActionEntry[]::new, Adapters.of(RaidActionEntry::new, false));

   public RaidControllerBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.RAID_CONTROLLER_TILE_ENTITY, pos, state);
      this.setRenderer(13369344, 13369344, 13369344);
      this.zone = null;
      this.proxies = new LinkedHashMap<>();
      this.spawners = new ArrayList<>();
      this.waves = IntRoll.ofConstant(2);
      this.actions = new ArrayList<>();
   }

   public RaidChallengeManager createManager() {
      ChunkRandom random = ChunkRandom.any();
      random.setDecoratorSeed(
         ServerVaults.get(this.level).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.getBlockPos().getX(), this.getBlockPos().getZ(), 329045113
      );
      RaidChallengeManager manager = new RaidChallengeManager(this.uuid, this.level.dimension(), this.getBlockPos());
      this.proxies.forEach((positions, entries) -> positions.forEach(position -> manager.getProxies().put(position, entries)));
      manager.setZone(this.zone);
      manager.getSpawner().getBlocks().addAll(this.spawners);
      manager.getSpawner().setTarget(this.waves.get(random));
      manager.addActions(this.actions);
      return manager;
   }

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
      ListTag proxies = new ListTag();
      this.proxies.forEach((pos, entries) -> {
         CompoundTag entry = new CompoundTag();
         if (pos.size() == 1) {
            Adapters.BLOCK_POS.writeNbt(pos.get(0)).ifPresent(tag -> entry.put("pos", tag));
         } else {
            ListTag positions = new ListTag();

            for (BlockPos p : pos) {
               Adapters.BLOCK_POS.writeNbt(p).ifPresent(positions::add);
            }

            entry.put("pos", positions);
         }

         ENTRIES.writeNbt(entries.toArray(RaidActionEntry[]::new)).ifPresent(tag -> entry.put("entries", tag));
         proxies.add(entry);
      });
      nbt.put("proxies", proxies);
      ListTag spawners = new ListTag();

      for (RaidSpawner.Block spawner : this.spawners) {
         spawner.writeNbt().ifPresent(spawners::add);
      }

      nbt.put("spawners", spawners);
      Adapters.INT_ROLL.writeNbt(this.waves).ifPresent(roll -> nbt.put("waves", roll));
      ACTIONS.writeNbt(this.actions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("actions", tag));
   }

   @Override
   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      ListTag proxies = nbt.getList("proxies", 10);
      this.proxies.clear();

      for (int i = 0; i < proxies.size(); i++) {
         CompoundTag entry = proxies.getCompound(i);
         List<BlockPos> positions = new ArrayList<>();
         if (entry.get("pos") instanceof ListTag list && (list.getElementType() == 9 || list.getElementType() == 4)) {
            for (Tag tag : list) {
               Adapters.BLOCK_POS.readNbt(tag).ifPresent(positions::add);
            }
         } else {
            Adapters.BLOCK_POS.readNbt(entry.get("pos")).ifPresent(positions::add);
         }

         this.proxies.put(positions, Arrays.stream(ENTRIES.readNbt(entry.get("entries")).orElseThrow()).collect(Collectors.toList()));
      }

      ListTag blocks = nbt.getList("spawners", 10);
      this.spawners.clear();

      for (int i = 0; i < blocks.size(); i++) {
         RaidSpawner.Block block = new RaidSpawner.Block();
         block.readNbt(blocks.getCompound(i));
         this.spawners.add(block);
      }

      this.waves = Adapters.INT_ROLL.readNbt(nbt.get("waves")).orElse(IntRoll.ofConstant(-1));
      this.actions.clear();
      ACTIONS.readNbt(nbt.get("actions")).ifPresent(actions -> this.actions.addAll(Arrays.asList(actions)));
   }
}
