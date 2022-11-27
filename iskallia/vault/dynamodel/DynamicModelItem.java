package iskallia.vault.dynamodel;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface DynamicModelItem {
   String NBT_GENERIC_MODEL_KEY = "VaultModelId";

   @Deprecated(
      forRemoval = true
   )
   static Optional<ResourceLocation> getGenericModelId(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getTag();
      if (nbt == null) {
         return Optional.empty();
      } else {
         String vaultModelId = nbt.getString("VaultModelId");
         return vaultModelId.isEmpty() ? Optional.empty() : Optional.of(new ResourceLocation(vaultModelId));
      }
   }

   @Deprecated(
      forRemoval = true
   )
   static void setGenericModelId(ItemStack itemStack, ResourceLocation modelId) {
      CompoundTag nbt = itemStack.getOrCreateTag();
      nbt.putString("VaultModelId", modelId.toString());
   }

   Optional<ResourceLocation> getDynamicModelId(ItemStack var1);
}
