package iskallia.vault.block.entity.challenge.raid;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

public class RaidSpawner implements ISerializable<CompoundTag, JsonObject> {
   private final List<RaidSpawner.Block> blocks = new ArrayList<>();
   private final List<RaidSpawner.Spawn> spawns = new ArrayList<>();
   private final Set<UUID> spawnedWave = new HashSet<>();
   private double currentHealth;
   private double totalHealth;
   private int count;
   private int target;
   private boolean trackSpawns;

   public List<RaidSpawner.Block> getBlocks() {
      return this.blocks;
   }

   public void setTarget(int target) {
      this.target = target;
   }

   public void addSpawn(PartialEntity entity, int count, String spawner) {
      this.spawns.add(new RaidSpawner.Spawn(entity, count, spawner));
   }

   public boolean isWaveCompleted() {
      return this.spawnedWave.isEmpty();
   }

   public double getCurrentHealth() {
      return this.currentHealth;
   }

   public double getTotalHealth() {
      return this.totalHealth;
   }

   public void onAttach(Level world, RaidChallengeManager manager) {
      CommonEvents.ENTITY_LEAVE.register(this, EventPriority.HIGHEST, event -> {
         if (event.getEntity().level == world) {
            this.spawnedWave.remove(event.getEntity().getUUID());
         }
      });
      CommonEvents.ENTITY_INITIALIZE.register(this, event -> {
         if (this.trackSpawns && event.getMob().level == world) {
            this.spawnedWave.add(event.getMob().getUUID());

            for (ChallengeAction<?> action : manager.getActions()) {
               action.onSummonMob(event.getMob());
            }
         }
      });
   }

   public void onDetach() {
      CommonEvents.ENTITY_LEAVE.release(this);
      CommonEvents.ENTITY_INITIALIZE.release(this);
   }

   public void onTick(ServerLevel world) {
      this.spawnedWave.removeIf(uuidx -> world.getEntity(uuidx) == null);
      double total = 0.0;
      this.currentHealth = 0.0;

      for (UUID uuid : this.spawnedWave) {
         if (world.getEntity(uuid) instanceof LivingEntity living) {
            this.currentHealth = this.currentHealth + living.getHealth();
            total += living.getMaxHealth();
         }
      }

      if (total > this.totalHealth) {
         this.totalHealth = total;
      }
   }

   public void onSpawn(ServerLevel world, BlockPos pos) {
      this.totalHealth = 0.0;
      JavaRandom random = JavaRandom.ofNanoTime();
      Map<BlockPos, List<RaidSpawner.Spawn>> spawnerToSpawns = new HashMap<>();

      for (RaidSpawner.Spawn spawn : this.spawns) {
         List<BlockPos> spawners = this.blocks.stream().filter(block -> block.groups.contains(spawn.spawner)).map(block -> block.pos).toList();
         if (!spawners.isEmpty()) {
            BlockPos spawner = spawners.get(random.nextInt(spawners.size()));
            spawnerToSpawns.computeIfAbsent(spawner.offset(pos), unused -> new ArrayList<>()).add(spawn);
         }
      }

      spawnerToSpawns.forEach((spawnerx, spawns) -> {
         BlockEntity blockEntity = world.getBlockEntity(spawnerx);
         if (blockEntity != null) {
            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            CompoundTag manager = nbt.getCompound("Manager");
            nbt.put("Manager", manager);
            CompoundTag settings = nbt.getCompound("Settings");
            manager.put("Settings", settings);
            manager.putInt("SpawnTimer", 0);
            manager.putInt("UsesLeft", -1);
            manager.putInt("WaveCounter", 1);
            manager.putBoolean("ConsumeCharge", true);
            settings.putInt("Mode", 2);
            settings.putInt("CheckRadius", 0);
            settings.putInt("PlayerRadius", 0);
            settings.putInt("SpawnDelay", 1);
            settings.putInt("Attempts", 200);
            settings.putInt("SpawnDelay", 200000);
            CompoundTag inventory = nbt.getCompound("Inventory");
            ListTag stacksList = inventory.getList("Stacks", 10);
            List<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < stacksList.size(); i++) {
               CompoundTag stackEntry = stacksList.getCompound(i);
               ItemStack stack = ItemStack.of(stackEntry);
               stack.setCount(stackEntry.getInt("Count"));
               stacks.add(stack);
            }

            for (int i = 0; i < spawns.size(); i++) {
               RaidSpawner.Spawn spawnx = spawns.get(i);
               if (spawnx.entity != null && spawnx.count > 0) {
                  ItemStack egg = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("ispawner:spawn_egg")));
                  egg.setCount(spawnx.count);
                  spawnx.entity.getNbt().asWhole().ifPresent(tagx -> egg.getOrCreateTag().put("EntityTag", tagx));
                  stacks.add(i, egg);
               }
            }

            stacksList.clear();

            for (ItemStack stack : stacks) {
               CompoundTag tag = stack.serializeNBT();
               tag.putInt("Count", stack.getCount());
               stacksList.add(tag);
            }

            inventory.put("Stacks", stacksList);
            blockEntity.load(nbt);
            if (world.getBlockState(spawnerx).getBlock() instanceof EntityBlock entityBlock) {
               BlockEntityTicker ticker = entityBlock.getTicker(world, blockEntity.getBlockState(), blockEntity.getType());
               if (ticker != null) {
                  this.trackSpawns = true;
                  ticker.tick(world, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity);
                  this.trackSpawns = false;
               }
            }
         }
      });
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ListTag blocks = new ListTag();

         for (RaidSpawner.Block block : this.blocks) {
            block.writeNbt().ifPresent(blocks::add);
         }

         nbt.put("blocks", blocks);
         ListTag spawns = new ListTag();

         for (RaidSpawner.Spawn spawn : this.spawns) {
            spawn.writeNbt().ifPresent(spawns::add);
         }

         nbt.put("spawns", spawns);
         ListTag spawnedWave = new ListTag();

         for (UUID uuid : this.spawnedWave) {
            spawnedWave.add(StringTag.valueOf(uuid.toString()));
         }

         nbt.put("spawnedWave", spawnedWave);
         nbt.putInt("count", this.count);
         nbt.putInt("target", this.target);
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.currentHealth)).ifPresent(tag -> nbt.put("currentHealth", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.totalHealth)).ifPresent(tag -> nbt.put("totalHealth", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      ListTag blocks = nbt.getList("blocks", 10);
      this.blocks.clear();

      for (int i = 0; i < blocks.size(); i++) {
         RaidSpawner.Block block = new RaidSpawner.Block();
         block.readNbt(blocks.getCompound(i));
         this.blocks.add(block);
      }

      ListTag spawns = nbt.getList("spawns", 10);
      this.spawns.clear();

      for (int i = 0; i < spawns.size(); i++) {
         RaidSpawner.Spawn spawn = new RaidSpawner.Spawn();
         spawn.readNbt(spawns.getCompound(i));
         this.spawns.add(spawn);
      }

      ListTag spawnedWave = nbt.getList("spawnedWave", 8);
      this.spawnedWave.clear();

      for (int i = 0; i < spawnedWave.size(); i++) {
         this.spawnedWave.add(UUID.fromString(spawnedWave.getString(i)));
      }

      this.count = nbt.getInt("count");
      this.target = Adapters.INT.readNbt(nbt.get("target")).orElse(-1);
      this.currentHealth = Adapters.DOUBLE.readNbt(nbt.get("currentHealth")).orElse(0.0);
      this.totalHealth = Adapters.DOUBLE.readNbt(nbt.get("totalHealth")).orElse(0.0);
   }

   public int getWaveCount() {
      return this.count;
   }

   public int getWaveTarget() {
      return this.target;
   }

   public void onCompleteWave() {
      this.count++;
   }

   public boolean hasWaveAvailable() {
      return this.target < 0 || this.count < this.target;
   }

   public static class Block implements ISerializable<CompoundTag, JsonObject> {
      private BlockPos pos;
      private Set<String> groups;
      public static final ArrayAdapter<String> GROUPS = Adapters.ofArray(String[]::new, Adapters.UTF_8);

      public Block() {
      }

      public Block(BlockPos pos, String... groups) {
         this.pos = pos;
         this.groups = new HashSet<>(Arrays.asList(groups));
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.BLOCK_POS.writeNbt(this.pos).ifPresent(tag -> nbt.put("pos", tag));
            GROUPS.writeNbt(this.groups.toArray(String[]::new)).ifPresent(tag -> nbt.put("groups", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.pos = Adapters.BLOCK_POS.readNbt(nbt.get("pos")).orElse(null);
         this.groups = Arrays.stream(GROUPS.readNbt(nbt.get("groups")).orElse(new String[0])).collect(Collectors.toSet());
      }
   }

   private static class Spawn implements ISerializable<CompoundTag, JsonObject> {
      private PartialEntity entity;
      private int count;
      private String spawner;

      public Spawn() {
      }

      public Spawn(PartialEntity entity, int count, String spawner) {
         this.entity = entity;
         this.count = count;
         this.spawner = spawner;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.PARTIAL_ENTITY.writeNbt(this.entity).ifPresent(tag -> nbt.put("entity", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
            Adapters.UTF_8.writeNbt(this.spawner).ifPresent(tag -> nbt.put("spawner", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.entity = Adapters.PARTIAL_ENTITY.readNbt(nbt.get("entity")).orElse(null);
         this.count = Adapters.INT.readNbt(nbt.get("count")).orElse(0);
         this.spawner = Adapters.UTF_8.readNbt(nbt.get("spawner")).orElse(null);
      }
   }
}
