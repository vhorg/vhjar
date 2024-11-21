package iskallia.vault.gear.data;

import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GearDataCache {
   private final ItemStack stack;

   private GearDataCache(ItemStack stack) {
      this.stack = stack;
   }

   public static GearDataCache of(ItemStack stack) {
      return !AttributeGearData.hasData(stack) ? new GearDataCache(ItemStack.EMPTY) : new GearDataCache(stack);
   }

   public static void removeCache(ItemStack stack) {
      if (stack.hasTag() && stack.getOrCreateTag().contains("clientCache")) {
         stack.getOrCreateTag().remove("clientCache");
      }
   }

   public static void createCache(ItemStack stack) {
      GearDataCache cache = of(stack);
      cache.getState();
      cache.getRarity();
      cache.getGearModel();
      cache.getGearColor(0);
      cache.getGearRollType();
      cache.getGearName();
      cache.getGearColorComponents();
      cache.getJewelColorComponents();
   }

   private CompoundTag cacheTag() {
      return this.stack.isEmpty() ? new CompoundTag() : this.stack.getOrCreateTagElement("clientCache");
   }

   @Nonnull
   private <T> GearDataCache.CacheResult<T> getOptionalCache(String key, Function<Tag, T> nbtRead) {
      int tagType = this.cacheTag().getTagType(key);
      if (tagType == 0) {
         return GearDataCache.CacheResult.miss();
      } else {
         return tagType == 7 ? GearDataCache.CacheResult.none() : GearDataCache.CacheResult.hit(nbtRead.apply(this.cacheTag().get(key)));
      }
   }

   private void setOptionalCache(String key, @Nullable Tag value) {
      if (value == null) {
         this.cacheTag().put(key, new ByteArrayTag(new byte[0]));
      } else {
         this.cacheTag().put(key, value);
      }
   }

   private <R, T> T queryCache(
      String key,
      Function<Tag, R> cacheRead,
      Function<R, Tag> cacheWrite,
      T cacheMissDefault,
      Function<R, T> cacheHitTransform,
      Function<ItemStack, R> cacheInit
   ) {
      if (this.stack.isEmpty()) {
         return cacheMissDefault;
      } else {
         GearDataCache.CacheResult<R> cacheResult = this.getOptionalCache(key, cacheRead);
         if (cacheResult.hitHasNoValue()) {
            return cacheMissDefault;
         } else if (cacheResult.isHit()) {
            return cacheHitTransform.apply(cacheResult.value());
         } else {
            R rawValue = cacheInit.apply(this.stack);
            if (rawValue == null) {
               this.setOptionalCache(key, null);
               return cacheMissDefault;
            } else {
               this.setOptionalCache(key, cacheWrite.apply(rawValue));
               return cacheHitTransform.apply(rawValue);
            }
         }
      }
   }

   private Boolean queryBooleanCache(String key, boolean defaultValue, Function<ItemStack, Boolean> cacheInit) {
      return this.queryCache(key, tag -> ((ByteTag)tag).getAsByte() == 1, bool -> ByteTag.valueOf(bool), defaultValue, Function.identity(), cacheInit);
   }

   private Integer queryIntCache(String key, int defaultValue, Function<ItemStack, Integer> cacheInit) {
      return this.queryCache(key, tag -> ((IntTag)tag).getAsInt(), IntTag::valueOf, defaultValue, Function.identity(), cacheInit);
   }

   private <T extends Enum<T>> T queryEnumCache(String key, Class<T> enumClass, Function<ItemStack, Integer> cacheInit) {
      return this.queryCache(key, tag -> ((IntTag)tag).getAsInt(), IntTag::valueOf, null, ordinal -> MiscUtils.getEnumEntry(enumClass, ordinal), cacheInit);
   }

   public boolean hasModifierOfCategory(VaultGearModifier.AffixCategory category) {
      return this.queryCache(
         String.format("hasModifier%s", category.name()),
         tag -> tag.getType() == ByteTag.TYPE && ((ByteTag)tag).getAsByte() == 1,
         ByteTag::valueOf,
         false,
         Boolean::booleanValue,
         stack -> AttributeGearData.read(this.stack) instanceof VaultGearData vgd
            ? VaultGearData.Type.ALL
               .getAttributeSource(vgd)
               .filter(attribute -> attribute instanceof VaultGearModifier)
               .map(attribute -> (VaultGearModifier)attribute)
               .anyMatch(modifier -> modifier.hasCategory(category))
            : false
      );
   }

   @Nullable
   public VaultGearState getState() {
      return this.queryEnumCache(
         "state",
         VaultGearState.class,
         stack -> {
            AttributeGearData data = AttributeGearData.read(this.stack);
            return data instanceof VaultGearData gearData
               ? gearData.getState().ordinal()
               : data.getFirstValue(ModGearAttributes.STATE).map(Enum::ordinal).orElse(null);
         }
      );
   }

   @Nullable
   public VaultGearRarity getRarity() {
      return this.queryEnumCache(
         "rarity", VaultGearRarity.class, stack -> AttributeGearData.read(this.stack) instanceof VaultGearData gearData ? gearData.getRarity().ordinal() : null
      );
   }

   public Optional<ResourceLocation> getGearModel() {
      return this.queryCache(
         "model", Tag::getAsString, StringTag::valueOf, Optional.empty(), modelStr -> Optional.of(new ResourceLocation(modelStr)), stack -> {
            AttributeGearData data = AttributeGearData.read(this.stack);
            return data.getFirstValue(ModGearAttributes.GEAR_MODEL).<String>map(ResourceLocation::toString).orElse(null);
         }
      );
   }

   public int getGearColor(int defaultColor) {
      return this.queryIntCache("color", defaultColor, stack -> {
         AttributeGearData data = AttributeGearData.read(this.stack);
         Optional<Integer> colorOpt = data.getFirstValue(ModGearAttributes.GEAR_COLOR);
         return colorOpt.orElse(null);
      });
   }

   @Nullable
   public String getGearRollType() {
      return this.queryCache("rollType", Tag::getAsString, StringTag::valueOf, null, Function.identity(), stack -> {
         AttributeGearData data = AttributeGearData.read(this.stack);
         Optional<String> rollTypeOpt = data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE);
         return rollTypeOpt.orElse(null);
      });
   }

   @Nullable
   public String getGearName() {
      return this.queryCache("gearName", Tag::getAsString, StringTag::valueOf, null, Function.identity(), stack -> {
         AttributeGearData data = AttributeGearData.read(stack);
         Optional<String> nameOpt = data.getFirstValue(ModGearAttributes.GEAR_NAME);
         return nameOpt.orElse(null);
      });
   }

   public boolean hasAttribute(VaultGearAttribute<?> attr) {
      return this.queryBooleanCache("present" + attr.getRegistryName(), false, stack -> AttributeGearData.<AttributeGearData>read(stack).hasAttribute(attr));
   }

   @Nullable
   public List<Integer> getGearColorComponents() {
      return this.queryCache(
         "colors",
         tag -> ((IntArrayTag)tag).getAsIntArray(),
         IntArrayTag::new,
         null,
         components -> Arrays.stream(components).boxed().toList(),
         stack -> {
            if (AttributeGearData.read(this.stack) instanceof VaultGearData gearData) {
               List<Integer> components = new ArrayList<>();
               components.add(gearData.getRarity().getColor().getValue());
               gearData.getAllAttributes()
                  .filter(attrInstance -> attrInstance instanceof VaultGearModifier)
                  .map(attrInstance -> (VaultGearModifier)attrInstance)
                  .filter(modifier -> modifier.hasCategory(VaultGearModifier.AffixCategory.LEGENDARY))
                  .findFirst()
                  .ifPresent(modifier -> components.add(15853364));
               return components.stream().mapToInt(Integer::intValue).toArray();
            } else {
               return null;
            }
         }
      );
   }

   @Nullable
   public List<Integer> getJewelColorComponents() {
      return this.queryCache(
         "jcolors", tag -> ((IntArrayTag)tag).getAsIntArray(), IntArrayTag::new, null, components -> Arrays.stream(components).boxed().toList(), stack -> {
            if (!(AttributeGearData.read(this.stack) instanceof VaultGearData gearData)) {
               return null;
            } else {
               ArrayList components = new ArrayList();

               for (VaultGearModifier<?> modifier : gearData.getAllModifierAffixes()) {
                  components.add(modifier.getAttribute().getReader().getRgbColor());
               }

               return components.stream().mapToInt(Integer::intValue).toArray();
            }
         }
      );
   }

   private record CacheResult<T>(GearDataCache.ResultType type, T value) {
      private static <T> GearDataCache.CacheResult<T> miss() {
         return new GearDataCache.CacheResult<>(GearDataCache.ResultType.MISS, null);
      }

      private static <T> GearDataCache.CacheResult<T> none() {
         return new GearDataCache.CacheResult<>(GearDataCache.ResultType.HIT_NONE, null);
      }

      private static <T> GearDataCache.CacheResult<T> hit(T value) {
         return new GearDataCache.CacheResult<>(GearDataCache.ResultType.HIT, value);
      }

      private boolean isHit() {
         return this.type == GearDataCache.ResultType.HIT;
      }

      private boolean hitHasNoValue() {
         return this.type == GearDataCache.ResultType.HIT_NONE;
      }
   }

   private static enum ResultType {
      HIT,
      HIT_NONE,
      MISS;
   }
}
