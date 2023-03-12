package iskallia.vault.core.vault.enhancement;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class IntFilterEnhancementTask<C extends IntFilterEnhancementTask.Config<?>> extends EnhancementTask<C> {
   protected int count;
   protected int requiredCount;

   public IntFilterEnhancementTask() {
   }

   public IntFilterEnhancementTask(C config, UUID vault, UUID player, UUID altar, int requiredCount) {
      super(config, vault, player, altar);
      this.count = 0;
      this.requiredCount = requiredCount;
   }

   @Override
   public boolean isFinished() {
      return this.count >= this.requiredCount;
   }

   @Override
   public Component getProgressComponent() {
      return new TextComponent(this.count + " / " + this.requiredCount);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.requiredCount)).ifPresent(tag -> nbt.put("requiredCount", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.count = Adapters.INT.readNbt(nbt.get("count")).orElse(0);
      this.requiredCount = Adapters.INT.readNbt(nbt.get("requiredCount")).orElse(0);
   }

   public abstract static class Config<T extends IntFilterEnhancementTask<?>> extends EnhancementTask.Config<T> {
      protected IntRoll range;

      public Config() {
      }

      public Config(String display, IntRoll range) {
         super(display);
         this.range = range;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.INT_ROLL.writeNbt(this.range).ifPresent(tag -> nbt.put("range", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.range = Adapters.INT_ROLL.readNbt(nbt.getCompound("range")).orElse(IntRoll.ofConstant(1));
      }
   }
}
