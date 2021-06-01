package iskallia.vault.item.gear;

import com.google.common.collect.Multimap;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class VaultSwordItem extends SwordItem implements VaultGear<VaultSwordItem> {
   public VaultSwordItem(ResourceLocation id, Properties builder) {
      super(VaultGear.Tier.INSTANCE, 0, -2.4F, builder);
      this.setRegistryName(id);
   }

   @Override
   public int getModelsFor(VaultGear.Rarity rarity) {
      return rarity == VaultGear.Rarity.SCRAPPY ? 2 : 2;
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
      return this.onItemRightClick(this, world, player, hand, super.func_77659_a(world, player, hand));
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      this.inventoryTick(this, stack, world, entity, itemSlot, isSelected);
   }

   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      this.addInformation(this, stack, world, tooltip, flag);
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return this.canElytraFly(this, stack, entity);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.elytraFlightTick(this, stack, entity, flightTicks);
   }
}
