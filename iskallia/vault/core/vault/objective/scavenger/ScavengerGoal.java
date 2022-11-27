package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.adapter.DirectAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.objective.Objective;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ScavengerGoal extends DataObject<ScavengerGoal> {
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Item> ITEM = FieldKey.of("item", Item.class)
      .with(
         Version.v1_0,
         new DirectAdapter<>(
            (buffer, context, value) -> buffer.writeIdentifier(value.getRegistryName()),
            (buffer, context) -> (Item)ForgeRegistries.ITEMS.getValue(buffer.readIdentifier())
         ),
         DISK.all().or(CLIENT.all())
      )
      .register(FIELDS);
   public static final FieldKey<Integer> TOTAL = FieldKey.of("total", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> CURRENT = FieldKey.of("current", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> ICON = FieldKey.of("secondary_icon", ResourceLocation.class)
      .with(Version.v1_0, Adapter.ofIdentifier(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> COLOR = FieldKey.of("color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected ScavengerGoal() {
   }

   public ScavengerGoal(Item item, int count, ResourceLocation icon, int color) {
      this.set(ITEM, item);
      this.set(TOTAL, Integer.valueOf(count));
      this.set(CURRENT, Integer.valueOf(0));
      this.set(ICON, icon);
      this.set(COLOR, Integer.valueOf(color));
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean isCompleted() {
      return this.get(CURRENT) >= this.get(TOTAL);
   }

   public boolean consume(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (this.get(CURRENT) >= this.get(TOTAL)) {
         return false;
      } else if (this.get(ITEM) != stack.getItem()) {
         return false;
      } else {
         int amount = Math.min(this.get(TOTAL) - this.get(CURRENT), stack.getCount());
         stack.shrink(amount);
         this.modify(CURRENT, value -> value + amount);
         return true;
      }
   }

   public boolean canConsume(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         return this.get(CURRENT) >= this.get(TOTAL) ? false : this.get(ITEM) == stack.getItem();
      }
   }

   public static class ObjList extends DataList<ScavengerGoal.ObjList, ScavengerGoal> {
      public ObjList() {
         super(new ArrayList<>(), Adapter.ofCompound(ScavengerGoal::new));
      }

      public boolean areAllCompleted() {
         return this.stream().allMatch(ScavengerGoal::isCompleted);
      }
   }
}
