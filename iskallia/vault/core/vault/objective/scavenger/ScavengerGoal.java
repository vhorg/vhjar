package iskallia.vault.core.vault.objective.scavenger;

import com.google.common.collect.Iterators;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.data.item.PartialStack;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.item.crystal.data.serializable.IBitSerializable;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ScavengerGoal extends DataObject<ScavengerGoal> {
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   private static final FieldKey<Item> ITEM = FieldKey.of("item", Item.class).with(Version.v1_0, Adapters.ITEM, DISK.all().or(CLIENT.all())).register(FIELDS);
   public static final FieldKey<Integer> TOTAL = FieldKey.of("total", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> BASE_TOTAL = FieldKey.of("base_total", Integer.class)
      .with(Version.v1_25, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> CURRENT = FieldKey.of("current", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   private static final FieldKey<ResourceLocation> ICON = FieldKey.of("secondary_icon", ResourceLocation.class)
      .with(Version.v1_0, Adapters.IDENTIFIER, DISK.all().or(CLIENT.all()))
      .with(Version.v1_19, Adapters.IDENTIFIER.asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   private static final FieldKey<Integer> COLOR = FieldKey.of("color", Integer.class)
      .with(Version.v1_0, Adapters.INT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ScavengerGoal.Entry.ObjList> ENTRIES = FieldKey.of("entries", ScavengerGoal.Entry.ObjList.class)
      .with(Version.v1_19, CompoundAdapter.of(() -> new ScavengerGoal.Entry.ObjList(Version.v1_19)), DISK.all().or(CLIENT.all()))
      .with(Version.v1_20, CompoundAdapter.of(() -> new ScavengerGoal.Entry.ObjList(Version.v1_20)), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected ScavengerGoal() {
      this.set(ENTRIES, new ScavengerGoal.Entry.ObjList(Version.v1_20));
   }

   public ScavengerGoal(int count) {
      this.set(TOTAL, Integer.valueOf(count));
      this.set(BASE_TOTAL, Integer.valueOf(count));
      this.set(CURRENT, Integer.valueOf(0));
      this.set(ENTRIES, new ScavengerGoal.Entry.ObjList(Version.v1_20));
   }

   public Iterator<ScavengerGoal.Entry> getEntries() {
      Iterator<ScavengerGoal.Entry> iterator = null;
      if (this.has(ITEM)) {
         iterator = Iterators.singletonIterator(
            new ScavengerGoal.Entry(this.get(ENTRIES).version, new ItemStack((ItemLike)this.get(ITEM)), this.get(ICON), this.get(COLOR))
         );
      }

      if (this.has(ENTRIES) && !this.get(ENTRIES).isEmpty()) {
         if (iterator == null) {
            iterator = this.get(ENTRIES).iterator();
         } else {
            iterator = Iterators.concat(iterator, this.get(ENTRIES).iterator());
         }
      }

      return iterator;
   }

   public ScavengerGoal put(ItemStack item, ResourceLocation icon, int color) {
      return this.put(new ScavengerGoal.Entry(this.get(ENTRIES).version, item, icon, color));
   }

   public ScavengerGoal put(ScavengerGoal.Entry entry) {
      this.get(ENTRIES).add(entry);
      return this;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean isCompleted() {
      return this.get(CURRENT) >= this.get(TOTAL);
   }

   public void tick(VirtualWorld world, Vault vault) {
      this.ifPresent(BASE_TOTAL, baseTotal -> {
         double increase = CommonEvents.OBJECTIVE_TARGET.invoke(world, vault, 0.0).getIncrease();
         this.set(TOTAL, Integer.valueOf((int)Math.round(baseTotal.intValue() * (1.0 + increase))));
      });
   }

   public boolean consume(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (this.get(CURRENT) >= this.get(TOTAL)) {
         return false;
      } else {
         this.getEntries().forEachRemaining(entry -> {
            if (PartialStack.of(entry.getStack()).isSubsetOf(stack)) {
               int amount = Math.min(this.get(TOTAL) - this.get(CURRENT), stack.getCount());
               stack.shrink(amount);
               this.modify(CURRENT, value -> value + amount);
            }
         });
         return true;
      }
   }

   public ScavengerGoal merge(ScavengerGoal other) {
      ScavengerGoal copy = this.copy();
      copy.set(CURRENT, Integer.valueOf(this.getOr(CURRENT, Integer.valueOf(0)) + other.getOr(CURRENT, Integer.valueOf(0))));
      copy.set(TOTAL, Integer.valueOf(this.getOr(TOTAL, Integer.valueOf(0)) + other.getOr(TOTAL, Integer.valueOf(0))));
      copy.set(BASE_TOTAL, Integer.valueOf(this.getOr(BASE_TOTAL, Integer.valueOf(0)) + other.getOr(BASE_TOTAL, Integer.valueOf(0))));
      other.getEntries().forEachRemaining(copy::put);
      return copy;
   }

   public ScavengerGoal copy() {
      ScavengerGoal copy = new ScavengerGoal();
      copy.set(CURRENT, this.get(CURRENT));
      copy.set(TOTAL, this.get(TOTAL));
      copy.set(BASE_TOTAL, this.get(BASE_TOTAL));
      this.getEntries().forEachRemaining(copy::put);
      return copy;
   }

   public static class Entry implements IBitSerializable {
      private Version version;
      private ItemStack item;
      private ResourceLocation icon;
      private int color;

      private Entry(Version version) {
         this.version = version;
      }

      public Entry(Version version, ItemStack item, ResourceLocation icon, int color) {
         this.version = version;
         this.item = item;
         this.icon = icon;
         this.color = color;
      }

      public ItemStack getStack() {
         return this.item;
      }

      public ItemStack getStack(int count) {
         ItemStack stack = this.item.copy();
         stack.setCount(count);
         return stack;
      }

      public ResourceLocation getIcon() {
         return this.icon;
      }

      public int getColor() {
         return this.color;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         if (this.version.isOlderThan(Version.v1_20)) {
            Adapters.ITEM.writeBits((IForgeRegistryEntry)this.item.getItem(), buffer);
         } else {
            Adapters.ITEM_STACK.writeBits(this.item, buffer);
         }

         Adapters.IDENTIFIER.writeBits(this.icon, buffer);
         Adapters.INT.writeBits(Integer.valueOf(this.color), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         if (this.version.isOlderThan(Version.v1_20)) {
            this.item = new ItemStack((ItemLike)Adapters.ITEM.readBits(buffer).orElseThrow());
         } else {
            this.item = Adapters.ITEM_STACK.readBits(buffer).orElseThrow();
         }

         this.icon = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
         this.color = Adapters.INT.readBits(buffer).orElseThrow();
      }

      public static class ObjList extends DataList<ScavengerGoal.Entry.ObjList, ScavengerGoal.Entry> {
         private final Version version;

         public ObjList(Version version) {
            super(new ArrayList<>(), Adapters.of(() -> new ScavengerGoal.Entry(version), false));
            this.version = version;
         }
      }
   }

   public static class ObjList extends DataList<ScavengerGoal.ObjList, ScavengerGoal> {
      public ObjList() {
         super(new ArrayList<>(), CompoundAdapter.of(ScavengerGoal::new));
      }

      public boolean areAllCompleted() {
         return this.stream().allMatch(ScavengerGoal::isCompleted);
      }
   }
}
