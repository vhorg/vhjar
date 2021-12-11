package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultRuneConfig extends Config {
   @Expose
   private final WeightedList<String> runeWeights = new WeightedList<>();

   public Item getRandomRune() {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.runeWeights.getRandom(rand)));
   }

   @Override
   public String getName() {
      return "vault_rune";
   }

   @Override
   protected void reset() {
      this.runeWeights.clear();
      this.runeWeights.add(ModItems.VAULT_RUNE_MINE.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_PUZZLE.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_DIGSITE.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_CRYSTAL.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_VIEWER.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_VENDOR.getRegistryName().toString(), 1);
      this.runeWeights.add(ModItems.VAULT_RUNE_XMARK.getRegistryName().toString(), 1);
   }
}
