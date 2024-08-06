package iskallia.vault.item.modification;

import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class ReforgeTagModificationFocus extends GearModificationItem implements DataTransferItem {
   private static final Map<Item, String> ITEM_TO_NAME = new HashMap<>();

   public ReforgeTagModificationFocus(ResourceLocation id, GearModification modification) {
      super(id, modification);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (ModConfigs.isInitialized()) {
         if (this.allowdedIn(tab)) {
            for (String tag : ModConfigs.VAULT_GEAR_TAG_CONFIG.getTags()) {
               ItemStack focus = new ItemStack(this);
               setModifierTag(focus, tag);
               items.add(focus);
            }
         }
      }
   }

   @Override
   public ItemStack convertStack(ItemStack stack, RandomSource random) {
      ItemStack result = DataTransferItem.super.convertStack(stack, random);
      if (getModifierTag(result) == null) {
         String randomTag = ModConfigs.VAULT_GEAR_TAG_CONFIG.getRandomTag();
         if (randomTag != null) {
            setModifierTag(result, randomTag);
         }
      }

      return result;
   }

   @Nullable
   public static VaultGearTagConfig.ModTagGroup getModifierTag(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof ReforgeTagModificationFocus) {
         String tagStr = stack.getOrCreateTag().getString("modTag");
         return ModConfigs.VAULT_GEAR_TAG_CONFIG.getGroupTag(tagStr);
      } else {
         return null;
      }
   }

   public static void setModifierTag(ItemStack stack, String tag) {
      if (!stack.isEmpty() && stack.getItem() instanceof ReforgeTagModificationFocus) {
         stack.getOrCreateTag().putString("modTag", tag);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      if (Screen.hasShiftDown()) {
         VaultGearTagConfig.ModTagGroup group = getModifierTag(stack);
         if (group != null) {
            getAttributes(group)
               .forEach(
                  (attribute, items) -> {
                     VaultGearModifierReader<?> reader = attribute.getReader();
                     MutableComponent text = new TextComponent(" - ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(new TextComponent(reader.getModifierName()).withStyle(reader.getColoredTextStyle()));
                     if (Screen.hasAltDown()) {
                        text.append(new TextComponent(" (" + getItemsDisplay((List<Item>)items) + ")").withStyle(ChatFormatting.GRAY));
                     }

                     tooltip.add(text);
                  }
               );
         }
      }
   }

   private static Map<VaultGearAttribute<?>, List<Item>> getAttributes(VaultGearTagConfig.ModTagGroup tagGroup) {
      Map<VaultGearAttribute<?>, List<Item>> attributes = new LinkedHashMap<>();
      ModConfigs.VAULT_GEAR_CONFIG.forEach((item, config) -> tagGroup.getTags().forEach(tag -> config.getGroupsWithModifierTag(tag).forEach(tpl -> {
         VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(((VaultGearTierConfig.ModifierTierGroup)tpl.getB()).getAttribute());
         ForgeRegistries.ITEMS.getHolder(item).ifPresent(holder -> attributes.computeIfAbsent(attribute, a -> new ArrayList<>()).add((Item)holder.value()));
      })));
      return attributes;
   }

   private static String getItemsDisplay(List<Item> items) {
      List<String> lines = new ArrayList<>();
      List<Item> armors = new ArrayList<>();
      if (items.contains(ModItems.HELMET)) {
         armors.add(ModItems.HELMET);
      }

      if (items.contains(ModItems.CHESTPLATE)) {
         armors.add(ModItems.CHESTPLATE);
      }

      if (items.contains(ModItems.LEGGINGS)) {
         armors.add(ModItems.LEGGINGS);
      }

      if (items.contains(ModItems.BOOTS)) {
         armors.add(ModItems.BOOTS);
      }

      if (armors.size() == 4) {
         lines.add("Armor");
      } else {
         armors.forEach(item -> lines.add(ITEM_TO_NAME.get(item)));
      }

      List<Item> weapons = new ArrayList<>();
      if (items.contains(ModItems.SWORD)) {
         weapons.add(ModItems.SWORD);
      }

      if (items.contains(ModItems.AXE)) {
         weapons.add(ModItems.AXE);
      }

      if (weapons.size() == 2) {
         lines.add("Weapon");
      } else {
         weapons.forEach(item -> lines.add(ITEM_TO_NAME.get(item)));
      }

      if (items.contains(ModItems.SHIELD)) {
         lines.add("Shield");
      }

      if (items.contains(ModItems.MAGNET)) {
         lines.add("Magnet");
      }

      List<Item> idols = new ArrayList<>();
      if (items.contains(ModItems.IDOL_BENEVOLENT)) {
         idols.add(ModItems.IDOL_BENEVOLENT);
      }

      if (items.contains(ModItems.IDOL_OMNISCIENT)) {
         idols.add(ModItems.IDOL_OMNISCIENT);
      }

      if (items.contains(ModItems.IDOL_TIMEKEEPER)) {
         idols.add(ModItems.IDOL_TIMEKEEPER);
      }

      if (items.contains(ModItems.IDOL_MALEVOLENCE)) {
         idols.add(ModItems.IDOL_MALEVOLENCE);
      }

      if (idols.size() == 4) {
         lines.add("Idol");
      } else {
         idols.forEach(item -> lines.add(ITEM_TO_NAME.get(item)));
      }

      if (items.contains(ModItems.FOCUS)) {
         lines.add("Focus");
      }

      if (items.contains(ModItems.WAND)) {
         lines.add("Wand");
      }

      StringBuilder result = new StringBuilder();
      Iterator<String> it = lines.iterator();

      while (it.hasNext()) {
         String line = it.next();
         result.append(line);
         if (it.hasNext()) {
            result.append(", ");
         }
      }

      return result.toString();
   }

   static {
      ITEM_TO_NAME.put(ModItems.HELMET, "Helmet");
      ITEM_TO_NAME.put(ModItems.CHESTPLATE, "Chestplate");
      ITEM_TO_NAME.put(ModItems.LEGGINGS, "Leggings");
      ITEM_TO_NAME.put(ModItems.BOOTS, "Boots");
      ITEM_TO_NAME.put(ModItems.SWORD, "Sword");
      ITEM_TO_NAME.put(ModItems.AXE, "Axe");
      ITEM_TO_NAME.put(ModItems.SHIELD, "Shield");
      ITEM_TO_NAME.put(ModItems.IDOL_BENEVOLENT, "Velara Idol");
      ITEM_TO_NAME.put(ModItems.IDOL_OMNISCIENT, "Tenos Idol");
      ITEM_TO_NAME.put(ModItems.IDOL_TIMEKEEPER, "Wendarr Idol");
      ITEM_TO_NAME.put(ModItems.IDOL_MALEVOLENCE, "Idona Idol");
      ITEM_TO_NAME.put(ModItems.FOCUS, "Focus");
      ITEM_TO_NAME.put(ModItems.WAND, "Wand");
   }
}
