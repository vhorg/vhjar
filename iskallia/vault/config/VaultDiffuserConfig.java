package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultDiffuserConfig extends Config {
   @Expose
   private int processingTickTime;
   @Expose
   private ResourceLocation outputItem;
   @Expose
   private Map<ResourceLocation, Integer> diffuserOutputMap;

   @Override
   public String getName() {
      return "vault_diffuser";
   }

   public int getProcessingTickTime() {
      return this.processingTickTime;
   }

   public Map<ResourceLocation, Integer> getDiffuserOutputMap() {
      return this.diffuserOutputMap;
   }

   @Override
   protected void reset() {
      this.processingTickTime = 40;
      this.outputItem = VaultMod.id("soul_dust");
      this.diffuserOutputMap = new LinkedHashMap<>();
      this.diffuserOutputMap.put(ModItems.SILVER_SCRAP.getRegistryName(), 6);
      this.diffuserOutputMap.put(ModItems.CARBON.getRegistryName(), 2);
      this.diffuserOutputMap.put(VaultMod.id("default"), 0);
   }

   public ResourceLocation getOutputItem() {
      return this.outputItem;
   }

   public List<ItemStack> generateOutput(int outputValue) {
      if (!ForgeRegistries.ITEMS.containsKey(ModConfigs.VAULT_DIFFUSER.outputItem)) {
         return List.of(ItemStack.EMPTY);
      } else {
         int numOfStacks = (int)Math.floor(outputValue / 64.0F);
         int leftover = outputValue - numOfStacks * 64;
         List<ItemStack> stacks = new ArrayList<>();

         for (int i = 0; i < numOfStacks; i++) {
            stacks.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(ModConfigs.VAULT_DIFFUSER.outputItem), 64));
         }

         stacks.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(ModConfigs.VAULT_DIFFUSER.outputItem), leftover));
         return stacks;
      }
   }
}
