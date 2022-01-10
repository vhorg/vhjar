package iskallia.vault.item;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGearHelper;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

public class VoidOrbItem extends BasicItem {
   public VoidOrbItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public Rarity func_77613_e(ItemStack stack) {
      return Rarity.RARE;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringTextComponent.field_240750_d_);
      tooltip.add(new StringTextComponent("Removes a modifier at random from").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("a vault gear item when combined").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("in an anvil.").func_240699_a_(TextFormatting.GRAY));
      Pair<EquipmentSlotType, VAttribute<?, ?>> gearModifier = getPredefinedRemoval(stack);
      if (gearModifier != null) {
         String slotName = StringUtils.capitalize(((EquipmentSlotType)gearModifier.getFirst()).func_188450_d());
         ITextComponent attributeTxt = VaultGearHelper.getDisplayName((VAttribute<?, ?>)gearModifier.getSecond());
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(
            new StringTextComponent("Only for: ")
               .func_240699_a_(TextFormatting.GRAY)
               .func_230529_a_(new StringTextComponent(slotName).func_240699_a_(TextFormatting.AQUA))
         );
         tooltip.add(new StringTextComponent("Removes: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(attributeTxt));
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         if (stack.func_190916_E() > 1) {
            while (stack.func_190916_E() > 1) {
               stack.func_190918_g(1);
               ItemStack orb = stack.func_77946_l();
               orb.func_190920_e(1);
               MiscUtils.giveItem(player, orb);
            }
         }
      }
   }

   @Nullable
   public static Pair<EquipmentSlotType, VAttribute<?, ?>> getPredefinedRemoval(ItemStack stack) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VoidOrbItem) {
         CompoundNBT tag = stack.func_196082_o();
         if (tag.func_150297_b("slot", 3) && tag.func_150297_b("attribute", 8)) {
            EquipmentSlotType slotType = MiscUtils.getEnumEntry(EquipmentSlotType.class, tag.func_74762_e("slot"));
            VAttribute<?, ?> attribute = ModAttributes.REGISTRY.get(new ResourceLocation(tag.func_74779_i("attribute")));
            return attribute == null ? null : new Pair(slotType, attribute);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void setPredefinedRemoval(ItemStack stack, EquipmentSlotType slotType, VAttribute<?, ?> attribute) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VoidOrbItem) {
         CompoundNBT tag = stack.func_196082_o();
         tag.func_74768_a("slot", slotType.ordinal());
         tag.func_74778_a("attribute", attribute.getId().toString());
      }
   }
}
