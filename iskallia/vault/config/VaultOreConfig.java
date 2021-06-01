package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.gen.ruletest.VaultRuleTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class VaultOreConfig extends Config {
   @Expose
   private int ORES_PER_VAULT;
   @Expose
   private List<VaultOreConfig.Ore> ORES = new ArrayList<>();
   private int totalWeight;

   @Override
   public String getName() {
      return "vault_ore";
   }

   public VaultOreConfig.Ore[] getPool(long worldSeed, int chunkX, int chunkZ, SharedSeedRandom rand) {
      chunkX <<= 4;
      chunkZ <<= 4;
      int regionX = chunkX < 0 ? chunkX / 2048 - 1 : chunkX / 2048;
      int regionZ = chunkZ < 0 ? chunkZ / 2048 - 1 : chunkZ / 2048;
      rand.func_202425_c(worldSeed, regionX, regionZ);
      Set<VaultOreConfig.Ore> indices = new HashSet<>();

      while (indices.size() < this.ORES_PER_VAULT) {
         indices.add(this.getWeightedOreAt(rand.nextInt(this.getTotalWeight())));
      }

      return indices.toArray(new VaultOreConfig.Ore[0]);
   }

   public int getTotalWeight() {
      if (this.totalWeight == 0) {
         for (VaultOreConfig.Ore ore : this.ORES) {
            this.totalWeight = this.totalWeight + ore.WEIGHT;
         }
      }

      return this.totalWeight;
   }

   public VaultOreConfig.Ore getWeightedOreAt(int index) {
      VaultOreConfig.Ore current = null;

      for (VaultOreConfig.Ore ore : this.ORES) {
         current = ore;
         index -= ore.WEIGHT;
         if (index < 0) {
            break;
         }
      }

      return current;
   }

   @Override
   protected void reset() {
      this.ORES_PER_VAULT = 2;
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.ALEXANDRITE_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.BENITOITE_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.LARIMAR_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.BLACK_OPAL_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.PAINITE_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.ISKALLIUM_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.RENIUM_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.GORGINITE_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.SPARKLETINE_ORE.getRegistryName().toString(), 64, 3, 1));
      this.ORES.add(new VaultOreConfig.Ore(ModBlocks.WUTODIE_ORE.getRegistryName().toString(), 64, 3, 1));
   }

   public static class Ore {
      @Expose
      public String NAME;
      @Expose
      public int TRIES;
      @Expose
      public int SIZE;
      @Expose
      public int WEIGHT;

      public Ore(String name, int tries, int size, int weight) {
         this.NAME = name;
         this.TRIES = tries;
         this.SIZE = size;
         this.WEIGHT = weight;
      }

      public OreFeatureConfig toConfig() {
         BlockState state = Registry.field_212618_g.func_241873_b(new ResourceLocation(this.NAME)).orElse(Blocks.field_196654_e).func_176223_P();
         return new OreFeatureConfig(VaultRuleTest.INSTANCE, state, this.SIZE);
      }
   }
}
