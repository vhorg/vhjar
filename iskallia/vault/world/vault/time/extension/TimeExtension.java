package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TimeExtension implements INBTSerializable<CompoundNBT> {
   public static final Map<ResourceLocation, Supplier<TimeExtension>> REGISTRY = new HashMap<>();
   protected ResourceLocation id;
   protected long extraTime;
   protected long executionTime;

   public TimeExtension() {
   }

   public TimeExtension(ResourceLocation id, long extraTime) {
      this.id = id;
      this.extraTime = extraTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public long getExtraTime() {
      return this.extraTime;
   }

   public long getExecutionTime() {
      return this.executionTime;
   }

   public void setExecutionTime(long executionTime) {
      this.executionTime = executionTime;
   }

   public void apply(VaultTimer timer) {
      timer.totalTime = (int)(timer.totalTime + this.extraTime);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      nbt.func_74772_a("ExtraTime", this.getExtraTime());
      nbt.func_74772_a("ExecutionTime", this.getExecutionTime());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
      this.extraTime = nbt.func_74763_f("ExtraTime");
      this.executionTime = nbt.func_74763_f("ExecutionTime");
   }

   public static TimeExtension fromNBT(CompoundNBT nbt) {
      ResourceLocation id = new ResourceLocation(nbt.func_74779_i("Id"));
      TimeExtension extension = REGISTRY.getOrDefault(id, () -> null).get();
      if (extension == null) {
         Vault.LOGGER.error("Vault time extension <" + id.toString() + "> is not defined, using fallback.");
         return new FallbackExtension(nbt);
      } else {
         try {
            extension.deserializeNBT(nbt);
            return extension;
         } catch (Exception var4) {
            var4.printStackTrace();
            Vault.LOGGER.error("Vault time extension <" + id.toString() + "> could not be deserialized, using fallback.");
            return new FallbackExtension(nbt);
         }
      }
   }
}
