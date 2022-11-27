package iskallia.vault.item.gear;

import iskallia.vault.config.TrinketConfig;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.BasicItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.DiscoveredTrinketsData;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class TrinketItem extends BasicItem implements ICurioItem, DataTransferItem {
   public TrinketItem(ResourceLocation id) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public static ItemStack createRandomTrinket(TrinketEffect<?> trinket) {
      ItemStack stack = createBaseTrinket(trinket);
      setUses(stack, trinket.getTrinketConfig().getRandomUses());
      return stack;
   }

   public static ItemStack createBaseTrinket(TrinketEffect<?> trinket) {
      ItemStack stack = new ItemStack(ModItems.TRINKET);
      AttributeGearData data = AttributeGearData.read(stack);
      data.updateAttribute(ModGearAttributes.STATE, VaultGearState.IDENTIFIED);
      data.updateAttribute(ModGearAttributes.TRINKET_EFFECT, trinket);
      data.write(stack);
      return stack;
   }

   public static Optional<String> getSlotIdentifier(ItemStack stack) {
      return getTrinket(stack).map(TrinketEffect::getConfig).filter(TrinketEffect.Config::hasCuriosSlot).map(TrinketEffect.Config::getCuriosSlot);
   }

   public static Optional<TrinketEffect<?>> getTrinket(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         AttributeGearData data = AttributeGearData.read(stack);
         return data.getFirstValue(ModGearAttributes.TRINKET_EFFECT);
      } else {
         return Optional.empty();
      }
   }

   public static int getUses(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof TrinketItem ? stack.getOrCreateTag().getInt("vaultUses") : 0;
   }

   public static void setUses(ItemStack stack, int uses) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         stack.getOrCreateTag().putInt("vaultUses", uses);
      }
   }

   public static boolean hasUsesLeft(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof TrinketItem ? getUses(stack) > getUsedVaults(stack).size() : false;
   }

   public static List<UUID> getUsedVaults(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         ListTag list = stack.getOrCreateTag().getList("usedVaults", 10);
         List<UUID> ids = new ArrayList<>();

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            ids.add(tag.getUUID("id"));
         }

         return ids;
      } else {
         return Collections.emptyList();
      }
   }

   public static void addUsedVault(ItemStack stack, UUID vaultId) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         ListTag list = stack.getOrCreateTag().getList("usedVaults", 10);
         CompoundTag tag = new CompoundTag();
         tag.putUUID("id", vaultId);
         list.add(tag);
         stack.getOrCreateTag().put("usedVaults", list);
      }
   }

   public static boolean isUsableInVault(ItemStack stack, UUID vaultId) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         ListTag list = stack.getOrCreateTag().getList("usedVaults", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (vaultId.equals(tag.getUUID("id"))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isIdentified(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem) {
         AttributeGearData data = AttributeGearData.read(stack);
         return data.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED) == VaultGearState.IDENTIFIED;
      } else {
         return false;
      }
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      super.fillItemCategory(tab, items);
      if (ModConfigs.isInitialized()) {
         if (this.allowdedIn(tab)) {
            TrinketEffectRegistry.getOrderedEntries().stream().map(TrinketItem::createRandomTrinket).forEach(items::add);
         }
      }
   }

   public Component getName(ItemStack stack) {
      return !isIdentified(stack) ? super.getName(stack) : getTrinket(stack).map(effect -> {
         TrinketConfig.Trinket cfg = effect.getTrinketConfig();
         TextComponent cmp = new TextComponent(cfg.getName());
         cmp.withStyle(Style.EMPTY.withColor(cfg.getComponentColor()));
         return cmp;
      }).orElseGet(() -> super.getName(stack));
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (isIdentified(stack)) {
         int totalUses = getUses(stack);
         int used = getUsedVaults(stack).size();
         int remaining = Math.max(totalUses - used, 0);
         MutableComponent usesTxt = new TextComponent("Uses: ").append(new TextComponent(String.valueOf(remaining)));
         tooltip.add(usesTxt);
         AttributeGearData data = AttributeGearData.read(stack);
         data.getFirstValue(ModGearAttributes.CRAFTED_BY)
            .ifPresent(crafter -> tooltip.add(new TextComponent("Crafted by: ").append(new TextComponent(crafter).setStyle(Style.EMPTY.withColor(16770048)))));
         getTrinket(stack).ifPresent(effect -> {
            TrinketConfig.Trinket cfg = effect.getTrinketConfig();

            for (TextComponent cmp : MiscUtils.splitDescriptionText(cfg.getEffectText())) {
               tooltip.add(cmp.withStyle(ChatFormatting.GRAY));
            }
         });
         getSlotIdentifier(stack).ifPresent(slotIdentifier -> {
            MutableComponent slotsTooltip = new TranslatableComponent("curios.slot").append(": ").withStyle(ChatFormatting.GOLD);
            MutableComponent type = new TranslatableComponent("curios.identifier." + slotIdentifier);
            type = type.withStyle(ChatFormatting.YELLOW);
            slotsTooltip.append(type);
            tooltip.add(TextComponent.EMPTY);
            tooltip.add(slotsTooltip);
         });
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (world.isClientSide()) {
         return InteractionResultHolder.pass(stack);
      } else {
         if (!ServerVaults.isVaultWorld(world) && !VHSmpUtil.isArenaWorld(world)) {
            AttributeGearData data = AttributeGearData.read(stack);
            if (data.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED) == VaultGearState.UNIDENTIFIED) {
               data.updateAttribute(ModGearAttributes.STATE, VaultGearState.ROLLING);
               data.write(stack);
               return InteractionResultHolder.fail(stack);
            }
         }

         return InteractionResultHolder.pass(stack);
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer sPlayer) {
         if (world instanceof ServerLevel && stack.getCount() > 1) {
            while (stack.getCount() > 1) {
               stack.shrink(1);
               ItemStack etching = stack.copy();
               etching.setCount(1);
               MiscUtils.giveItem(sPlayer, etching);
            }
         }

         if (AttributeGearData.<AttributeGearData>read(stack).getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED)
            == VaultGearState.ROLLING) {
            GearRollHelper.tickToll(stack, sPlayer, rollStack -> {
               AttributeGearData data = AttributeGearData.read(stack);
               TrinketEffect<?> randomTrinket = ModConfigs.TRINKET.getRandomTrinketSet();
               if (randomTrinket != null) {
                  data.updateAttribute(ModGearAttributes.TRINKET_EFFECT, randomTrinket);
               }

               data.write(rollStack);
            }, finishStack -> {
               AttributeGearData data = AttributeGearData.read(stack);
               TrinketEffect<?> randomTrinket = ModConfigs.TRINKET.getRandomTrinketSet();
               if (randomTrinket != null) {
                  data.updateAttribute(ModGearAttributes.TRINKET_EFFECT, randomTrinket);
                  setUses(stack, randomTrinket.getTrinketConfig().getRandomUses());
                  data.updateAttribute(ModGearAttributes.STATE, VaultGearState.IDENTIFIED);
               } else {
                  data.updateAttribute(ModGearAttributes.STATE, VaultGearState.UNIDENTIFIED);
               }

               data.write(finishStack);
               DiscoveredTrinketsData trinketData = DiscoveredTrinketsData.get(sPlayer.getLevel().getServer());
               trinketData.discoverTrinketAndBroadcast(finishStack, sPlayer);
            });
         }
      }
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      if (isIdentified(stack)) {
         getTrinket(stack).ifPresent(trinketEffect -> trinketEffect.onWornTick(slotContext.entity(), stack));
         super.curioTick(slotContext, stack);
      }
   }

   public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
      if (isIdentified(stack)) {
         getTrinket(stack).ifPresent(trinketEffect -> trinketEffect.onEquip(slotContext.entity(), stack));
         super.onEquip(slotContext, prevStack, stack);
      }
   }

   public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
      super.onEquipFromUse(slotContext, stack);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      if (slotContext.entity() instanceof Player player && ServerVaults.isInVault(player)) {
         return false;
      } else if (!isIdentified(stack)) {
         return false;
      } else {
         String slot = getSlotIdentifier(stack).orElse(null);
         if (slot != null && slot.equals(slotContext.identifier())) {
            return CuriosApi.getSlotHelper() != null && !CuriosApi.getSlotHelper().getSlotType(slot).<Boolean>map(ISlotType::isVisible).orElse(false)
               ? false
               : super.canEquip(slotContext, stack);
         } else {
            return false;
         }
      }
   }

   public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
      if (isIdentified(stack)) {
         getTrinket(stack).ifPresent(trinketEffect -> trinketEffect.onUnEquip(slotContext.entity(), stack));
         super.onUnequip(slotContext, newStack, stack);
      }
   }

   public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
      return slotContext.entity() instanceof Player player && ServerVaults.isInVault(player) ? false : super.canUnequip(slotContext, stack);
   }
}
