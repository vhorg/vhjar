package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.util.VaultRarity;
import java.util.ArrayList;
import java.util.Objects;

public class ChestStat extends DataObject<ChestStat> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<VaultChestType> TYPE = FieldKey.of("type", VaultChestType.class)
      .with(Version.v1_0, Adapters.ofEnum(VaultChestType.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> TRAPPED = FieldKey.of("trapped", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<VaultRarity> RARITY = FieldKey.of("rarity", VaultRarity.class)
      .with(Version.v1_0, Adapters.ofEnum(VaultRarity.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);

   private ChestStat() {
   }

   public static ChestStat ofTrapped(VaultChestType type) {
      return new ChestStat().set(TYPE, type).set(TRAPPED);
   }

   public static ChestStat ofLoot(VaultChestType type, VaultRarity rarity) {
      return new ChestStat().set(TYPE, type).setIf(RARITY, rarity, Objects::nonNull);
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public static class List extends DataList<ChestStat.List, ChestStat> {
      public List() {
         super(new ArrayList<>(), CompoundAdapter.of(ChestStat::new));
      }
   }
}
