package iskallia.vault.block.entity.challenge.raid;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class ChallengeActionEntry implements ISerializable<CompoundTag, JsonObject> {
   private final ChallengeActionEntry.Config config = new ChallengeActionEntry.Config();
   private boolean populated = false;
   private int count;

   public ChallengeActionEntry.Config getConfig() {
      return this.config;
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public void setPopulated(boolean populated) {
      this.populated = populated;
   }

   public float getProbability() {
      return this.getConfig().probability;
   }

   public void onPopulate(RandomSource random) {
      if (this.getConfig().count != null) {
         this.count = this.getConfig().count.get(random);
      }

      this.setPopulated(true);
   }

   public List<ChallengeAction<?>> getActions() {
      List<ChallengeAction<?>> actions = new ArrayList<>();

      for (int i = 0; i < this.count; i++) {
         actions.add(this.getConfig().action);
      }

      return actions;
   }

   public <T extends ChallengeActionEntry> T copy() {
      ChallengeActionEntry entry = new ChallengeActionEntry();
      entry.readNbt(this.writeNbt().orElseThrow());
      return (T)entry;
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         if (!this.isPopulated()) {
            this.config.writeNbt().ifPresent(tag -> {
               for (String key : tag.getAllKeys()) {
                  nbt.put(key, Objects.requireNonNull(tag.get(key)));
               }
            });
         } else {
            this.config.writeNbt().ifPresent(tag -> nbt.put("config", tag));
            Adapters.BOOLEAN.writeNbt(this.populated).ifPresent(tag -> nbt.put("populated", tag));
         }

         Adapters.INT.writeNbt(Integer.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      if (nbt.contains("config")) {
         this.config.readNbt(nbt.getCompound("config"));
      } else {
         this.config.readNbt(nbt);
      }

      this.populated = Adapters.BOOLEAN.readNbt(nbt.get("populated")).orElse(false);
      this.count = Adapters.INT.readNbt(nbt.get("count")).orElse(0);
   }

   public static class Config implements ISerializable<CompoundTag, JsonObject> {
      private ChallengeAction<?> action;
      private float probability;
      private IntRoll count;

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.RAID_ACTION.writeNbt(this.action).ifPresent(tag -> nbt.put("action", tag));
            Adapters.FLOAT.writeNbt(Float.valueOf(this.probability)).ifPresent(tag -> nbt.put("probability", tag));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.action = Adapters.RAID_ACTION.readNbt(nbt.get("action")).orElseThrow();
         this.probability = Adapters.FLOAT.readNbt(nbt.get("probability")).orElseThrow();
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElseThrow();
      }
   }
}
