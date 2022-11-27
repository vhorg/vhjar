package iskallia.vault.world.vault.logic.task;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultTask implements IVaultTask, INBTSerializable<CompoundTag> {
   public static final Map<ResourceLocation, VaultTask> REGISTRY = new HashMap<>();
   public static final VaultTask EMPTY = register(VaultMod.id("empty"), (vault, player, world) -> {});
   private ResourceLocation id;
   protected IVaultTask task;

   protected VaultTask() {
   }

   public VaultTask(ResourceLocation id, IVaultTask task) {
      this.id = id;
      this.task = task;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      this.task.execute(vault, player, world);
   }

   public VaultTask then(VaultTask other) {
      return new CompoundVaultTask(this, other, ">", this.task.then(other));
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
   }

   public static VaultTask fromNBT(CompoundTag nbt) {
      return (VaultTask)(nbt.contains("Id", 8) ? REGISTRY.get(new ResourceLocation(nbt.getString("Id"))) : CompoundVaultTask.fromNBT(nbt));
   }

   public static VaultTask register(ResourceLocation id, IVaultTask task) {
      VaultTask vaultTask = new VaultTask(id, task);
      REGISTRY.put(id, vaultTask);
      return vaultTask;
   }
}
