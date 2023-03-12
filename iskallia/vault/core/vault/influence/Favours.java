package iskallia.vault.core.vault.influence;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import java.util.UUID;

public class Favours extends Modifiers {
   public static final FieldRegistry FIELDS = Modifiers.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class).with(Version.v1_5, Adapters.UUID, DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> REPUTATION = FieldKey.of("reputation", Integer.class)
      .with(Version.v1_5, Adapters.INT_SEGMENTED_7, DISK.all())
      .register(FIELDS);

   public Favours() {
   }

   public Favours(UUID player, int reputation) {
      this.set(PLAYER, player);
      this.set(REPUTATION, Integer.valueOf(reputation));
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public ModifierContext getContext(Modifiers.Entry entry) {
      return super.getContext(entry).set(ModifierContext.REPUTATION, this.get(REPUTATION)).set(ModifierContext.TARGET, this.get(PLAYER));
   }
}
