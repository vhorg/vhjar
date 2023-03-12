package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MobsStat extends DataMap<MobsStat, ResourceLocation, MobsStat.Entry> {
   public MobsStat() {
      super(new HashMap<>(), Adapters.IDENTIFIER, CompoundAdapter.of(MobsStat.Entry::new));
   }

   public void onKilled(Entity entity) {
      this.computeIfAbsent(entity.getType().getRegistryName(), v -> new MobsStat.Entry()).modify(MobsStat.Entry.KILLED, count -> count + 1);
   }

   public void onDamageDealt(Entity entity, float amount) {
      this.computeIfAbsent(entity.getType().getRegistryName(), v -> new MobsStat.Entry()).modify(MobsStat.Entry.DAMAGE_DEALT, damage -> damage + amount);
   }

   public void onDamageReceived(Entity entity, float amount) {
      this.computeIfAbsent(entity.getType().getRegistryName(), v -> new MobsStat.Entry()).modify(MobsStat.Entry.DAMAGE_RECEIVED, damage -> damage + amount);
   }

   public static class Entry extends DataObject<MobsStat.Entry> {
      public static final FieldRegistry FIELDS = new FieldRegistry();
      public static final FieldKey<Integer> KILLED = FieldKey.of("killed", Integer.class)
         .with(Version.v1_0, Adapters.INT_SEGMENTED_7, DISK.all())
         .register(FIELDS);
      public static final FieldKey<Float> DAMAGE_DEALT = FieldKey.of("damage_dealt", Float.class)
         .with(Version.v1_0, Adapters.FLOAT, DISK.all())
         .register(FIELDS);
      public static final FieldKey<Float> DAMAGE_RECEIVED = FieldKey.of("damage_received", Float.class)
         .with(Version.v1_0, Adapters.FLOAT, DISK.all())
         .register(FIELDS);

      public Entry() {
         this.set(KILLED, Integer.valueOf(0));
         this.set(DAMAGE_DEALT, Float.valueOf(0.0F));
         this.set(DAMAGE_RECEIVED, Float.valueOf(0.0F));
      }

      @Override
      public FieldRegistry getFields() {
         return FIELDS;
      }
   }
}
