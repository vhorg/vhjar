package iskallia.vault.item.bottle;

import iskallia.vault.mana.Mana;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class ManaPercentBottleEffect extends BottleEffect {
   public static final String TYPE = "mana_percent";
   private final float amount;

   public ManaPercentBottleEffect(String effectId, float amount) {
      super(effectId);
      this.amount = amount;
   }

   @Override
   public String getType() {
      return "mana_percent";
   }

   @Override
   public String getTooltipText(String tooltipFormat) {
      return String.format(tooltipFormat, (int)(100.0F * this.amount));
   }

   @Override
   public void trigger(ServerPlayer player) {
      float current = Mana.get(player);
      float total = Mana.getMax(player);
      Mana.set(player, Math.min(total, this.amount * total + current));
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putFloat("amount", this.amount);
      return tag;
   }

   public static BottleEffect deserialize(String effectId, CompoundTag tag) {
      return new ManaPercentBottleEffect(effectId, tag.getFloat("amount"));
   }
}
