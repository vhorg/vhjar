package iskallia.vault.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import iskallia.vault.item.BasicItem;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IdolItem extends BasicItem implements VaultGear<IdolItem> {
   private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-2C13A33DB5CF");
   private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-4CCE9785ACA3");
   private final PlayerFavourData.VaultGodType type;

   public IdolItem(ResourceLocation id, PlayerFavourData.VaultGodType type, Properties properties) {
      super(id, properties);
      this.type = type;
   }

   public PlayerFavourData.VaultGodType getType() {
      return this.type;
   }

   @Override
   public int getModelsFor(VaultGear.Rarity rarity) {
      return 1;
   }

   @Nullable
   @Override
   public EquipmentSlotType getIntendedSlot() {
      return EquipmentSlotType.OFFHAND;
   }

   public void func_150895_a(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.func_194125_a(group)) {
         this.fillItemGroup(items);
      }
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
      if (this.isIntendedForSlot(slot)) {
         Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
         builder.put(Attributes.field_233823_f_, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Idol modifier", 0.0, Operation.ADDITION));
         builder.put(Attributes.field_233825_h_, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Idol modifier", 0.0, Operation.ADDITION));
         return this.getAttributeModifiers(this, slot, stack, builder.build());
      } else {
         return ImmutableMultimap.of();
      }
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
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
      this.splitStack(this, stack, world, entity);
      if (entity instanceof ServerPlayerEntity) {
         this.inventoryTick(this, stack, world, (ServerPlayerEntity)entity, itemSlot, isSelected);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      this.addInformation(this, stack, tooltip, Screen.func_231173_s_());
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return this.canElytraFly(this, stack, entity);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.elytraFlightTick(this, stack, entity, flightTicks);
   }
}
