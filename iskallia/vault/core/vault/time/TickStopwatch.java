package iskallia.vault.core.vault.time;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TickStopwatch extends TickClock {
   public static final SupplierKey<TickClock> KEY = SupplierKey.of("tick_stopwatch", TickClock.class).with(Version.v1_19, TickStopwatch::new);
   public static final FieldRegistry FIELDS = TickClock.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> PANIC_TIME = FieldKey.of("panic_time", Integer.class)
      .with(Version.v1_0, Adapters.INT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> LIMIT = FieldKey.of("limit", Integer.class)
      .with(Version.v1_19, Adapters.INT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public TickStopwatch() {
      this.set(PANIC_TIME, Integer.valueOf(400));
      this.set(MODIFIERS, new ClockModifier.List());
   }

   @Override
   public SupplierKey<TickClock> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   protected void tickTime() {
      this.set(LOGICAL_TIME, Integer.valueOf(this.get(LOGICAL_TIME) + 1));
      this.set(DISPLAY_TIME, Integer.valueOf(this.get(DISPLAY_TIME) + 1));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void tickClient() {
      int timeLeft = this.get(LIMIT) - this.get(DISPLAY_TIME);
      if (!this.has(PAUSED) && this.get(DISPLAY_TIME) > 0 && timeLeft < this.get(PANIC_TIME) && this.get(DISPLAY_TIME) % 20 == 0) {
         float pitch = 2.0F - (float)timeLeft / this.get(PANIC_TIME).intValue();
         SimpleSoundInstance sound = SimpleSoundInstance.forUI(ModSounds.TIMER_PANIC_TICK_SFX, pitch);
         Minecraft.getInstance().getSoundManager().play(sound);
      }
   }

   @Override
   protected int getTextColor(int time) {
      int timeLeft = this.get(LIMIT) - time;
      return timeLeft < this.get(PANIC_TIME) && timeLeft % 10 < 5 ? -65536 : super.getTextColor(time);
   }

   @Override
   protected float getRotationTime(int time) {
      float value = super.getRotationTime(time);
      int timeLeft = this.get(LIMIT) - time;
      if (timeLeft <= this.get(PANIC_TIME)) {
         value /= 4.0F;
      }

      return value;
   }
}
