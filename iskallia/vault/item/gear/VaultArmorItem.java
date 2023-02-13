package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearArmorMaterial;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;

public class VaultArmorItem extends DyeableArmorItem implements VaultGearItem {
   public static VaultArmorItem forSlot(EquipmentSlot equipmentSlot) {
      return switch (equipmentSlot) {
         case HEAD -> ModItems.HELMET;
         case CHEST -> ModItems.CHESTPLATE;
         case LEGS -> ModItems.LEGGINGS;
         case FEET -> ModItems.BOOTS;
         default -> null;
      };
   }

   public VaultArmorItem(ResourceLocation id, EquipmentSlot slot, Properties builder) {
      super(VaultGearArmorMaterial.INSTANCE, slot, builder);
      this.setRegistryName(id);
   }

   @Nullable
   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return this.getSlot();
   }

   @NotNull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.ARMOR;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      switch (this.getSlot()) {
         case HEAD:
            return ProficiencyType.HELMET;
         case CHEST:
            return ProficiencyType.CHESTPLATE;
         case LEGS:
            return ProficiencyType.LEGGINGS;
         case FEET:
            return ProficiencyType.BOOTS;
         default:
            throw new IllegalArgumentException("VaultGear Armor having an incorrect slot: " + this.getSlot());
      }
   }

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return this.getSlot();
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      VaultGearData gearData = VaultGearData.read(stack);
      VaultGearRarity rarity = gearData.getRarity();
      EquipmentSlot intendedSlot = this.getIntendedSlot(stack);
      Set<ResourceLocation> possibleIds = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getPossibleRolls(this, rarity, intendedSlot);
      return MiscUtils.getRandomEntry(possibleIds, random);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
      return VaultGearHelper.getModifiers(stack, slot);
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
      return VaultGearData.read(stack).getState() == VaultGearState.IDENTIFIED;
   }

   public int getMaxDamage(ItemStack stack) {
      return VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum());
   }

   public Component getName(ItemStack stack) {
      return VaultGearHelper.getDisplayName(stack, super.getName(stack));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      EquipmentSlot slot = Mob.getEquipmentSlotForItem(heldStack);
      InteractionResultHolder<ItemStack> defaultAction = this.canEquip(heldStack, slot, player)
         ? super.use(world, player, hand)
         : InteractionResultHolder.fail(heldStack);
      return VaultGearHelper.rightClick(world, player, hand, defaultAction);
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

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return false;
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return false;
   }

   public int getColor(ItemStack stack) {
      return VaultGearHelper.getGearColor(stack);
   }

   public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
      return VaultGearData.read(stack).getState() == VaultGearState.IDENTIFIED && super.canEquip(stack, armorType, entity);
   }

   public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
      super.initializeClient(consumer);
      consumer.accept(VaultArmorRenderProperties.INSTANCE);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlot slot, String type) {
      VaultGearData gearData = VaultGearData.read(itemStack);
      return gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
         .map(id -> id.getNamespace() + ":textures/item/" + id.getPath())
         .map(id -> id.substring(0, id.lastIndexOf("/")))
         .map(id -> id + "/armor" + (slot == EquipmentSlot.LEGS ? "_layer2" : "_layer1"))
         .map(id -> (String)(type == null ? id : id + "_" + type))
         .map(id -> id + ".png")
         .orElse("");
   }
}
