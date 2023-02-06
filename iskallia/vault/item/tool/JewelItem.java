package iskallia.vault.item.tool;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class JewelItem extends Item implements VaultGearItem, DataInitializationItem, DataTransferItem {
   public JewelItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public Component getName(@NotNull ItemStack stack) {
      int color = getColor(stack);
      VaultGearData data = VaultGearData.read(stack);
      if (data.getRarity() == VaultGearRarity.SCRAPPY) {
         return super.getName(stack).copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
      } else {
         String prefix = this.getDescriptionId() + "." + data.getRarity().name().toLowerCase(Locale.ROOT);
         return new TranslatableComponent(prefix)
            .append(new TextComponent(" "))
            .append(super.getName(stack))
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
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

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         this.vaultGearTick(stack, player);
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
      data.setRarity(rarity);
      data.write(stack);
      GearRollHelper.initializeGear(stack);
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
      VaultGearData data = VaultGearData.read(stack);
      ColorBlender blender = new ColorBlender(1.0F);

      for (VaultGearModifier<?> modifier : data.getAllModifierAffixes()) {
         blender.add(modifier.getAttribute().getReader().getRgbColor(), 60.0F);
      }

      float time = (float)ClientScheduler.INSTANCE.getTickCount();
      return blender.getColor(time);
   }
}
