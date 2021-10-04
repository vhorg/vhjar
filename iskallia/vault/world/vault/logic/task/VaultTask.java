package iskallia.vault.world.vault.logic.task;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultTask implements IVaultTask, INBTSerializable<CompoundNBT> {
   public static final Map<ResourceLocation, VaultTask> REGISTRY = new HashMap<>();
   public static final VaultTask EMPTY = register(Vault.id("empty"), (vault, player, world) -> {});
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
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      this.task.execute(vault, player, world);
   }

   public VaultTask then(VaultTask other) {
      return new CompoundVaultTask(this, other, ">", this.task.then(other));
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
   }

   public static VaultTask fromNBT(CompoundNBT nbt) {
      return (VaultTask)(nbt.func_150297_b("Id", 8) ? REGISTRY.get(new ResourceLocation(nbt.func_74779_i("Id"))) : CompoundVaultTask.fromNBT(nbt));
   }

   public static VaultTask register(ResourceLocation id, IVaultTask task) {
      VaultTask vaultTask = new VaultTask(id, task);
      REGISTRY.put(id, vaultTask);
      return vaultTask;
   }
}
