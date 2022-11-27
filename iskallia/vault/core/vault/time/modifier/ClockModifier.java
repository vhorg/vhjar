package iskallia.vault.core.vault.time.modifier;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.time.TickClock;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;

public abstract class ClockModifier extends DataObject<ClockModifier> implements ISupplierKey<ClockModifier> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Void> CONSUMED = FieldKey.of("consumed", Void.class).with(Version.v1_0, Adapter.ofVoid(), DISK.all()).register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public final void tick(ServerLevel world, TickClock clock) {
      if (!this.has(CONSUMED)) {
         CommonEvents.CLOCK_MODIFIER.invoke(clock, this);
         if (!this.has(CONSUMED)) {
            this.apply(world, clock);
         }
      }
   }

   protected abstract void apply(ServerLevel var1, TickClock var2);

   public static class List extends DataList<ClockModifier.List, ClockModifier> {
      public List() {
         super(new ArrayList<>(), Adapter.ofRegistryValue(() -> VaultRegistry.CLOCK_MODIFIER, ISupplierKey::getKey, Supplier::get));
      }
   }
}
