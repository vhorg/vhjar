package iskallia.vault.item.gear;

import iskallia.vault.item.BasicItem;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EtchingItem extends BasicItem implements VaultGear<EtchingItem> {
   public EtchingItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   @Override
   public int getModelsFor(VaultGear.Rarity rarity) {
      return VaultGear.Set.values().length;
   }

   @Override
   public EquipmentSlotType getIntendedSlot() {
      return null;
   }

   public boolean isDamageable(ItemStack stack) {
      return this.isDamageable(this, stack);
   }

   public int getMaxDamage(ItemStack stack) {
      return this.getMaxDamage(this, stack, super.getMaxDamage(stack));
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      return this.getDisplayName(this, stack, super.func_200295_i(stack));
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      return this.onItemRightClick(this, world, player, hand, super.func_77659_a(world, player, hand));
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayerEntity) {
         this.inventoryTick(this, stack, world, (ServerPlayerEntity)entity, itemSlot, isSelected);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
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
