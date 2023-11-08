package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.stat.VaultChestType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class CatalystStageAttributes {
   private final List<CatalystStageAttributes.CatalystWave> catalystWaves;
   private final float projectileDamage;
   private final float chestChance;
   private final float fangsChance;
   private final float fangsDamage;
   private final float explosionDamageMultiplier;
   private final WeightedList<CatalystStageAttributes.EffectAttributes> effects;
   private final Map<VaultChestType, ResourceLocation> chestLootTables;

   public CatalystStageAttributes(
      List<CatalystStageAttributes.CatalystWave> catalystWaves,
      float projectileDamage,
      float chestChance,
      float fangsChance,
      float fangsDamage,
      float explosionDamageMultiplier,
      WeightedList<CatalystStageAttributes.EffectAttributes> effects,
      Map<VaultChestType, ResourceLocation> chestLootTables
   ) {
      this.catalystWaves = catalystWaves;
      this.projectileDamage = projectileDamage;
      this.chestChance = chestChance;
      this.fangsChance = fangsChance;
      this.fangsDamage = fangsDamage;
      this.explosionDamageMultiplier = explosionDamageMultiplier;
      this.effects = effects;
      this.chestLootTables = chestLootTables;
   }

   public float getProjectileDamage() {
      return this.projectileDamage;
   }

   public List<CatalystStageAttributes.CatalystWave> getCatalystWaves() {
      return this.catalystWaves;
   }

   public static CatalystStageAttributes from(CompoundTag tag) {
      return new CatalystStageAttributes(
         deserializeCatalystWaves(tag.getList("CatalystWaves", 10)),
         tag.getFloat("ProjectileDamage"),
         tag.getFloat("ChestChance"),
         tag.getFloat("FangsChance"),
         tag.getFloat("FangsDamage"),
         tag.getFloat("ExplosionDamageMultiplier"),
         deserializeEffects(tag.getList("Effects", 10)),
         deserializeChestLootTables(tag.getCompound("ChestLootTables"))
      );
   }

   protected static WeightedList<CatalystStageAttributes.EffectAttributes> deserializeEffects(ListTag tag) {
      WeightedList<CatalystStageAttributes.EffectAttributes> effects = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         CatalystStageAttributes.EffectAttributes.from(compoundTag)
            .ifPresent(effectAttributes -> effects.add(effectAttributes, compoundTag.getDouble("Weight")));
      }

      return effects;
   }

   protected static Map<VaultChestType, ResourceLocation> deserializeChestLootTables(CompoundTag tag) {
      Map<VaultChestType, ResourceLocation> chestLootTables = new EnumMap<>(VaultChestType.class);

      for (String key : tag.getAllKeys()) {
         chestLootTables.put(VaultChestType.valueOf(key), new ResourceLocation(tag.getString(key)));
      }

      return chestLootTables;
   }

   private static List<CatalystStageAttributes.CatalystWave> deserializeCatalystWaves(ListTag tag) {
      List<CatalystStageAttributes.CatalystWave> catalystWaves = new ArrayList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         catalystWaves.add(CatalystStageAttributes.CatalystWave.from(compoundTag));
      }

      return catalystWaves;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.put("CatalystWaves", this.serializeCatalystWaves(this.catalystWaves));
      tag.putFloat("ProjectileDamage", this.projectileDamage);
      tag.putFloat("ChestChance", this.chestChance);
      tag.putFloat("FangsChance", this.fangsChance);
      tag.putFloat("FangsDamage", this.fangsDamage);
      tag.putFloat("ExplosionDamageMultiplier", this.explosionDamageMultiplier);
      tag.put("Effects", serializeEffects(this.effects));
      tag.put("ChestLootTables", serializeChestLootTables(this.chestLootTables));
      return tag;
   }

   protected static CompoundTag serializeChestLootTables(Map<VaultChestType, ResourceLocation> chestLootTables) {
      CompoundTag tag = new CompoundTag();
      chestLootTables.forEach((vaultChestType, resourceLocation) -> tag.putString(vaultChestType.name(), resourceLocation.toString()));
      return tag;
   }

   protected static ListTag serializeEffects(WeightedList<CatalystStageAttributes.EffectAttributes> effects) {
      ListTag tag = new ListTag();
      effects.forEach((effectAttributes, weight) -> {
         CompoundTag compoundTag = effectAttributes.serialize();
         compoundTag.putDouble("Weight", weight);
         tag.add(compoundTag);
      });
      return tag;
   }

   private ListTag serializeCatalystWaves(List<CatalystStageAttributes.CatalystWave> catalystWaves) {
      ListTag tag = new ListTag();
      catalystWaves.forEach(catalystWave -> tag.add(catalystWave.serialize()));
      return tag;
   }

   public float getChestChance() {
      return this.chestChance;
   }

   public float getFangsChance() {
      return this.fangsChance;
   }

   public WeightedList<CatalystStageAttributes.EffectAttributes> getEffects() {
      return this.effects;
   }

   public Map<VaultChestType, ResourceLocation> getChestLootTables() {
      return this.chestLootTables;
   }

   public float getFangsDamage() {
      return this.fangsDamage;
   }

   public float getExplosionDamageMultiplier() {
      return this.explosionDamageMultiplier;
   }

   public record CatalystWave(int minCatalysts, int maxCatalysts) {
      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putInt("MinCatalysts", this.minCatalysts);
         tag.putInt("MaxCatalysts", this.maxCatalysts);
         return tag;
      }

      public static CatalystStageAttributes.CatalystWave from(CompoundTag tag) {
         return new CatalystStageAttributes.CatalystWave(tag.getInt("MinCatalysts"), tag.getInt("MaxCatalysts"));
      }
   }

   public record EffectAttributes(MobEffect effect, int duration, int amplifier) {
      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putString("Effect", this.effect.getRegistryName().toString());
         tag.putInt("Duration", this.duration);
         tag.putInt("Amplifier", this.amplifier);
         return tag;
      }

      public static Optional<CatalystStageAttributes.EffectAttributes> from(CompoundTag tag) {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("Effect")));
         return effect == null
            ? Optional.empty()
            : Optional.of(new CatalystStageAttributes.EffectAttributes(effect, tag.getInt("Duration"), tag.getInt("Amplifier")));
      }
   }
}
