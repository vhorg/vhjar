package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FallbackExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("fallback");
   protected CompoundNBT fallback;

   public FallbackExtension() {
   }

   public FallbackExtension(CompoundNBT fallback) {
      super(ID, 0L);
      this.deserializeNBT(fallback);
   }

   public CompoundNBT getFallback() {
      return this.fallback;
   }

   @Override
   public CompoundNBT serializeNBT() {
      return this.fallback;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      this.fallback = nbt;
      this.extraTime = this.getFallback().func_74763_f("ExtraTime");
      this.executionTime = this.getFallback().func_74763_f("ExecutionTime");
   }
}
