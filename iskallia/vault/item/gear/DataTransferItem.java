package iskallia.vault.item.gear;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface DataTransferItem {
   static ItemStack doConvertStack(ItemStack stack) {
      return doConvertStack(stack, JavaRandom.ofNanoTime());
   }

   static ItemStack doConvertStack(ItemStack stack, RandomSource random) {
      return stack.getItem() instanceof DataTransferItem transferItem ? transferItem.convertStack(stack, random) : stack;
   }

   default ItemStack convertStack(ItemStack stack, RandomSource random) {
      this.convertData(stack);
      return stack;
   }

   default void convertData(ItemStack stack) {
      if (stack.hasTag()) {
         Set<String> convertedKeys = new HashSet<>();
         CompoundTag tag = stack.getOrCreateTag();
         AttributeGearData data = AttributeGearData.read(stack);

         for (String key : tag.getAllKeys()) {
            ResourceLocation attrKey;
            try {
               attrKey = new ResourceLocation(key);
            } catch (ResourceLocationException var9) {
               continue;
            }

            VaultGearAttribute<?> attr = VaultGearAttributeRegistry.getAttribute(attrKey);
            if (attr != null) {
               this.transferData(data, attr, tag.get(key));
               convertedKeys.add(key);
            }
         }

         data.write(stack);
         convertedKeys.forEach(tag::remove);
      }
   }

   default <T> void transferData(AttributeGearData data, VaultGearAttribute<T> attr, Tag tag) {
      if (attr == ModGearAttributes.STATE && data instanceof VaultGearData vaultGearData) {
         vaultGearData.setState(ModGearAttributes.STATE.getType().nbtRead(tag));
      } else {
         data.updateAttribute(attr, attr.getType().nbtRead(tag));
      }
   }
}
