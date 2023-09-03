package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.init.ModItems;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultGearCommonConfig extends Config {
   @Expose
   private int offHandSwapCooldown;
   @Expose
   private final List<ResourceLocation> offHandSwapItems = new ArrayList<>();

   @Override
   public String getName() {
      return "gear%sgear_common".formatted(File.separator);
   }

   public int getOffHandSwapCooldown() {
      return this.offHandSwapCooldown;
   }

   public List<Item> getOffHandSwapItems() {
      return this.offHandSwapItems.stream().<Item>map(ForgeRegistries.ITEMS::getValue).filter(item -> item != Items.AIR).toList();
   }

   @Override
   protected void reset() {
      this.offHandSwapCooldown = 100;
      this.offHandSwapItems.clear();
      this.offHandSwapItems.add(ModItems.WAND.getRegistryName());
      this.offHandSwapItems.add(ModItems.SHIELD.getRegistryName());
      this.offHandSwapItems.add(ModItems.IDOL_BENEVOLENT.getRegistryName());
      this.offHandSwapItems.add(ModItems.IDOL_MALEVOLENCE.getRegistryName());
      this.offHandSwapItems.add(ModItems.IDOL_TIMEKEEPER.getRegistryName());
      this.offHandSwapItems.add(ModItems.IDOL_OMNISCIENT.getRegistryName());
   }
}
