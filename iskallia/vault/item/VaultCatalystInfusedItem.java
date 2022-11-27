package iskallia.vault.item;

import iskallia.vault.config.VaultCrystalCatalystConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.CodecUtils;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VaultCatalystInfusedItem extends Item {
   private static final String TOOLTIP_MODIFIER_SINGULAR = "tooltip.the_vault.vault_catalyst.modifier.singular";
   private static final String TOOLTIP_MODIFIER_PLURAL = "tooltip.the_vault.vault_catalyst.modifier.plural";
   private static final String TOOLTIP_MODIFIER_NAME_PREFIX = "- ";
   private static final String TOOLTIP_MODIFIER_DESCRIPTION_PREFIX = "   ";
   private static final String TAG_MODIFIERS = "modifiers";
   private static final Random RANDOM = new Random();

   public VaultCatalystInfusedItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   @OnlyIn(Dist.CLIENT)
   @ParametersAreNonnullByDefault
   public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      List<ResourceLocation> modifierIdList = getModifiers(itemStack);
      if (!modifierIdList.isEmpty()) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(
            new TranslatableComponent(
                  modifierIdList.size() <= 1 ? "tooltip.the_vault.vault_catalyst.modifier.singular" : "tooltip.the_vault.vault_catalyst.modifier.plural"
               )
               .withStyle(ChatFormatting.GOLD)
         );

         for (ResourceLocation modifierId : modifierIdList) {
            VaultModifierRegistry.getOpt(modifierId).ifPresent(vaultModifier -> {
               tooltip.add(new TextComponent("- " + vaultModifier.getDisplayName()).withStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor())));
               if (Screen.hasShiftDown()) {
                  tooltip.add(new TextComponent("   " + vaultModifier.getDisplayDescription()).withStyle(ChatFormatting.DARK_GRAY));
               }
            });
         }
      }
   }

   public static void initializeModifiers(ItemStack itemStack, boolean casualVaults) {
      if (!hasModifiers(itemStack)) {
         randomizeModifiers(itemStack, casualVaults);
      }
   }

   public boolean isFoil(ItemStack itemStack) {
      return itemStack.getItem() == ModItems.VAULT_CATALYST_INFUSED;
   }

   private static void randomizeModifiers(ItemStack itemStack, boolean casualVaults) {
      if (itemStack.getItem() == ModItems.VAULT_CATALYST_INFUSED) {
         VaultCrystalCatalystConfig.ModifierPoolGroup modifierPoolGroup = ModConfigs.VAULT_CRYSTAL_CATALYST.getRandomModifierPoolGroup(RANDOM);
         if (modifierPoolGroup != null) {
            List<ResourceLocation> modifierIdList = modifierPoolGroup.getModifierPoolIdList()
               .stream()
               .map(modifierPoolId -> ModConfigs.VAULT_CRYSTAL_CATALYST.getModifierPoolById(modifierPoolId))
               .filter(Objects::nonNull)
               .map(modifierPool -> modifierPool.getRandomModifier(RANDOM, modifierId -> casualVaults && isInventoryRestoreModifier(modifierId)))
               .collect(Collectors.toList());
            setModifiers(itemStack, modifierIdList);
         }
      }
   }

   private static boolean isInventoryRestoreModifier(ResourceLocation modifierId) {
      return VaultModifierRegistry.get(modifierId) instanceof PlayerInventoryRestoreModifier;
   }

   private static boolean hasModifiers(ItemStack itemStack) {
      if (itemStack.getItem() != ModItems.VAULT_CATALYST_INFUSED) {
         return false;
      } else {
         CompoundTag tag = itemStack.getOrCreateTag();
         return tag.contains("modifiers", 9);
      }
   }

   public static List<ResourceLocation> getModifiers(ItemStack itemStack) {
      if (itemStack.getItem() != ModItems.VAULT_CATALYST_INFUSED) {
         return Collections.emptyList();
      } else {
         CompoundTag tag = itemStack.getOrCreateTag();
         return CodecUtils.<List<ResourceLocation>>readNBT(ResourceLocation.CODEC.listOf(), tag.getList("modifiers", 8)).orElse(Collections.emptyList());
      }
   }

   private static void setModifiers(ItemStack itemStack, List<ResourceLocation> result) {
      if (itemStack.getItem() == ModItems.VAULT_CATALYST_INFUSED) {
         CompoundTag tag = itemStack.getOrCreateTag();
         CodecUtils.writeNBT(ResourceLocation.CODEC.listOf(), result, nbt -> tag.put("modifiers", nbt));
      }
   }
}
