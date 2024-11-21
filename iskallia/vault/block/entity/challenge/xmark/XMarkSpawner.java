package iskallia.vault.block.entity.challenge.xmark;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraftforge.eventbus.api.EventPriority;

public class XMarkSpawner implements ISerializable<CompoundTag, JsonObject> {
   private final List<BlockPos> blocks = new ArrayList<>();
   private final Set<UUID> spawnedWave = new HashSet<>();
   private double currentHealth;
   private double totalHealth;
   private int waveCount;
   private int waveTarget;
   private int spawnDelay;
   private int spawnTick;
   private boolean active;
   private boolean trackSpawns;

   public List<BlockPos> getBlocks() {
      return this.blocks;
   }

   public double getCurrentHealth() {
      return this.currentHealth;
   }

   public double getTotalHealth() {
      return this.totalHealth;
   }

   public void add(List<BlockPos> blocks) {
      this.blocks.addAll(blocks);
   }

   public void setWaveTarget(int waveTarget) {
      this.waveTarget = waveTarget;
   }

   public boolean isWaveCompleted() {
      return this.spawnedWave.isEmpty();
   }

   public int getWaveCount() {
      return this.waveCount;
   }

   public int getWaveTarget() {
      return this.waveTarget;
   }

   public void onCompleteWave() {
      this.waveCount++;
   }

   public void setSpawnDelay(int spawnDelay) {
      this.spawnDelay = spawnDelay;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public void onAttach(Level world, XMarkChallengeManager manager) {
      CommonEvents.ENTITY_LEAVE.register(this, EventPriority.HIGHEST, event -> {
         if (event.getEntity().level == world) {
            this.spawnedWave.remove(event.getEntity().getUUID());
         }
      });
      CommonEvents.ENTITY_INITIALIZE.register(this, event -> {
         if (this.trackSpawns && event.getMob().level == world) {
            this.spawnedWave.add(event.getMob().getUUID());
         }
      });
   }

   public void onDetach() {
      CommonEvents.ENTITY_LEAVE.release(this);
      CommonEvents.ENTITY_INITIALIZE.release(this);
   }

   public void onTick(ServerLevel world, BlockPos pos, Set<UUID> players) {
      this.spawnedWave.removeIf(uuidx -> world.getEntity(uuidx) == null);
      if (this.active) {
         if (this.spawnDelay > 0 && this.spawnTick % this.spawnDelay == 0) {
            this.onSpawn(world, pos, players.stream().<Player>map(world::getPlayerByUUID).toList());
         }

         this.spawnTick++;
      }

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

   public void onSpawn(ServerLevel world, BlockPos pos, List<Player> players) {
      if (this.waveCount < this.waveTarget) {
         this.waveCount++;
         if (this.waveCount == this.waveTarget) {
            players.forEach(player -> world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.2F, 0.2F));
         } else {
            players.forEach(player -> world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 0.2F, 0.2F));
         }

         this.totalHealth = 0.0;

         for (BlockPos spawner : this.blocks) {
            spawner = spawner.offset(pos);
            BlockEntity blockEntity = world.getBlockEntity(spawner);
            if (blockEntity == null) {
               return;
            }

            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            CompoundTag manager = nbt.getCompound("Manager");
            nbt.put("Manager", manager);
            CompoundTag settings = manager.getCompound("Settings");
            manager.put("Settings", settings);
            manager.putInt("SpawnTimer", 0);
            manager.putInt("UsesLeft", 1);
            settings.putInt("Mode", 2);
            settings.putInt("SpawnDelay", 20000000);
            blockEntity.load(nbt);
            if (world.getBlockState(spawner).getBlock() instanceof EntityBlock entityBlock) {
               BlockEntityTicker ticker = entityBlock.getTicker(world, blockEntity.getBlockState(), blockEntity.getType());
               if (ticker != null) {
                  this.trackSpawns = true;
                  ticker.tick(world, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity);
                  this.trackSpawns = false;
               }
            }
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ListTag blocks = new ListTag();

         for (BlockPos block : this.blocks) {
            Adapters.BLOCK_POS.writeNbt(block).ifPresent(blocks::add);
         }

         nbt.put("blocks", blocks);
         ListTag spawnedWave = new ListTag();

         for (UUID uuid : this.spawnedWave) {
            spawnedWave.add(StringTag.valueOf(uuid.toString()));
         }

         nbt.put("spawnedWave", spawnedWave);
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.currentHealth)).ifPresent(tag -> nbt.put("currentHealth", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.totalHealth)).ifPresent(tag -> nbt.put("totalHealth", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.waveCount)).ifPresent(tag -> nbt.put("waveCount", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.waveTarget)).ifPresent(tag -> nbt.put("waveTarget", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.spawnDelay)).ifPresent(tag -> nbt.put("spawnDelay", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.spawnTick)).ifPresent(tag -> nbt.put("spawnTick", tag));
         Adapters.BOOLEAN.writeNbt(this.active).ifPresent(tag -> nbt.put("active", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      ListTag blocks = nbt.getList("blocks", 10);
      this.blocks.clear();

      for (int i = 0; i < blocks.size(); i++) {
         Adapters.BLOCK_POS.readNbt(blocks.get(i)).ifPresent(this.blocks::add);
      }

      ListTag spawnedWave = nbt.getList("spawnedWave", 8);
      this.spawnedWave.clear();

      for (int i = 0; i < spawnedWave.size(); i++) {
         this.spawnedWave.add(UUID.fromString(spawnedWave.getString(i)));
      }

      this.currentHealth = Adapters.DOUBLE.readNbt(nbt.get("currentHealth")).orElse(0.0);
      this.totalHealth = Adapters.DOUBLE.readNbt(nbt.get("totalHealth")).orElse(0.0);
      this.waveCount = Adapters.INT.readNbt(nbt.get("waveCount")).orElse(0);
      this.waveTarget = Adapters.INT.readNbt(nbt.get("waveTarget")).orElse(0);
      this.spawnDelay = Adapters.INT.readNbt(nbt.get("spawnDelay")).orElse(0);
      this.spawnTick = Adapters.INT.readNbt(nbt.get("spawnTick")).orElse(0);
      this.active = Adapters.BOOLEAN.readNbt(nbt.get("active")).orElse(false);
   }
}
