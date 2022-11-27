package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TimeExtension implements INBTSerializable<CompoundTag> {
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

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      nbt.putLong("ExtraTime", this.getExtraTime());
      nbt.putLong("ExecutionTime", this.getExecutionTime());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
      this.extraTime = nbt.getLong("ExtraTime");
      this.executionTime = nbt.getLong("ExecutionTime");
   }

   public static TimeExtension fromNBT(CompoundTag nbt) {
      ResourceLocation id = new ResourceLocation(nbt.getString("Id"));
      TimeExtension extension = REGISTRY.getOrDefault(id, () -> null).get();
      if (extension == null) {
         VaultMod.LOGGER.error("Vault time extension <" + id.toString() + "> is not defined, using fallback.");
         return new FallbackExtension(nbt);
      } else {
         try {
            extension.deserializeNBT(nbt);
            return extension;
         } catch (Exception var4) {
            var4.printStackTrace();
            VaultMod.LOGGER.error("Vault time extension <" + id.toString() + "> could not be deserialized, using fallback.");
            return new FallbackExtension(nbt);
         }
      }
   }
}
