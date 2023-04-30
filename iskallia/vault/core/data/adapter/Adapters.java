package iskallia.vault.core.data.adapter;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.array.ByteArrayAdapter;
import iskallia.vault.core.data.adapter.array.IntArrayAdapter;
import iskallia.vault.core.data.adapter.array.LongArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.OrdinalAdapter;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.data.adapter.basic.StringAdapter;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.data.adapter.basic.UuidAdapter;
import iskallia.vault.core.data.adapter.basic.VoidAdapter;
import iskallia.vault.core.data.adapter.nbt.ByteArrayNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.ByteNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.CollectionNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.CompoundNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.DoubleNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.EndNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.FloatNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.GenericNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.IntArrayNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.IntNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.ListNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.LongArrayNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.LongNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.NumericNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.ShortNbtAdapter;
import iskallia.vault.core.data.adapter.nbt.StringNbtAdapter;
import iskallia.vault.core.data.adapter.number.BigDecimalAdapter;
import iskallia.vault.core.data.adapter.number.BigIntegerAdapter;
import iskallia.vault.core.data.adapter.number.BooleanAdapter;
import iskallia.vault.core.data.adapter.number.BoundedIntAdapter;
import iskallia.vault.core.data.adapter.number.ByteAdapter;
import iskallia.vault.core.data.adapter.number.CharAdapter;
import iskallia.vault.core.data.adapter.number.DoubleAdapter;
import iskallia.vault.core.data.adapter.number.FloatAdapter;
import iskallia.vault.core.data.adapter.number.IntAdapter;
import iskallia.vault.core.data.adapter.number.LongAdapter;
import iskallia.vault.core.data.adapter.number.NumericAdapter;
import iskallia.vault.core.data.adapter.number.SegmentedIntAdapter;
import iskallia.vault.core.data.adapter.number.ShortAdapter;
import iskallia.vault.core.data.adapter.util.ForgeRegistryAdapter;
import iskallia.vault.core.data.adapter.util.IdentifierAdapter;
import iskallia.vault.core.data.adapter.util.ItemStackAdapter;
import iskallia.vault.core.data.adapter.util.ResourceKeyAdapter;
import iskallia.vault.core.data.adapter.util.SerializableWeightedTreeAdapter;
import iskallia.vault.core.vault.enhancement.BreakBlocksEnhancementTask;
import iskallia.vault.core.vault.enhancement.EnhancementTask;
import iskallia.vault.core.vault.enhancement.KillMobsEnhancementTask;
import iskallia.vault.core.vault.enhancement.LootChestsEnhancementTask;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.item.PartialItem;
import iskallia.vault.core.world.data.tile.PartialBlock;
import iskallia.vault.core.world.data.tile.PartialBlockProperties;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.loot.entry.LootEntry;
import iskallia.vault.core.world.loot.entry.ReferenceLootEntry;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.template.data.DirectTemplateEntry;
import iskallia.vault.core.world.template.data.IndirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.skill.base.Skill;
import java.nio.charset.StandardCharsets;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Adapters {
   public static final BooleanAdapter BOOLEAN = new BooleanAdapter(false);
   public static final NumericAdapter NUMERIC = new NumericAdapter(false);
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
   public static final BigIntegerAdapter BIG_INTEGER = new BigIntegerAdapter(false);
   public static final BigDecimalAdapter BIG_DECIMAL = new BigDecimalAdapter(false);
   public static final VoidAdapter<?> VOID = new VoidAdapter();
   public static final StringAdapter UTF_8 = new StringAdapter(StandardCharsets.UTF_8, false);
   public static final UuidAdapter UUID = new UuidAdapter(false);
   public static final GenericNbtAdapter GENERIC_NBT = new GenericNbtAdapter(false);
   public static final NumericNbtAdapter NUMERIC_NBT = new NumericNbtAdapter(false);
   public static final CollectionNbtAdapter COLLECTION_NBT = new CollectionNbtAdapter(false);
   public static final EndNbtAdapter END_NBT = new EndNbtAdapter(false);
   public static final ByteNbtAdapter BYTE_NBT = new ByteNbtAdapter(false);
   public static final ShortNbtAdapter SHORT_NBT = new ShortNbtAdapter(false);
   public static final IntNbtAdapter INT_NBT = new IntNbtAdapter(false);
   public static final LongNbtAdapter LONG_NBT = new LongNbtAdapter(false);
   public static final FloatNbtAdapter FLOAT_NBT = new FloatNbtAdapter(false);
   public static final DoubleNbtAdapter DOUBLE_NBT = new DoubleNbtAdapter(false);
   public static final ByteArrayNbtAdapter BYTE_ARRAY_NBT = new ByteArrayNbtAdapter(false);
   public static final StringNbtAdapter STRING_NBT = new StringNbtAdapter(false);
   public static final ListNbtAdapter LIST_NBT = new ListNbtAdapter(false);
   public static final CompoundNbtAdapter COMPOUND_NBT = new CompoundNbtAdapter(false);
   public static final IntArrayNbtAdapter INT_ARRAY_NBT = new IntArrayNbtAdapter(false);
   public static final LongArrayNbtAdapter LONG_ARRAY_NBT = new LongArrayNbtAdapter(false);
   public static final IdentifierAdapter IDENTIFIER = new IdentifierAdapter(false);
   public static final ItemStackAdapter ITEM_STACK = new ItemStackAdapter(false);
   public static final PartialBlock.Adapter PARTIAL_BLOCK = new PartialBlock.Adapter();
   public static final PartialBlockProperties.Adapter PARTIAL_BLOCK_PROPERTIES = new PartialBlockProperties.Adapter();
   public static final PartialBlockState.Adapter PARTIAL_BLOCK_STATE = new PartialBlockState.Adapter();
   public static final PartialCompoundNbt.Adapter PARTIAL_BLOCK_ENTITY = new PartialCompoundNbt.Adapter();
   public static final PartialTile.Adapter PARTIAL_TILE = new PartialTile.Adapter();
   public static final PartialItem.Adapter PARTIAL_ITEM = new PartialItem.Adapter();
   public static final ItemPredicate.Adapter PARTIAL_STACK = new ItemPredicate.Adapter();
   public static final TilePredicate.Adapter TILE_PREDICATE = new TilePredicate.Adapter();
   public static final EntityPredicate.Adapter ENTITY_PREDICATE = new EntityPredicate.Adapter();
   public static final ItemPredicate.Adapter ITEM_PREDICATE = new ItemPredicate.Adapter();
   public static final ForgeRegistryAdapter<Block> BLOCK = new ForgeRegistryAdapter(() -> ForgeRegistries.BLOCKS, false);
   public static final ForgeRegistryAdapter<Item> ITEM = new ForgeRegistryAdapter(() -> ForgeRegistries.ITEMS, false);
   public static final ForgeRegistryAdapter<Enchantment> ENCHANTMENT = new ForgeRegistryAdapter(() -> ForgeRegistries.ENCHANTMENTS, false);
   public static final ForgeRegistryAdapter<MobEffect> EFFECT = new ForgeRegistryAdapter(() -> ForgeRegistries.MOB_EFFECTS, false);
   public static final ForgeRegistryAdapter<Attribute> ATTRIBUTE = new ForgeRegistryAdapter(() -> ForgeRegistries.ATTRIBUTES, false);
   public static final ForgeRegistryAdapter<VaultGearAttribute<?>> GEAR_ATTRIBUTE = new ForgeRegistryAdapter(VaultGearAttributeRegistry::getRegistry, false);
   public static final SerializableAdapter<CrystalData, CompoundTag, JsonObject> CRYSTAL = new SerializableAdapter<>(CrystalData::empty, true);
   public static final Skill.Adapter SKILL = new Skill.Adapter();
   public static TypeSupplierAdapter<EnhancementTask<?>> ENHANCEMENT_TASK = new TypeSupplierAdapter<BreakBlocksEnhancementTask>("type", true)
      .<TypeSupplierAdapter<KillMobsEnhancementTask>>register("break_blocks", BreakBlocksEnhancementTask.class, BreakBlocksEnhancementTask::new)
      .<TypeSupplierAdapter<LootChestsEnhancementTask>>register("kill_mobs", KillMobsEnhancementTask.class, KillMobsEnhancementTask::new)
      .register("loot_chests", LootChestsEnhancementTask.class, LootChestsEnhancementTask::new);
   public static TypeSupplierAdapter<EnhancementTask.Config<?>> ENHANCEMENT_CONFIG = new TypeSupplierAdapter<BreakBlocksEnhancementTask.Config>("type", true)
      .<TypeSupplierAdapter<KillMobsEnhancementTask.Config>>register(
         "break_blocks", BreakBlocksEnhancementTask.Config.class, BreakBlocksEnhancementTask.Config::new
      )
      .<TypeSupplierAdapter<LootChestsEnhancementTask.Config>>register("kill_mobs", KillMobsEnhancementTask.Config.class, KillMobsEnhancementTask.Config::new)
      .register("loot_chests", LootChestsEnhancementTask.Config.class, LootChestsEnhancementTask.Config::new);
   public static SerializableWeightedTreeAdapter<LootEntry, LootPool> LOOT_POOL = new SerializableWeightedTreeAdapter<LootEntry, LootPool>(LootPool::new)
      .register("item", ItemLootEntry.class, ItemLootEntry::new)
      .register("reference", ReferenceLootEntry.class, ReferenceLootEntry::new);
   public static SerializableWeightedTreeAdapter<TemplateEntry, TemplatePool> TEMPLATE_POOL = new SerializableWeightedTreeAdapter<TemplateEntry, TemplatePool>(
         TemplatePool::new
      )
      .register("value", DirectTemplateEntry.class, DirectTemplateEntry::new)
      .register("reference", IndirectTemplateEntry.class, IndirectTemplateEntry::new);

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
