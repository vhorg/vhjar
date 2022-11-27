package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.util.VaultRarity;
import java.util.ArrayList;

public class ChestStat extends DataObject<ChestStat> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<VaultChestType> TYPE = FieldKey.of("type", VaultChestType.class)
      .with(Version.v1_0, Adapter.ofEnum(VaultChestType.class), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> TRAPPED = FieldKey.of("trapped", Void.class).with(Version.v1_0, Adapter.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<VaultRarity> RARITY = FieldKey.of("rarity", VaultRarity.class)
      .with(Version.v1_0, Adapter.ofEnum(VaultRarity.class), DISK.all())
      .register(FIELDS);

   private ChestStat() {
   }

   public static ChestStat ofTrapped(VaultChestType type) {
      return new ChestStat().set(TYPE, type).set(TRAPPED);
   }

   public static ChestStat ofLoot(VaultChestType type, VaultRarity rarity) {
      return new ChestStat().set(TYPE, type).set(RARITY, rarity);
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public static class List extends DataList<ChestStat.List, ChestStat> {
      public List() {
         super(new ArrayList<>(), Adapter.ofCompound(ChestStat::new));
      }
   }
}
