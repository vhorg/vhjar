package iskallia.vault.core.vault.time.modifier;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.world.data.DiscoveredRelicsData;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class RelicExtension extends ClockModifier {
   public static final SupplierKey<ClockModifier> KEY = SupplierKey.of("relic", ClockModifier.class).with(Version.v1_0, RelicExtension::new);
   public static final FieldRegistry FIELDS = ClockModifier.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class).with(Version.v1_0, Adapter.ofUUID(), DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> INCREMENT_PER_RELIC = FieldKey.of("increment_per_relic", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(7), DISK.all())
      .register(FIELDS);
   public static final FieldKey<RelicExtension.Relics> RELICS = FieldKey.of("relics", RelicExtension.Relics.class)
      .with(Version.v1_0, Adapter.ofCompound(RelicExtension.Relics::new), DISK.all())
      .register(FIELDS);

   protected RelicExtension() {
   }

   public RelicExtension(UUID player, int incrementPerRelic) {
      this.set(PLAYER, player);
      this.set(INCREMENT_PER_RELIC, Integer.valueOf(incrementPerRelic));
      this.set(RELICS, new RelicExtension.Relics());
      this.get(RELICS).addAll(DiscoveredRelicsData.getRelics(player));
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
      clock.set(TickClock.DISPLAY_TIME, Integer.valueOf(clock.get(TickClock.DISPLAY_TIME) + this.get(INCREMENT_PER_RELIC) * this.get(RELICS).size()));
      this.set(CONSUMED);
   }

   private static class Relics extends DataList<RelicExtension.Relics, ResourceLocation> {
      public Relics() {
         super(new ArrayList<>(), Adapter.ofIdentifier());
      }
   }
}
