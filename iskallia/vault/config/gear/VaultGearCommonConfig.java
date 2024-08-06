package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.init.ModItems;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultGearCommonConfig extends Config {
   @Expose
   private int swapCooldown;
   @Expose
   private final Map<EquipmentSlot, List<ResourceLocation>> swapItems = new HashMap<>();

   @Override
   public String getName() {
      return "gear%sgear_common".formatted(File.separator);
   }

   public int getSwapCooldown() {
      return this.swapCooldown;
   }

   public List<Item> getSwapItems(EquipmentSlot slot) {
      return this.swapItems
         .getOrDefault(slot, Collections.emptyList())
         .stream()
         .<Item>map(ForgeRegistries.ITEMS::getValue)
         .filter(item -> item != Items.AIR)
         .toList();
   }

   @Override
   protected void reset() {
      this.swapCooldown = 400;
      this.swapItems.clear();
      List<ResourceLocation> items = new ArrayList<>();
      items.add(ModItems.FOCUS.getRegistryName());
      items.add(ModItems.WAND.getRegistryName());
      items.add(ModItems.SHIELD.getRegistryName());
      items.add(ModItems.IDOL_BENEVOLENT.getRegistryName());
      items.add(ModItems.IDOL_MALEVOLENCE.getRegistryName());
      items.add(ModItems.IDOL_TIMEKEEPER.getRegistryName());
      items.add(ModItems.IDOL_OMNISCIENT.getRegistryName());
      this.swapItems.put(EquipmentSlot.OFFHAND, items);
      this.swapItems.put(EquipmentSlot.HEAD, List.of(ModItems.HELMET.getRegistryName()));
      this.swapItems.put(EquipmentSlot.CHEST, List.of(ModItems.CHESTPLATE.getRegistryName()));
      this.swapItems.put(EquipmentSlot.LEGS, List.of(ModItems.LEGGINGS.getRegistryName()));
      this.swapItems.put(EquipmentSlot.FEET, List.of(ModItems.BOOTS.getRegistryName()));
   }
}
