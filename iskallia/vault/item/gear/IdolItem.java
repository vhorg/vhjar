package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearDataTooltip;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.BasicItem;
import java.util.List;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class IdolItem extends BasicItem implements VaultGearItem {
   private final VaultGod type;

   public IdolItem(ResourceLocation id, VaultGod type, Properties properties) {
      super(id, properties);
      this.type = type;
   }

   public VaultGod getType() {
      return this.type;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return switch (this.type) {
         case VELARA -> ModDynamicModels.Idols.VELARA.getId();
         case TENOS -> ModDynamicModels.Idols.TENOS.getId();
         case WENDARR -> ModDynamicModels.Idols.WENDARR.getId();
         case IDONA -> ModDynamicModels.Idols.IDONA.getId();
      };
   }

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return EquipmentSlot.OFFHAND;
   }

   @NotNull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.IDOL;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.IDOL;
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         items.add(this.defaultItem());
      }
   }

   public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
      return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
      return VaultGearHelper.getModifiers(stack, slot);
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   public boolean isDamageable(ItemStack stack) {
      return true;
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
   @Override
   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      tooltip.addAll(VaultGearDataTooltip.createTooltip(stack, GearTooltip.itemTooltip()));
   }
}
