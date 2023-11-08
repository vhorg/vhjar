package iskallia.vault.block.base;

import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.task.CompleteGodAltarTask;
import iskallia.vault.task.FailGodAltarTask;
import iskallia.vault.task.KillEntityTask;
import iskallia.vault.task.NodeTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.GodAltarRenderer;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.GodAltarData;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldSettings;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GodAltarTileEntity extends BlockEntity {
   protected UUID uuid;
   protected Task task;
   protected Task taskPool = new KillEntityTask(
         new KillEntityTask.Config(EntityPredicate.of("@the_vault:zombie", true).orElse(EntityPredicate.FALSE), IntRoll.ofUniform(1, 5))
      )
      .add(new GodAltarRenderer.Base("Kill Zombies", "${current}/${target}"));
   protected ResourceLocation modifierCompletionPool = VaultMod.id("default");
   protected ResourceLocation modifierFailurePool = VaultMod.id("default");
   protected Map<VaultDifficulty, IntRoll> timePool = new HashMap<>();

   public GodAltarTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.GOD_ALTAR_TILE_ENTITY, pos, state);
   }

   public GodAltarTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);

      for (VaultDifficulty difficulty : VaultDifficulty.values()) {
         this.timePool.put(difficulty, IntRoll.ofConstant(1200));
      }
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public Task getTask() {
      return this.task;
   }

   public ResourceLocation getModifierCompletionPool() {
      return this.modifierCompletionPool;
   }

   public ResourceLocation getModifierFailurePool() {
      return this.modifierFailurePool;
   }

   public boolean isCompleted() {
      return this.task == null && this.uuid != null;
   }

   public void setTask(Task task) {
      this.task = task;
      this.sendUpdates();
   }

   public void fetchTask() {
      if (this.uuid != null) {
         this.setTask(GodAltarData.get(this.uuid).map(GodAltarData.Entry::getTask).orElse(null));
      } else {
         this.setTask(null);
      }
   }

   public void onClick(ServerLevel world, ServerPlayer player) {
      this.fetchTask();
      if (this.task == null && !this.isCompleted() && !GodAltarData.contains(player)) {
         ChunkRandom random = ChunkRandom.any();
         Vault vault = ServerVaults.get(world).orElse(null);
         UUID vaultUuid = vault == null ? null : vault.getOr(Vault.ID, null);
         if (vault != null) {
            random.setBlockSeed(vault.get(Vault.SEED), this.worldPosition, 1876541L);
         } else {
            random.setSeed(JavaRandom.ofNanoTime().getSeed());
         }

         VaultDifficulty difficulty = WorldSettings.get(world).getPlayerDifficulty(player.getUUID());
         VaultGod god = (VaultGod)world.getBlockState(this.worldPosition).getValue(GodAltarBlock.GOD);
         this.uuid = UUID.randomUUID();
         Task completion = new CompleteGodAltarTask(this.uuid, this.modifierCompletionPool).add(new GodAltarRenderer.Complete());
         Task failure = new FailGodAltarTask(vaultUuid, player.getUUID(), this.uuid, this.modifierFailurePool);
         Task task = new NodeTask(
               new TimedTask(this.timePool.get(difficulty).get(random)).add(new GodAltarRenderer.Timed()),
               new NodeTask(this.taskPool.copy(), new NodeTask(completion)),
               new NodeTask(failure)
            )
            .add(new GodAltarRenderer.Node());
         GodAltarData.add(this.uuid, task, EntityTaskSource.of(random, player), god);
         this.fetchTask();
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, GodAltarTileEntity tile) {
      if (level instanceof ServerLevel world) {
         if (tile.getUuid() != null) {
            tile.fetchTask();
         }
      }
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.UUID.writeNbt(this.uuid).ifPresent(task -> nbt.put("uuid", task));
      Adapters.TASK.writeNbt(this.task).ifPresent(task -> nbt.put("task", task));
      Adapters.TASK.writeNbt(this.taskPool).ifPresent(task -> nbt.put("taskPool", task));
      Adapters.IDENTIFIER.writeNbt(this.modifierCompletionPool).ifPresent(task -> nbt.put("modifierCompletionPool", task));
      Adapters.IDENTIFIER.writeNbt(this.modifierFailurePool).ifPresent(task -> nbt.put("modifierFailurePool", task));
      CompoundTag timePool = new CompoundTag();

      for (VaultDifficulty difficulty : VaultDifficulty.values()) {
         Adapters.INT_ROLL.writeNbt(this.timePool.get(difficulty)).ifPresent(task -> timePool.put(difficulty.name(), task));
      }

      nbt.put("timePool", timePool);
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElse(null);
      this.task = Adapters.TASK.readNbt(nbt.get("task")).orElse(null);
      this.taskPool = Adapters.TASK.readNbt(nbt.get("taskPool")).orElse(this.taskPool);
      this.modifierCompletionPool = Adapters.IDENTIFIER.readNbt(nbt.get("modifierCompletionPool")).orElse(this.modifierCompletionPool);
      this.modifierFailurePool = Adapters.IDENTIFIER.readNbt(nbt.get("modifierFailurePool")).orElse(this.modifierFailurePool);
      CompoundTag timePool = nbt.getCompound("timePool");

      for (VaultDifficulty difficulty : VaultDifficulty.values()) {
         Adapters.INT_ROLL.readNbt(timePool.get(difficulty.name())).ifPresent(roll -> this.timePool.put(difficulty, roll));
      }
   }

   public CompoundTag getUpdateTag() {
      CompoundTag nbt = this.saveWithoutMetadata();
      nbt.remove("taskPool");
      nbt.remove("modifierCompletionPool");
      nbt.remove("modifierFailurePool");
      nbt.remove("timePool");
      return nbt;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
