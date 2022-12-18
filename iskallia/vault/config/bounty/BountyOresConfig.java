package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BountyOresConfig extends Config {
   @Expose
   private HashMap<ResourceLocation, List<ResourceLocation>> ores = new HashMap<>();

   @Override
   public String getName() {
      return "bounty/ores";
   }

   @Override
   protected void reset() {
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.ALEXANDRITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_ALEXANDRITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.ASHIUM_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_ASHIUM_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.BENITOITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_BENITOITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.BOMIGNITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_BOMIGNITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.ECHO_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_ECHO_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.GORGINITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_GORGINITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.ISKALLIUM_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_ISKALLIUM_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.LARIMAR_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_LARIMAR_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.PAINITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_PAINITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.PETZANITE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_PETZANITE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.SPARKLETINE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_SPARKLETINE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.TUBIUM_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_TUBIUM_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.UPALINE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_UPALINE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.WUTODIE_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_WUTODIE_ORE)));
      this.ores.put(ForgeRegistries.BLOCKS.getKey(ModBlocks.XENIUM_ORE), List.of(ForgeRegistries.BLOCKS.getKey(ModBlocks.VAULTSTONE_XENIUM_ORE)));
   }

   public List<ResourceLocation> getValidOres(ResourceLocation id) {
      return this.ores.computeIfAbsent(id, location -> new ArrayList<>());
   }
}
