package iskallia.vault.world.vault.logic.condition;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultCondition implements IVaultCondition, INBTSerializable<CompoundNBT> {
   public static final Map<ResourceLocation, VaultCondition> REGISTRY = new HashMap<>();
   private ResourceLocation id;
   protected IVaultCondition condition;

   protected VaultCondition() {
   }

   protected VaultCondition(ResourceLocation id, IVaultCondition condition) {
      this.id = id;
      this.condition = condition;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public boolean test(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      return this.condition.test(vault, player, world);
   }

   public VaultCondition negate() {
      return new CompoundVaultCondition(this, null, "~", this.condition.negate());
   }

   public VaultCondition and(VaultCondition other) {
      return new CompoundVaultCondition(this, other, "&", this.condition.and(other));
   }

   public VaultCondition or(VaultCondition other) {
      return new CompoundVaultCondition(this, other, "|", this.condition.or(other));
   }

   public VaultCondition xor(VaultCondition other) {
      return new CompoundVaultCondition(this, other, "^", this.condition.xor(other));
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
   }

   public static VaultCondition fromNBT(CompoundNBT nbt) {
      return (VaultCondition)(nbt.func_150297_b("Id", 8) ? REGISTRY.get(new ResourceLocation(nbt.func_74779_i("Id"))) : CompoundVaultCondition.fromNBT(nbt));
   }

   public static VaultCondition register(ResourceLocation id, IVaultCondition condition) {
      VaultCondition vaultCondition = new VaultCondition(id, condition);
      REGISTRY.put(vaultCondition.getId(), vaultCondition);
      return vaultCondition;
   }
}
