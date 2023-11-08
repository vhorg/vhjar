package iskallia.vault.item.tool;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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
      VaultGearRarity rarity = (VaultGearRarity)ObjectUtils.firstNonNull(new VaultGearRarity[]{cache.getRarity(), VaultGearRarity.SCRAPPY});
      if (rarity == VaultGearRarity.SCRAPPY) {
         return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
      } else {
         String prefix = this.getDescriptionId() + "." + rarity.name().toLowerCase(Locale.ROOT);
         return new TranslatableComponent(prefix).append(super.getName(stack)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
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
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.PICKING, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.AXING, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.SHOVELLING, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.WOODEN_AFFINITY, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ORNATE_AFFINITY, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.GILDED_AFFINITY, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.LIVING_AFFINITY, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.COIN_AFFINITY, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.SMELTING, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.PULVERIZING, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.HYDROVOID, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.SOULBOUND, true))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 10.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, 100.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.DURABILITY, 1))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.DURABILITY, 10))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.DURABILITY, 100))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.DURABILITY, 1000))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.DURABILITY, 10000))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.001F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.01F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.COPIOUSLY, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.001F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.01F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_QUANTITY, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.001F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.01F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ITEM_RARITY, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.001F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.01F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.TRAP_DISARMING, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.001F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.01F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 0.1F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.IMMORTALITY, 1.0F))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.REACH, 0.01))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.REACH, 0.1))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.REACH, 1.0))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 1))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 5))));
         jewels.add(create(datax -> datax.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 10))));

         for (ItemStack jewel : jewels) {
            VaultGearData data = VaultGearData.read(jewel);
            data.setRarity(VaultGearRarity.UNIQUE);
            data.setState(VaultGearState.IDENTIFIED);
            data.write(jewel);
         }

         items.addAll(jewels);
      }
   }

   @Override
   public void initialize(ItemStack stack, RandomSource random) {
      Random rand = ((JavaRandom)random).asRandomView();
      VaultGearData data = VaultGearData.read(stack);
      VaultGearRarity rarity = data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE)
         .flatMap(rollTypeStr -> ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(rollTypeStr))
         .orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll())
         .getRandom(rand);
      data.setState(VaultGearState.IDENTIFIED);
      data.setRarity(rarity);
      data.write(stack);
      GearRollHelper.initializeGear(stack);
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

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return null;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return null;
   }

   public static int getColor(ItemStack stack) {
      GearDataCache clientCache = GearDataCache.of(stack);
      ColorBlender blender = new ColorBlender(1.0F);
      Optional.ofNullable(clientCache.getJewelColorComponents()).ifPresent(colors -> colors.forEach(color -> blender.add(color, 60.0F)));
      float time = (float)ClientScheduler.INSTANCE.getTickCount();
      return blender.getColor(time);
   }

   public static ItemStack create(Consumer<VaultGearData> consumer) {
      ItemStack stack = new ItemStack(ModItems.JEWEL);
      VaultGearData data = VaultGearData.read(stack);
      consumer.accept(data);
      data.write(stack);
      return stack;
   }
}
