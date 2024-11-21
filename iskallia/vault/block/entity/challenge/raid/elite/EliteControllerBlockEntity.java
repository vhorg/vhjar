package iskallia.vault.block.entity.challenge.raid.elite;

import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.elite.EliteChallengeManager;
import iskallia.vault.block.entity.challenge.raid.ChallengeActionEntry;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.processor.tile.RotateTileProcessor;
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

public class EliteControllerBlockEntity extends ChallengeControllerBlockEntity<EliteChallengeManager> {
   private BlockCuboid zone;
   private final Map<List<BlockPos>, List<ChallengeActionEntry>> proxies;
   private final WeightedList<PartialEntity> elite;
   public static ArrayAdapter<ChallengeActionEntry> ENTRIES = Adapters.ofArray(ChallengeActionEntry[]::new, Adapters.of(ChallengeActionEntry::new, false));

   public EliteControllerBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ELITE_CONTROLLER_TILE_ENTITY, pos, state);
      this.setRenderer(4351733, 4351733, 4351733);
      this.proxies = new LinkedHashMap<>();
      this.zone = null;
      this.elite = new WeightedList<>();
   }

   public EliteChallengeManager createManager() {
      ChunkRandom random = ChunkRandom.any();
      random.setDecoratorSeed(
         ServerVaults.get(this.level).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.getBlockPos().getX(), this.getBlockPos().getZ(), 329045113
      );
      EliteChallengeManager manager = new EliteChallengeManager(this.uuid, this.level.dimension(), this.getBlockPos());
      manager.setZone(this.zone.rotate(this.getRotation(), BlockPos.ZERO, true).offset(this.getBlockPos()));
      this.proxies
         .forEach(
            (offsets, entries) -> {
               for (BlockPos offset : offsets) {
                  RotateTileProcessor processor = new RotateTileProcessor(this.getRotation(), 0, 0, true);
                  offset = processor.transform(offset);
                  manager.getProxies()
                     .put(
                        offset,
                        new EliteChallengeManager.Proxy(
                           (List<ChallengeActionEntry>)entries,
                           this.elite.getRandom(random).orElseGet(() -> PartialEntity.parse("minecraft:pig", true).orElseThrow())
                        )
                     );
               }
            }
         );
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

         ENTRIES.writeNbt(entries.toArray(ChallengeActionEntry[]::new)).ifPresent(tag -> entry.put("entries", tag));
         proxies.add(entry);
      });
      nbt.put("proxies", proxies);
      ListTag boss = new ListTag();
      this.elite.forEach((entity, weight) -> Adapters.PARTIAL_ENTITY.writeNbt(entity).ifPresent(tag -> {
         if (tag instanceof CompoundTag compound) {
            Adapters.DOUBLE.writeNbt(weight).ifPresent(tag1 -> compound.put("weight", tag1));
            boss.add(tag);
         }
      }));
      nbt.put("elite", boss);
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

      ListTag boss = nbt.getList("elite", 10);
      this.elite.clear();

      for (int i = 0; i < boss.size(); i++) {
         double weight = Adapters.DOUBLE.readNbt(boss.getCompound(i).get("weight")).orElse(1.0);
         Adapters.PARTIAL_ENTITY.readNbt(boss.getCompound(i)).ifPresent(entity -> this.elite.put(entity, weight));
      }
   }
}
