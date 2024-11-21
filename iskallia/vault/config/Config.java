package iskallia.vault.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.VaultMod;
import iskallia.vault.antique.condition.AntiqueCondition;
import iskallia.vault.antique.reward.AntiqueReward;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.config.adapter.BingoItemAdapter;
import iskallia.vault.config.adapter.IdentifierAdapter;
import iskallia.vault.config.adapter.ItemStackAdapter;
import iskallia.vault.config.adapter.PartialTileAdapter;
import iskallia.vault.config.adapter.ProcessorAdapter;
import iskallia.vault.config.adapter.RegistryCodecAdapter;
import iskallia.vault.config.adapter.TextColorAdapter;
import iskallia.vault.config.adapter.VersionedKeyAdapter;
import iskallia.vault.config.adapter.WeightedListAdapter;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.skillgate.SkillGateType;
import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardCondition;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.CardScaler;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.objective.bingo.BingoItem;
import iskallia.vault.core.vault.objective.elixir.ElixirTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.roll.FloatRoll;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.properties.CrystalProperties;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import iskallia.vault.item.crystal.time.CrystalTime;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.skill.SkillGates;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.task.Task;
import iskallia.vault.util.EnchantmentCost;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class Config {
   public static final Random rand = new Random();
   public static final RandomSource randSrc = JavaRandom.ofNanoTime();
   public static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(MobEffect.class, RegistryCodecAdapter.of(ForgeRegistries.MOB_EFFECTS))
      .registerTypeHierarchyAdapter(Item.class, RegistryCodecAdapter.of(ForgeRegistries.ITEMS))
      .registerTypeHierarchyAdapter(Block.class, RegistryCodecAdapter.of(ForgeRegistries.BLOCKS))
      .registerTypeHierarchyAdapter(Enchantment.class, Adapters.ENCHANTMENT)
      .registerTypeAdapterFactory(IdentifierAdapter.FACTORY)
      .registerTypeAdapterFactory(TextColorAdapter.FACTORY)
      .registerTypeAdapterFactory(ItemStackAdapter.FACTORY)
      .registerTypeAdapterFactory(PartialTileAdapter.FACTORY)
      .registerTypeAdapterFactory(WeightedListAdapter.Factory.INSTANCE)
      .registerTypeHierarchyAdapter(VaultGearTierConfig.AttributeGroup.class, new VaultGearTierConfig.AttributeGroup.Serializer())
      .registerTypeHierarchyAdapter(EtchingConfig.EtchingMap.class, new EtchingConfig.EtchingMap.Serializer())
      .registerTypeHierarchyAdapter(TrinketConfig.TrinketMap.class, new TrinketConfig.TrinketMap.Serializer())
      .registerTypeHierarchyAdapter(CharmConfig.CharmMap.class, new CharmConfig.CharmMap.Serializer())
      .registerTypeAdapter(VaultModifiersConfig.ModifierTypeGroups.class, new VaultModifiersConfig.ModifierTypeGroups.Serializer())
      .registerTypeAdapter(CompoundTag.class, Adapters.COMPOUND_NBT)
      .registerTypeAdapter(EnchantmentCost.class, EnchantmentCost.ADAPTER)
      .registerTypeHierarchyAdapter(VersionedKey.class, VersionedKeyAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(CrystalTheme.class, CrystalData.THEME)
      .registerTypeHierarchyAdapter(CrystalLayout.class, CrystalData.LAYOUT)
      .registerTypeHierarchyAdapter(CrystalObjective.class, CrystalData.OBJECTIVE)
      .registerTypeHierarchyAdapter(CrystalTime.class, CrystalData.TIME)
      .registerTypeHierarchyAdapter(CrystalModifiers.class, CrystalData.MODIFIERS)
      .registerTypeHierarchyAdapter(CrystalProperties.class, CrystalData.PROPERTIES)
      .registerTypeHierarchyAdapter(ScavengeTask.class, ScavengeTask.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(LootPool.class, Adapters.LOOT_POOL)
      .registerTypeHierarchyAdapter(LootTable.Entry.class, Adapters.LOOT_TABLE_ENTRY)
      .registerTypeHierarchyAdapter(IntRoll.class, Adapters.INT_ROLL)
      .registerTypeHierarchyAdapter(FloatRoll.class, Adapters.FLOAT_ROLL)
      .registerTypeHierarchyAdapter(Skill.class, Adapters.SKILL)
      .registerTypeHierarchyAdapter(Task.class, Adapters.TASK)
      .registerTypeHierarchyAdapter(ChallengeAction.class, Adapters.RAID_ACTION)
      .registerTypeAdapter(ElixirTask.Config.class, ElixirTask.Config.Serializer.INSTANCE)
      .registerTypeHierarchyAdapter(Quest.class, Quest.Adapter.INSTANCE)
      .registerTypeAdapter(TilePredicate.class, Adapters.TILE_PREDICATE)
      .registerTypeAdapter(EntityPredicate.class, Adapters.ENTITY_PREDICATE)
      .registerTypeAdapter(ItemPredicate.class, Adapters.ITEM_PREDICATE)
      .registerTypeAdapter(SkillGateType.class, SkillGates.GATE_TYPE)
      .registerTypeAdapter(VaultAltarConfig.Interface.class, Adapters.ALTAR_INTERFACE)
      .registerTypeAdapter(BingoItem.class, BingoItemAdapter.INSTANCE)
      .registerTypeAdapter(Card.Config.class, Card.Config.ADAPTER)
      .registerTypeHierarchyAdapter(Processor.class, ProcessorAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(AntiqueCondition.class, AntiqueCondition.Serializer.INSTANCE)
      .registerTypeHierarchyAdapter(AntiqueReward.class, AntiqueReward.Serializer.INSTANCE)
      .registerTypeAdapter(CardEntry.Config.class, CardEntry.Config.ADAPTER)
      .registerTypeAdapter(CardScaler.class, CardScaler.ADAPTER)
      .registerTypeAdapter(CardCondition.class, CardCondition.ADAPTER)
      .registerTypeHierarchyAdapter(Component.class, Adapters.COMPONENT)
      .excludeFieldsWithoutExposeAnnotation()
      .enableComplexMapKeySerialization()
      .disableHtmlEscaping()
      .setPrettyPrinting()
      .create();
   protected String root = "config%s%s%s".formatted(File.separator, "the_vault", File.separator);
   protected String extension = ".json";

   public void generateConfig() {
      this.reset();

      try {
         this.writeConfig();
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private File getConfigFile() {
      return new File(this.root + this.getName() + this.extension);
   }

   public abstract String getName();

   @Override
   public String toString() {
      return this.getName();
   }

   public <T extends Config> T readConfig() {
      VaultMod.LOGGER.info("Reading config: " + this.getName());

      try {
         Config var3;
         try (FileReader reader = new FileReader(this.getConfigFile())) {
            try {
               T config = (T)GSON.fromJson(reader, this.getClass());
               config.onLoad(this);
               if (!config.isValid()) {
                  VaultMod.LOGGER.error("Invalid config {}, using defaults", this);
                  ModConfigs.INVALID_CONFIGS.add(this.getConfigFile().getName() + " - There was an invalid setting in this config.");
                  config.reset();
               }

               ModConfigs.CONFIGS.add(config);
               return config;
            } catch (Exception var5) {
               VaultMod.LOGGER.warn("Invalid config {}, using defaults", this, var5);
               this.reset();
               this.onLoad(null);
               ModConfigs.INVALID_CONFIGS.add(this.getConfigFile().getName() + " - Exception: " + var5.getMessage());
               ModConfigs.CONFIGS.add(this);
               var5.printStackTrace();
               var3 = this;
            }
         }

         return (T)var3;
      } catch (Exception var7) {
         VaultMod.LOGGER.warn("Config file {} not found, generating new", this);
         this.generateConfig();
         ModConfigs.CONFIGS.add(this);
         return (T)this;
      }
   }

   protected boolean isValid() {
      return true;
   }

   protected void onLoad(@Nullable Config oldConfigInstance) {
   }

   public void onUnload() {
   }

   public static boolean checkAllFieldsAreNotNull(Object o) throws IllegalAccessException {
      for (Field v : o.getClass().getDeclaredFields()) {
         if (v.canAccess(o)) {
            Object field = v.get(o);
            if (field == null) {
               return false;
            }

            if (!field.getClass().isPrimitive()) {
               boolean b = checkAllFieldsAreNotNull(field);
               if (!b) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   protected abstract void reset();

   public void writeConfig() throws IOException {
      File cfgFile = this.getConfigFile();
      File dir = cfgFile.getParentFile();
      if (dir.exists() || dir.mkdirs()) {
         if (cfgFile.exists() || cfgFile.createNewFile()) {
            FileWriter writer = new FileWriter(cfgFile);
            GSON.toJson(this, writer);
            writer.flush();
            writer.close();
         }
      }
   }
}
