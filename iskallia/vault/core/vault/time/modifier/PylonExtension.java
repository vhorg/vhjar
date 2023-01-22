package iskallia.vault.core.vault.time.modifier;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.time.TickClock;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class PylonExtension extends ClockModifier {
   public static final SupplierKey<ClockModifier> KEY = SupplierKey.of("pylon", ClockModifier.class).with(Version.v1_6, PylonExtension::new);
   public static final FieldRegistry FIELDS = ClockModifier.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class).with(Version.v1_6, Adapter.ofUUID(), DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> INCREMENT = FieldKey.of("increment", Integer.class)
      .with(Version.v1_6, Adapter.ofSegmentedInt(7), DISK.all())
      .register(FIELDS);

   protected PylonExtension() {
   }

   public PylonExtension(Player player, int increment) {
      this.set(PLAYER, player.getUUID());
      this.set(INCREMENT, Integer.valueOf(increment));
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
      clock.set(TickClock.DISPLAY_TIME, Integer.valueOf(clock.get(TickClock.DISPLAY_TIME) + this.get(INCREMENT)));
      this.set(CONSUMED);
   }
}
