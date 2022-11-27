package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class FallbackExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("fallback");
   protected CompoundTag fallback;

   public FallbackExtension() {
   }

   public FallbackExtension(CompoundTag fallback) {
      super(ID, 0L);
      this.deserializeNBT(fallback);
   }

   public CompoundTag getFallback() {
      return this.fallback;
   }

   @Override
   public CompoundTag serializeNBT() {
      return this.fallback;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.fallback = nbt;
      this.extraTime = this.getFallback().getLong("ExtraTime");
      this.executionTime = this.getFallback().getLong("ExecutionTime");
   }
}
