package iskallia.vault.world.vault.modifier.spi;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import java.util.Optional;
import java.util.UUID;

public class ModifierContext extends DataObject<ModifierContext> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<UUID> UUID = FieldKey.of("uuid", UUID.class).with(Version.v1_0, Adapter.ofUUID(), DISK.all().or(CLIENT.all())).register(FIELDS);
   public static final FieldKey<Integer> TICKS_LEFT = FieldKey.of("ticks_left", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(7), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public UUID getUUID() {
      return this.get(UUID);
   }

   public Optional<Integer> getTimeLeft() {
      return this.getOptional(TICKS_LEFT);
   }

   public boolean hasExpired() {
      return this.has(TICKS_LEFT) && this.get(TICKS_LEFT) <= 0;
   }
}