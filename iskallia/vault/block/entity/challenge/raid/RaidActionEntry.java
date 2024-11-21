package iskallia.vault.block.entity.challenge.raid;

import iskallia.vault.core.data.adapter.Adapters;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class RaidActionEntry extends ChallengeActionEntry {
   private Integer minWave;
   private Integer maxWave;

   public boolean matchesWave(int wave) {
      return this.minWave != null && wave < this.minWave ? false : this.maxWave == null || wave <= this.maxWave;
   }

   @Override
   public <T extends ChallengeActionEntry> T copy() {
      RaidActionEntry entry = new RaidActionEntry();
      entry.readNbt(this.writeNbt().orElseThrow());
      return (T)entry;
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(this.minWave).ifPresent(tag -> nbt.put("minWave", tag));
         Adapters.INT.writeNbt(this.maxWave).ifPresent(tag -> nbt.put("maxWave", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.minWave = Adapters.INT.readNbt(nbt.get("minWave")).orElse(null);
      this.maxWave = Adapters.INT.readNbt(nbt.get("maxWave")).orElse(null);
   }
}
