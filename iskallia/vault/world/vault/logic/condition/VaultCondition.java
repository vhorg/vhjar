package iskallia.vault.world.vault.logic.condition;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultCondition implements IVaultCondition, INBTSerializable<CompoundTag> {
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
   public boolean test(VaultRaid vault, VaultPlayer player, ServerLevel world) {
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

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
   }

   public static VaultCondition fromNBT(CompoundTag nbt) {
      return (VaultCondition)(nbt.contains("Id", 8) ? REGISTRY.get(new ResourceLocation(nbt.getString("Id"))) : CompoundVaultCondition.fromNBT(nbt));
   }

   public static VaultCondition register(ResourceLocation id, IVaultCondition condition) {
      VaultCondition vaultCondition = new VaultCondition(id, condition);
      REGISTRY.put(vaultCondition.getId(), vaultCondition);
      return vaultCondition;
   }
}
