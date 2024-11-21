package iskallia.vault.item.crystal.time;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.time.TickStopwatch;
import iskallia.vault.core.vault.time.TickTimer;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ValueCrystalTime extends CrystalTime {
   private IntRoll roll;

   public ValueCrystalTime() {
   }

   public ValueCrystalTime(IntRoll roll) {
      this.roll = roll;
   }

   public IntRoll getRoll() {
      return this.roll;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.ifPresent(Vault.CLOCK, clock -> {
         if (clock instanceof TickTimer) {
            clock.set(TickTimer.DISPLAY_TIME, Integer.valueOf(this.roll.get(random)));
         } else if (clock instanceof TickStopwatch) {
            clock.set(TickStopwatch.LIMIT, Integer.valueOf(this.roll.get(random)));
         }
      });
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      int min = this.roll.getMin();
      int max = this.roll.getMax();
      String text = UIHelper.formatTimeString(min);
      if (min != max) {
         text = text + " - " + UIHelper.formatTimeString(max);
      }

      tooltip.add(new TextComponent("Time: ").append(new TextComponent(text).withStyle(ChatFormatting.GRAY)));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT_ROLL.writeNbt(this.roll).ifPresent(roll -> nbt.put("roll", roll));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.roll = Adapters.INT_ROLL.readNbt(nbt.getCompound("roll")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT_ROLL.writeJson(this.roll).ifPresent(roll -> json.add("roll", roll));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.roll = Adapters.INT_ROLL.readJson(json.getAsJsonObject("roll")).orElse(null);
   }
}
