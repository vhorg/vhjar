package iskallia.vault.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.VaultMod;
import iskallia.vault.config.adapter.CompoundTagAdapter;
import iskallia.vault.config.adapter.IdentifierAdapter;
import iskallia.vault.config.adapter.ItemStackAdapter;
import iskallia.vault.config.adapter.LootPoolAdapter;
import iskallia.vault.config.adapter.LootRollAdapter;
import iskallia.vault.config.adapter.PartialTileAdapter;
import iskallia.vault.config.adapter.RegistryCodecAdapter;
import iskallia.vault.config.adapter.TextColorAdapter;
import iskallia.vault.config.adapter.VersionedKeyAdapter;
import iskallia.vault.config.adapter.WeightedListAdapter;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootRoll;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalModifiers;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class Config {
   public static final Random rand = new Random();
   private static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(MobEffect.class, RegistryCodecAdapter.of(ForgeRegistries.MOB_EFFECTS))
      .registerTypeHierarchyAdapter(Item.class, RegistryCodecAdapter.of(ForgeRegistries.ITEMS))
      .registerTypeHierarchyAdapter(Block.class, RegistryCodecAdapter.of(ForgeRegistries.BLOCKS))
      .registerTypeAdapterFactory(IdentifierAdapter.FACTORY)
      .registerTypeAdapterFactory(TextColorAdapter.FACTORY)
      .registerTypeAdapterFactory(ItemStackAdapter.FACTORY)
      .registerTypeAdapterFactory(PartialTileAdapter.FACTORY)
      .registerTypeAdapterFactory(WeightedListAdapter.Factory.INSTANCE)
      .registerTypeHierarchyAdapter(VaultGearTierConfig.AttributeGroup.class, new VaultGearTierConfig.AttributeGroup.Serializer())
      .registerTypeHierarchyAdapter(EtchingConfig.EtchingMap.class, new EtchingConfig.EtchingMap.Serializer())
      .registerTypeHierarchyAdapter(TrinketConfig.TrinketMap.class, new TrinketConfig.TrinketMap.Serializer())
      .registerTypeAdapter(VaultModifiersConfig.ModifierTypeGroups.class, new VaultModifiersConfig.ModifierTypeGroups.Serializer())
      .registerTypeAdapter(CompoundTag.class, CompoundTagAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(VersionedKey.class, VersionedKeyAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(CrystalLayout.class, CrystalLayout.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(CrystalObjective.class, CrystalObjective.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(CrystalTheme.class, CrystalTheme.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(CrystalModifiers.class, CrystalModifiers.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(ScavengeTask.class, ScavengeTask.Adapter.INSTANCE)
      .registerTypeHierarchyAdapter(LootPool.class, LootPoolAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(LootRoll.class, LootRollAdapter.INSTANCE)
      .excludeFieldsWithoutExposeAnnotation()
      .enableComplexMapKeySerialization()
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

               return config;
            } catch (Exception var5) {
               VaultMod.LOGGER.warn("Invalid config {}, using defaults", this, var5);
               this.reset();
               ModConfigs.INVALID_CONFIGS.add(this.getConfigFile().getName() + " - Exception: " + var5.getMessage());
               var3 = this;
            }
         }

         return (T)var3;
      } catch (Exception var7) {
         VaultMod.LOGGER.warn("Config file {} not found, generating new", this);
         this.generateConfig();
         return (T)this;
      }
   }

   protected boolean isValid() {
      return true;
   }

   protected void onLoad(Config oldConfigInstance) {
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
