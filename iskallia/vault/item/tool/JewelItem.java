package iskallia.vault.item.tool;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.VaultGearType;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.recipe.JewelAnvilRecipe;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

public class JewelItem extends Item implements VaultGearItem, DataInitializationItem, DataTransferItem {
   public JewelItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public Component getName(@NotNull ItemStack stack) {
      int color = getColor(stack);
      GearDataCache cache = GearDataCache.of(stack);
      if (cache.getState() == VaultGearState.UNIDENTIFIED) {
         return new TranslatableComponent(this.getDescriptionId(stack) + ".unidentified");
      } else {
         VaultGearRarity rarity = (VaultGearRarity)ObjectUtils.firstNonNull(new VaultGearRarity[]{cache.getRarity(), VaultGearRarity.SCRAPPY});
         if (rarity == VaultGearRarity.SCRAPPY) {
            return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
         } else {
            GearDataCache clientCache = GearDataCache.of(stack);
            String customName = clientCache.getGearName();
            if (customName != null) {
               return new TextComponent(customName).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            } else {
               String prefix = this.getDescriptionId() + "." + rarity.name().toLowerCase(Locale.ROOT);
               return new TranslatableComponent(prefix).append(super.getName(stack)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
   }

   @Override
   public void addTooltipDurability(List<Component> tooltip, ItemStack stack) {
   }

   @Override
   public void addRepairTooltip(List<Component> tooltip, int usedRepairs, int totalRepairs) {
   }

   @Override
   public void addTooltipRarity(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   @Override
   public void addTooltipCraftingPotential(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return VaultGearHelper.shouldPlayGearReequipAnimation(oldStack, newStack, slotChanged);
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         this.vaultGearTick(stack, player);
      }
   }

   public void fillItemCategory(@Nonnull CreativeModeTab category, @Nonnull NonNullList<ItemStack> items) {
      if (category == ModItems.GEAR_GROUP) {
         List<ItemStack> jewels = new ArrayList<>();

         for (VaultGearModifier<?> modifier : JewelAnvilRecipe.PRESET_MODIFIERS) {
            jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, modifier)));
         }

         for (ItemStack jewel : jewels) {
            VaultGearData data = VaultGearData.read(jewel);
            data.setRarity(VaultGearRarity.OMEGA);
            data.setState(VaultGearState.IDENTIFIED);
            data.write(jewel);
         }

         items.addAll(jewels);
      }
   }

   @Override
   public void initialize(ItemStack stack, RandomSource random) {
      this.instantIdentify(null, stack);
   }

   @Override
   public void onIdentify(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      ModConfigs.JEWEL_SIZE.getSize(data.getRarity()).ifPresent(size -> {
         data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.JEWEL_SIZE, size.getRandom(random)));
         data.write(stack);
      });
   }

   @Override
   public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return ModConfigs.VAULT_RECYCLER.getJewelRecyclingOutput();
   }

   @Override
   public float getResultPercentage(ItemStack input) {
      return 1.0F;
   }

   @Nonnull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.JEWEL;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.UNKNOWN;
   }

   @Nonnull
   @Override
   public VaultGearType getGearType(ItemStack stack) {
      return VaultGearType.JEWEL;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return null;
   }

   public static int getColor(ItemStack stack) {
      GearDataCache clientCache = GearDataCache.of(stack);
      if (clientCache.getState() == VaultGearState.UNIDENTIFIED) {
         return -12632257;
      } else {
         ColorBlender blender = new ColorBlender(1.0F);
         Optional.ofNullable(clientCache.getJewelColorComponents()).ifPresent(colors -> colors.forEach(color -> blender.add(color, 60.0F)));
         float time = (float)ClientScheduler.INSTANCE.getTick();
         return blender.getColor(time);
      }
   }

   public static ItemStack create(Consumer<VaultGearData> consumer) {
      ItemStack stack = new ItemStack(ModItems.JEWEL);
      VaultGearData data = VaultGearData.read(stack);
      consumer.accept(data);
      data.write(stack);
      return stack;
   }
}
