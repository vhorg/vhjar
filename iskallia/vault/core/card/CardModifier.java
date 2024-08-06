package iskallia.vault.core.card;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class CardModifier<C extends CardModifier.Config> extends CardProperty<C> {
   public static final CardModifier.Adapter ADAPTER = new CardModifier.Adapter();

   public CardModifier(C config) {
      super(config);
   }

   public int getMaxTier() {
      return this.getConfig().maxTier;
   }

   public abstract List<VaultGearAttributeInstance<?>> getSnapshotAttributes(int var1);

   public abstract int getHighlightColor();

   public static class Adapter extends TypeSupplierAdapter<CardModifier<?>> {
      public Adapter() {
         super("type", true);
         this.register("dummy", DummyCardModifier.class, DummyCardModifier::new);
         this.register("gear", GearCardModifier.class, GearCardModifier::new);
         this.register("task_loot", TaskLootCardModifier.class, TaskLootCardModifier::new);
      }
   }

   public static class Config extends CardProperty.Config {
      public int maxTier;

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.maxTier), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.maxTier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.INT.writeNbt(Integer.valueOf(this.maxTier)).ifPresent(tag -> nbt.put("maxTier", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         this.maxTier = Adapters.INT.readNbt(nbt.get("maxTier")).orElse(100);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            Adapters.INT.writeJson(Integer.valueOf(this.maxTier)).ifPresent(tag -> json.add("maxTier", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         this.maxTier = Adapters.INT.readJson(json.get("maxTier")).orElse(100);
      }
   }
}
