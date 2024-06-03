package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.item.VaultGearToolTier;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

public class VaultAxeItem extends AxeItem implements VaultGearItem {
   public VaultAxeItem(ResourceLocation id, Properties builder) {
      super(VaultGearToolTier.INSTANCE, 0.0F, -2.4F, builder);
      this.setRegistryName(id);
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      VaultGearData gearData = VaultGearData.read(stack);
      EquipmentSlot intendedSlot = this.getIntendedSlot(stack);
      return ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRandomRoll(stack, gearData, intendedSlot, random);
   }

   @Override
   public Optional<? extends DynamicModel<?>> resolveDynamicModel(ItemStack stack, ResourceLocation key) {
      return (Optional<? extends DynamicModel<?>>)ModDynamicModels.Axes.REGISTRY.get(key);
   }

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return EquipmentSlot.MAINHAND;
   }

   @NotNull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.AXE;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.AXE;
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, p_43296_ -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
      return true;
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      return 1.0F;
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return false;
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
      return VaultGearHelper.getModifiers(stack, slot);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return VaultGearHelper.shouldPlayGearReequipAnimation(oldStack, newStack, slotChanged);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         items.add(this.defaultItem());
      }
   }

   public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
      return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   public boolean isDamageable(ItemStack stack) {
      return GearDataCache.of(stack).getState() == VaultGearState.IDENTIFIED;
   }

   public int getMaxDamage(ItemStack stack) {
      return VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum());
   }

   public Component getName(ItemStack stack) {
      return VaultGearHelper.getDisplayName(stack, super.getName(stack));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      return VaultGearHelper.rightClick(world, player, hand, super.use(world, player, hand));
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         this.vaultGearTick(stack, player);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
   }

   public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment instanceof MendingEnchantment ? false : enchantment.category.canEnchant(Items.NETHERITE_SWORD);
   }
}
