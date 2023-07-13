package iskallia.vault.item.gear;

import iskallia.vault.config.EtchingConfig;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.BasicItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.VHSmpUtil;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EtchingItem extends BasicItem implements DataTransferItem, IdentifiableItem {
   public EtchingItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public static ItemStack createEtchingStack(EtchingSet<?> etchingSet) {
      ItemStack etchingStack = new ItemStack(ModItems.ETCHING);
      AttributeGearData data = AttributeGearData.read(etchingStack);
      data.updateAttribute(ModGearAttributes.ETCHING, etchingSet);
      data.updateAttribute(ModGearAttributes.STATE, VaultGearState.IDENTIFIED);
      data.write(etchingStack);
      return etchingStack;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      super.fillItemCategory(tab, items);
      if (ModConfigs.isInitialized()) {
         if (this.allowdedIn(tab)) {
            EtchingRegistry.getOrderedEntries().stream().map(EtchingItem::createEtchingStack).forEach(items::add);
         }
      }
   }

   public Component getName(ItemStack stack) {
      MutableComponent name = new TranslatableComponent(this.getDescriptionId(stack));
      AttributeGearData.<AttributeGearData>read(stack).getFirstValue(ModGearAttributes.ETCHING).ifPresent(etchingSet -> {
         EtchingConfig.Etching config = ModConfigs.ETCHING.getEtchingConfig((EtchingSet<?>)etchingSet);
         if (config != null) {
            name.setStyle(name.getStyle().withColor(config.getComponentColor()));
         }
      });
      return name;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (world.isClientSide()) {
         return InteractionResultHolder.pass(stack);
      } else {
         return !VHSmpUtil.isArenaWorld(world) && this.tryStartIdentification(player, stack)
            ? InteractionResultHolder.fail(stack)
            : InteractionResultHolder.pass(stack);
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         if (world instanceof ServerLevel && stack.getCount() > 1) {
            while (stack.getCount() > 1) {
               stack.shrink(1);
               ItemStack etching = stack.copy();
               etching.setCount(1);
               MiscUtils.giveItem(player, etching);
            }
         }

         this.inventoryIdentificationTick(player, stack);
      }
   }

   @Override
   public void tickRoll(ItemStack stack, Player player) {
      AttributeGearData data = AttributeGearData.read(stack);
      EtchingSet<?> etchingSet = ModConfigs.ETCHING.getRandomEtchingSet();
      if (etchingSet != null) {
         data.updateAttribute(ModGearAttributes.ETCHING, etchingSet);
      }

      data.write(stack);
   }

   @Override
   public void tickFinishRoll(ItemStack stack, Player player) {
      AttributeGearData data = AttributeGearData.read(stack);
      Optional<EtchingSet<?>> optEtchingSet = data.getFirstValue(ModGearAttributes.ETCHING);
      if (optEtchingSet.isPresent()) {
         data.updateAttribute(ModGearAttributes.STATE, VaultGearState.IDENTIFIED);
      } else {
         data.updateAttribute(ModGearAttributes.STATE, VaultGearState.UNIDENTIFIED);
      }

      data.write(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      AttributeGearData data = AttributeGearData.read(stack);
      if (data.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED) == VaultGearState.IDENTIFIED) {
         data.getFirstValue(ModGearAttributes.ETCHING).ifPresent(etchingSet -> {
            EtchingConfig.Etching config = ModConfigs.ETCHING.getEtchingConfig((EtchingSet<?>)etchingSet);
            if (config != null) {
               tooltip.add(new TextComponent("Etching: ").append(config.getName()).withStyle(Style.EMPTY.withColor(config.getComponentColor())));
               tooltip.add(TextComponent.EMPTY);

               for (TextComponent cmp : MiscUtils.splitDescriptionText(config.getEffectText())) {
                  tooltip.add(cmp.withStyle(ChatFormatting.GRAY));
               }
            }
         });
      }
   }
}
