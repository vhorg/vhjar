package iskallia.vault.core.vault.time.modifier;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.init.ModEffects;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class VoidFluidExtension extends ClockModifier {
   public static final SupplierKey<ClockModifier> KEY = SupplierKey.of("void_fluid", ClockModifier.class).with(Version.v1_0, VoidFluidExtension::new);
   public static final FieldRegistry FIELDS = ClockModifier.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class).with(Version.v1_0, Adapters.UUID, DISK.all()).register(FIELDS);

   protected VoidFluidExtension() {
   }

   public VoidFluidExtension(Player player) {
      this.set(PLAYER, player.getUUID());
   }

   @Override
   public SupplierKey<ClockModifier> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   protected void apply(ServerLevel world, TickClock clock) {
      ServerPlayer player = world.getServer().getPlayerList().getPlayer(this.get(PLAYER));
      if (player == null) {
         this.set(CONSUMED);
      } else {
         MobEffectInstance effect = player.getEffect(ModEffects.TIMER_ACCELERATION);
         if (effect != null && effect.getAmplifier() >= 0) {
            int decrement = (effect.getAmplifier() + 1) * 6;
            clock.set(TickClock.DISPLAY_TIME, Integer.valueOf(clock.get(TickClock.DISPLAY_TIME) - decrement));
         } else {
            this.set(CONSUMED);
         }
      }
   }
}
