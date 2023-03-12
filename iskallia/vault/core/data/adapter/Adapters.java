package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.array.ByteArrayAdapter;
import iskallia.vault.core.data.adapter.array.IntArrayAdapter;
import iskallia.vault.core.data.adapter.array.LongArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.OrdinalAdapter;
import iskallia.vault.core.data.adapter.basic.StringAdapter;
import iskallia.vault.core.data.adapter.basic.UuidAdapter;
import iskallia.vault.core.data.adapter.basic.VoidAdapter;
import iskallia.vault.core.data.adapter.nbt.CompoundTagAdapter;
import iskallia.vault.core.data.adapter.nbt.IntTagAdapter;
import iskallia.vault.core.data.adapter.primitive.BooleanAdapter;
import iskallia.vault.core.data.adapter.primitive.BoundedIntAdapter;
import iskallia.vault.core.data.adapter.primitive.ByteAdapter;
import iskallia.vault.core.data.adapter.primitive.CharAdapter;
import iskallia.vault.core.data.adapter.primitive.DoubleAdapter;
import iskallia.vault.core.data.adapter.primitive.FloatAdapter;
import iskallia.vault.core.data.adapter.primitive.IntAdapter;
import iskallia.vault.core.data.adapter.primitive.LongAdapter;
import iskallia.vault.core.data.adapter.primitive.SegmentedIntAdapter;
import iskallia.vault.core.data.adapter.primitive.ShortAdapter;
import iskallia.vault.core.data.adapter.util.ForgeRegistryAdapter;
import iskallia.vault.core.data.adapter.util.IdentifierAdapter;
import iskallia.vault.core.data.adapter.util.ItemStackAdapter;
import iskallia.vault.core.data.adapter.util.ResourceKeyAdapter;
import iskallia.vault.core.vault.enhancement.BreakBlocksEnhancementTask;
import iskallia.vault.core.vault.enhancement.EnhancementTask;
import iskallia.vault.core.vault.enhancement.KillMobsEnhancementTask;
import iskallia.vault.core.vault.enhancement.LootChestsEnhancementTask;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.ObjectEntryAdapter;
import java.nio.charset.StandardCharsets;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Adapters {
   public static final BooleanAdapter BOOLEAN = new BooleanAdapter(false);
   public static final ByteAdapter BYTE = new ByteAdapter(false);
   public static final ByteArrayAdapter BYTE_ARRAY = new ByteArrayAdapter(BYTE, false);
   public static final ShortAdapter SHORT = new ShortAdapter(false);
   public static final CharAdapter CHAR = new CharAdapter(false);
   public static final IntAdapter INT = new IntAdapter(false);
   public static final IntArrayAdapter INT_ARRAY = new IntArrayAdapter(INT, false);
   public static final SegmentedIntAdapter INT_SEGMENTED_3 = new SegmentedIntAdapter(3, false);
   public static final SegmentedIntAdapter INT_SEGMENTED_7 = new SegmentedIntAdapter(7, false);
   public static final IntRoll.Adapter INT_ROLL = new IntRoll.Adapter();
   public static final FloatAdapter FLOAT = new FloatAdapter(false);
   public static final LongAdapter LONG = new LongAdapter(false);
   public static final LongArrayAdapter LONG_ARRAY = new LongArrayAdapter(LONG, false);
   public static final DoubleAdapter DOUBLE = new DoubleAdapter(false);
   public static final VoidAdapter<?> VOID = new VoidAdapter();
   public static final StringAdapter UTF_8 = new StringAdapter(StandardCharsets.UTF_8, false);
   public static final UuidAdapter UUID = new UuidAdapter(false);
   public static final IntTagAdapter INT_TAG = new IntTagAdapter(false);
   public static final CompoundTagAdapter COMPOUND_TAG = new CompoundTagAdapter(false);
   public static final IdentifierAdapter IDENTIFIER = new IdentifierAdapter(false);
   public static final ItemStackAdapter ITEM_STACK = new ItemStackAdapter(false);
   public static final ForgeRegistryAdapter<Item> ITEM = new ForgeRegistryAdapter(ForgeRegistries.ITEMS, false);
   public static ObjectEntryAdapter<EnhancementTask<?>> ENHANCEMENT_TASK = new ObjectEntryAdapter<BreakBlocksEnhancementTask>("type")
      .register("break_blocks", BreakBlocksEnhancementTask.class, BreakBlocksEnhancementTask::new)
      .register("kill_mobs", KillMobsEnhancementTask.class, KillMobsEnhancementTask::new)
      .register("loot_chests", LootChestsEnhancementTask.class, LootChestsEnhancementTask::new);
   public static ObjectEntryAdapter<EnhancementTask.Config<?>> ENHANCEMENT_CONFIG = new ObjectEntryAdapter<BreakBlocksEnhancementTask.Config>("type")
      .register("break_blocks", BreakBlocksEnhancementTask.Config.class, BreakBlocksEnhancementTask.Config::new)
      .register("kill_mobs", KillMobsEnhancementTask.Config.class, KillMobsEnhancementTask.Config::new)
      .register("loot_chests", LootChestsEnhancementTask.Config.class, LootChestsEnhancementTask.Config::new);

   public static BoundedIntAdapter ofBoundedInt(int bound) {
      return new BoundedIntAdapter(0, bound - 1, false);
   }

   public static BoundedIntAdapter ofBoundedInt(int min, int max) {
      return new BoundedIntAdapter(min, max, false);
   }

   public static <T> ArrayAdapter<T> ofArray(IntFunction<T[]> constructor, Object elementAdapter) {
      return new ArrayAdapter<>(constructor, elementAdapter, () -> null, false);
   }

   public static <T> VoidAdapter<T> ofVoid() {
      return new VoidAdapter<>();
   }

   public static <E extends Enum<E>> EnumAdapter<E> ofEnum(Class<E> type, EnumAdapter.Mode mode) {
      return new EnumAdapter<>(type, mode, false);
   }

   public static <T> OrdinalAdapter<T> ofOrdinal(ToIntFunction<T> mapper, T... array) {
      return new OrdinalAdapter<>(mapper, false, array);
   }

   public static <T> ResourceKeyAdapter<T> ofResourceKey(ResourceKey<Registry<T>> registry) {
      return new ResourceKeyAdapter<>(registry, false);
   }
}
