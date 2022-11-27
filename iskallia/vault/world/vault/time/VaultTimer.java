package iskallia.vault.world.vault.time;

import iskallia.vault.nbt.VListNBT;
import iskallia.vault.world.vault.time.extension.TimeExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultTimer implements INBTSerializable<CompoundTag> {
   public int startTime;
   public int totalTime;
   public int runTime;
   protected VListNBT<TimeExtension, CompoundTag> extensions = VListNBT.of(TimeExtension::fromNBT);
   protected List<BiConsumer<VaultTimer, TimeExtension>> extensionAddedListeners = new ArrayList<>();
   protected List<BiConsumer<VaultTimer, TimeExtension>> extensionAppliedListeners = new ArrayList<>();

   public int getStartTime() {
      return this.startTime;
   }

   public int getTotalTime() {
      return this.totalTime;
   }

   public int getRunTime() {
      return this.runTime;
   }

   public int getTimeLeft() {
      return this.totalTime - this.runTime;
   }

   public VaultTimer addTime(TimeExtension extension, int delay) {
      extension.setExecutionTime(this.runTime + delay);
      this.extensions.add(extension);
      this.extensionAddedListeners.forEach(listener -> listener.accept(this, extension));
      return this;
   }

   public VaultTimer onExtensionAdded(BiConsumer<VaultTimer, TimeExtension> listener) {
      this.extensionAddedListeners.add(listener);
      return this;
   }

   public VaultTimer onExtensionApplied(BiConsumer<VaultTimer, TimeExtension> listener) {
      this.extensionAppliedListeners.add(listener);
      return this;
   }

   public VaultTimer start(int startTime) {
      this.runTime = 0;
      this.startTime = startTime;
      this.totalTime = this.startTime;
      return this;
   }

   public void tick() {
      this.extensions.forEach(extension -> {
         if (extension.getExecutionTime() == this.runTime) {
            extension.apply(this);
            this.extensionAppliedListeners.forEach(listener -> listener.accept(this, extension));
         }
      });
      this.runTime++;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("StartTime", this.startTime);
      nbt.putInt("TotalTime", this.totalTime);
      nbt.putInt("RunTime", this.runTime);
      nbt.put("TimeExtensions", this.extensions.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.startTime = nbt.getInt("StartTime");
      this.totalTime = nbt.getInt("TotalTime");
      this.runTime = nbt.getInt("RunTime");
      this.extensions.deserializeNBT(nbt.getList("TimeExtensions", 10));
   }
}
