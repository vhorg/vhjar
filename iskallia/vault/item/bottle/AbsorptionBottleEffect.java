package iskallia.vault.item.bottle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class AbsorptionBottleEffect extends BottleEffect {
   public static final String TYPE = "absorption";
   private final float amount;

   public AbsorptionBottleEffect(String effectId, float amount) {
      super(effectId);
      this.amount = amount;
   }

   @Override
   public String getType() {
      return "absorption";
   }

   @Override
   public String getTooltipText(String tooltipFormat) {
      return String.format(tooltipFormat, (int)this.amount / 2);
   }

   @Override
   public void trigger(ServerPlayer player) {
      player.setAbsorptionAmount(player.getAbsorptionAmount() + this.amount);
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putFloat("amount", this.amount);
      return tag;
   }

   public static BottleEffect deserialize(String effectId, CompoundTag tag) {
      return new AbsorptionBottleEffect(effectId, tag.getFloat("amount"));
   }
}
