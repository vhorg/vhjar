package iskallia.vault.item;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.VaultGearHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class ArtisanScrollItem extends BasicItem {
   public static final WeightedList<EquipmentSlotType> SLOTS = new WeightedList<EquipmentSlotType>()
      .add(EquipmentSlotType.MAINHAND, 2)
      .add(EquipmentSlotType.OFFHAND, 2)
      .add(EquipmentSlotType.HEAD, 1)
      .add(EquipmentSlotType.CHEST, 1)
      .add(EquipmentSlotType.LEGS, 1)
      .add(EquipmentSlotType.FEET, 1);

   public ArtisanScrollItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      IFormattableTextComponent displayName = (IFormattableTextComponent)super.func_200295_i(stack);
      return displayName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-1213660)));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringTextComponent.field_240750_d_);
      tooltip.add(new StringTextComponent("Reforges a gear piece to").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("it's unidentified state,").func_240699_a_(TextFormatting.GRAY));
      tooltip.add(new StringTextComponent("allowing you to re-roll it").func_240699_a_(TextFormatting.GRAY));
      Pair<EquipmentSlotType, VAttribute<?, ?>> gearModifier = getPredefinedRoll(stack);
      if (gearModifier != null) {
         String slotName = StringUtils.capitalize(((EquipmentSlotType)gearModifier.getFirst()).func_188450_d());
         ITextComponent attributeTxt = VaultGearHelper.getDisplayName((VAttribute<?, ?>)gearModifier.getSecond());
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(
            new StringTextComponent("Only for: ")
               .func_240699_a_(TextFormatting.GRAY)
               .func_230529_a_(new StringTextComponent(slotName).func_240699_a_(TextFormatting.AQUA))
         );
         tooltip.add(new StringTextComponent("Adds: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(attributeTxt));
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!isInitialized(stack) && entity instanceof ServerPlayerEntity) {
         if (world instanceof ServerWorld) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            if (stack.func_190916_E() > 1) {
               while (stack.func_190916_E() > 1) {
                  stack.func_190918_g(1);
                  ItemStack scroll = stack.func_77946_l();
                  scroll.func_190920_e(1);
                  MiscUtils.giveItem(player, scroll);
               }
            }
         }

         if (generateRoll(stack)) {
            setInitialized(stack, true);
         }
      }
   }

   public static void setInitialized(ItemStack stack, boolean initialized) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof ArtisanScrollItem) {
         stack.func_196082_o().func_74757_a("initialized", initialized);
      }
   }

   public static boolean isInitialized(ItemStack stack) {
      return !stack.func_190926_b() && stack.func_77973_b() instanceof ArtisanScrollItem ? stack.func_196082_o().func_74767_n("initialized") : true;
   }

   @Nullable
   public static Pair<EquipmentSlotType, VAttribute<?, ?>> getPredefinedRoll(ItemStack stack) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof ArtisanScrollItem) {
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

   public static void setPredefinedRoll(ItemStack stack, EquipmentSlotType slotType, VAttribute<?, ?> attribute) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof ArtisanScrollItem) {
         CompoundNBT tag = stack.func_196082_o();
         tag.func_74768_a("slot", slotType.ordinal());
         tag.func_74778_a("attribute", attribute.getId().toString());
      }
   }

   private static boolean generateRoll(ItemStack out) {
      VaultGearConfig config = VaultGearConfig.get(VaultGear.Rarity.OMEGA);
      VaultGearConfig.Tier tierConfig = config.TIERS.get(0);
      String itemKey = MiscUtils.getRandomEntry(tierConfig.BASE_MODIFIERS.keySet(), field_77697_d);

      Item item;
      try {
         item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
      } catch (Exception var9) {
         return false;
      }

      if (!(item instanceof VaultGear)) {
         return false;
      } else {
         EquipmentSlotType slotType = ((VaultGear)item).getIntendedSlot();
         slotType = SLOTS.getRandom(new Random());
         if (slotType == null) {
            return false;
         } else {
            VaultGearConfig.BaseModifiers modifiers = tierConfig.BASE_MODIFIERS.get(itemKey);
            WeightedList<Pair<VAttribute<?, ?>, VAttribute.Instance.Generator<?>>> generatorList = new WeightedList<>();
            ModAttributes.REGISTRY
               .values()
               .stream()
               .map(attr -> new Pair(attr, modifiers.getGenerator((VAttribute<?, ?>)attr)))
               .filter(pair -> pair.getSecond() != null)
               .forEach(
                  pair -> generatorList.add(
                     new Pair(pair.getFirst(), ((WeightedList.Entry)pair.getSecond()).value), ((WeightedList.Entry)pair.getSecond()).weight
                  )
               );
            if (generatorList.isEmpty()) {
               return false;
            } else {
               Pair<VAttribute<?, ?>, VAttribute.Instance.Generator<?>> generatorPair = generatorList.getRandom(field_77697_d);
               if (generatorPair == null) {
                  return false;
               } else {
                  setPredefinedRoll(out, slotType, (VAttribute<?, ?>)generatorPair.getFirst());
                  return true;
               }
            }
         }
      }
   }
}
