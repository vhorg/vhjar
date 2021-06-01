package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.attribute.EnumAttribute;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultArmorItem extends DyeableArmorItem implements VaultGear<VaultArmorItem> {
   public VaultArmorItem(ResourceLocation id, EquipmentSlotType slot, Properties builder) {
      super(VaultGear.Material.INSTANCE, slot, builder);
      this.setRegistryName(id);
   }

   public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
      return this.func_185083_B_();
   }

   @Override
   public int getModelsFor(VaultGear.Rarity rarity) {
      return 11;
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
      return this.getAttributeModifiers(this, slot, stack, super.getAttributeModifiers(slot, stack));
   }

   public boolean isDamageable(ItemStack stack) {
      return this.isDamageable(this, stack);
   }

   public int getMaxDamage(ItemStack stack) {
      return this.getMaxDamage(this, stack, super.getMaxDamage(stack));
   }

   public ITextComponent func_200295_i(ItemStack itemStack) {
      return this.getDisplayName(this, itemStack, super.func_200295_i(itemStack));
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack heldStack = player.func_184586_b(hand);
      EquipmentSlotType slot = MobEntity.func_184640_d(heldStack);
      return this.onItemRightClick(
         this, world, player, hand, this.canEquip(heldStack, slot, player) ? super.func_77659_a(world, player, hand) : ActionResult.func_226251_d_(heldStack)
      );
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
   }

   public void func_77624_a(ItemStack itemStack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(itemStack, world, tooltip, flag);
      this.addInformation(this, itemStack, world, tooltip, flag);
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return this.canElytraFly(this, stack, entity);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.elytraFlightTick(this, stack, entity, flightTicks);
   }

   public int func_200886_f(ItemStack stack) {
      return this.getColor(this, stack);
   }

   public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
      EnumAttribute<VaultGear.State> stateAttribute = ModAttributes.GEAR_STATE.get(stack).orElse(null);
      return stateAttribute != null && stateAttribute.getValue(stack) == VaultGear.State.IDENTIFIED && super.canEquip(stack, armorType, entity);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
      return this.getArmorModel(this, entityLiving, itemStack, armorSlot, _default);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlotType slot, String type) {
      return this.getArmorTexture(this, itemStack, entity, slot, type);
   }
}
