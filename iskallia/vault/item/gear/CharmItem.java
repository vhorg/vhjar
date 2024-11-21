package iskallia.vault.item.gear;

import iskallia.vault.config.CharmConfig;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.gear.reader.DecimalModifierReader;
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
import javax.annotation.Nonnull;
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
import net.minecraft.util.Mth;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CharmItem extends BasicItem implements ICurioItem, DataTransferItem, IdentifiableItem, RecyclableItem {
   CharmConfig.Size size;
   private static final int crystalIngredientSize = 10;

   public CharmItem(ResourceLocation id, CharmConfig.Size size) {
      super(id, new Properties().tab(ModItems.GEAR_GROUP).stacksTo(1));
      this.size = size;
   }

   public static float getUsePercentage(ItemStack input) {
      if (input.isEmpty()) {
         return 0.0F;
      } else if (input.getItem() instanceof CharmItem) {
         return !isIdentified(input) ? 1.0F : 1.0F - (float)getUsedVaults(input).size() / getUses(input);
      } else {
         return 0.0F;
      }
   }

   public int getBarWidth(ItemStack pStack) {
      return Math.round(13.0F - (1.0F - getUsePercentage(pStack)) * 13.0F);
   }

   public int getBarColor(ItemStack pStack) {
      return Mth.hsvToRgb(Math.max(0.0F, getUsePercentage(pStack) / 3.0F), 1.0F, 1.0F);
   }

   public boolean isBarVisible(ItemStack pStack) {
      return isIdentified(pStack) && getUsePercentage(pStack) != 1.0F;
   }

   public int getColor() {
      if (this == ModItems.SMALL_CHARM) {
         return 11110389;
      } else if (this == ModItems.LARGE_CHARM) {
         return 8935924;
      } else {
         return this == ModItems.GRAND_CHARM ? 7551734 : 5640437;
      }
   }

   public static Optional<CharmEffect<?>> getCharm(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         AttributeGearData data = AttributeGearData.read(stack);
         return data.getFirstValue(ModGearAttributes.CHARM_EFFECT);
      } else {
         return Optional.empty();
      }
   }

   public static int getUses(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof CharmItem ? stack.getOrCreateTag().getInt("vaultUses") : 0;
   }

   public static void setUses(ItemStack stack, int uses) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         stack.getOrCreateTag().putInt("vaultUses", uses);
      }
   }

   public static boolean hasValue(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof CharmItem ? stack.hasTag() && stack.getOrCreateTag().contains("charmValue") : false;
   }

   public static float getValue(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof CharmItem ? stack.getOrCreateTag().getFloat("charmValue") : 0.0F;
   }

   public static void setValue(ItemStack stack, float value) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         stack.getOrCreateTag().putFloat("charmValue", value);
      }
   }

   public static boolean hasUsesLeft(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof CharmItem ? getUses(stack) > getUsedVaults(stack).size() : false;
   }

   public static List<UUID> getUsedVaults(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         ListTag list = stack.getOrCreateTag().getList("usedVaults", 10);
         List<UUID> ids = new ArrayList<>();

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (tag.contains("id")) {
               ids.add(tag.getUUID("id"));
            }
         }

         return ids;
      } else {
         return Collections.emptyList();
      }
   }

   public static void addUsedVault(ItemStack stack, UUID vaultId) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         ListTag list = stack.getOrCreateTag().getList("usedVaults", 10);
         CompoundTag tag = new CompoundTag();
         tag.putUUID("id", vaultId);
         list.add(tag);
         stack.getOrCreateTag().put("usedVaults", list);
      }
   }

   public static void addFreeUsedVault(ItemStack stack, UUID vaultId) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         ListTag list = stack.getOrCreateTag().getList("freeUsedVaults", 10);
         CompoundTag tag = new CompoundTag();
         tag.putUUID("id", vaultId);
         list.add(tag);
         stack.getOrCreateTag().put("freeUsedVaults", list);
      }
   }

   public static boolean isUsableInVault(ItemStack stack, UUID vaultId) {
      return !stack.isEmpty() && stack.getItem() instanceof CharmItem && vaultId != null;
   }

   public static boolean isIdentified(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof CharmItem) {
         AttributeGearData data = AttributeGearData.read(stack);
         return data.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED) == VaultGearState.IDENTIFIED;
      } else {
         return false;
      }
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      super.fillItemCategory(tab, items);
      if (ModConfigs.isInitialized()) {
         ;
      }
   }

   public Component getName(ItemStack stack) {
      return (Component)(!isIdentified(stack)
         ? new TextComponent("Unidentified ").withStyle(Style.EMPTY.withColor(this.getColor())).append(super.getName(stack).copy())
         : getCharm(stack).map(effect -> {
            CharmConfig.Charm cfg = effect.getCharmConfig();
            TextComponent cmp = new TextComponent(cfg.getName());
            cmp.withStyle(Style.EMPTY.withColor(cfg.getComponentColor()));
            return cmp;
         }).orElseGet(() -> super.getName(stack)));
   }

   public static String getParticleLoc(ItemStack stack) {
      return !isIdentified(stack) ? null : getCharm(stack).map(effect -> effect.getCharmConfig().getParticleLoc()).orElse(null);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      int totalUses = getUses(stack);
      int used = getUsedVaults(stack).size();
      int remaining = Math.max(totalUses - used, 0);
      MutableComponent usesTxt = new TextComponent("Uses: ").append(new TextComponent(String.valueOf(remaining)));
      tooltip.add(usesTxt);
      AttributeGearData data = AttributeGearData.read(stack);
      data.getFirstValue(ModGearAttributes.CHARM_EFFECT)
         .ifPresent(
            charmEffect -> {
               DecimalModifierReader.Percentage<?> percentage = (DecimalModifierReader.Percentage<?>)((CharmEffect.Config)charmEffect.getCharmConfig()
                     .getConfig())
                  .getAttribute()
                  .getReader();
               tooltip.add(
                  new TextComponent(Math.round(getValue(stack) * 100.0F) + "% " + percentage.getModifierName())
                     .setStyle(Style.EMPTY.withColor(percentage.getRgbColor()))
               );
               tooltip.add(new TextComponent("Size: ").append(new TextComponent(String.valueOf(10))));
               tooltip.add(TextComponent.EMPTY);
            }
         );
      MutableComponent slotsTooltip = new TranslatableComponent("curios.slot").append(": ").withStyle(ChatFormatting.GOLD);
      MutableComponent type = new TranslatableComponent("curios.identifier.charm");
      type = type.withStyle(ChatFormatting.YELLOW);
      slotsTooltip.append(type);
      tooltip.add(slotsTooltip);
   }

   public static int getCrystalIngredientSize() {
      return 10;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      InteractionResultHolder<ItemStack> defaultAction = super.use(world, player, hand);
      if (world.isClientSide()) {
         return defaultAction;
      } else if (VHSmpUtil.isArenaWorld(world)) {
         return defaultAction;
      } else {
         return this.tryStartIdentification(player, stack) ? InteractionResultHolder.fail(stack) : defaultAction;
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

         this.inventoryIdentificationTick(sPlayer, stack);
      }
   }

   @Override
   public void tickRoll(ItemStack stack, @Nullable Player player) {
      AttributeGearData data = AttributeGearData.read(stack);
      if (stack.getItem() instanceof CharmItem charmItem) {
         CharmEffect<?> randomTrinket = ModConfigs.CHARM.getRandomTrinketSet(charmItem.size);
         if (randomTrinket != null) {
            data.createOrReplaceAttributeValue(ModGearAttributes.CHARM_EFFECT, randomTrinket);
         }

         data.write(stack);
      }
   }

   @Override
   public void tickFinishRoll(ItemStack stack, @Nullable Player player) {
      AttributeGearData data = AttributeGearData.read(stack);
      Optional<CharmEffect<?>> optCharmEffect = data.getFirstValue(ModGearAttributes.CHARM_EFFECT);
      if (optCharmEffect.isPresent()) {
         CharmEffect<?> trinketEffect = optCharmEffect.get();
         setUses(stack, trinketEffect.getCharmConfig().getRandomUses());
         setValue(stack, trinketEffect.getCharmConfig().getRandomAffinity() / 100.0F);
         data.createOrReplaceAttributeValue(ModGearAttributes.STATE, VaultGearState.IDENTIFIED);
      } else {
         data.createOrReplaceAttributeValue(ModGearAttributes.STATE, VaultGearState.UNIDENTIFIED);
      }

      data.write(stack);
      if (player instanceof ServerPlayer sPlayer) {
         DiscoveredTrinketsData trinketData = DiscoveredTrinketsData.get(sPlayer.getLevel().getServer());
         trinketData.discoverTrinketAndBroadcast(stack, sPlayer);
      }
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      if (isIdentified(stack)) {
         super.curioTick(slotContext, stack);
      }
   }

   public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
      if (isIdentified(stack)) {
         getCharm(stack).ifPresent(trinketEffect -> trinketEffect.onEquip(slotContext.entity(), stack));
         super.onEquip(slotContext, prevStack, stack);
      }
   }

   public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
      super.onEquipFromUse(slotContext, stack);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      if (slotContext.entity() instanceof Player player && ServerVaults.get(player.level).isPresent()) {
         return false;
      } else if (!isIdentified(stack)) {
         return false;
      } else {
         String slot = "charm";
         if (!slot.equals(slotContext.identifier())) {
            return false;
         } else {
            return CuriosApi.getSlotHelper() != null && !CuriosApi.getSlotHelper().getSlotType(slot).<Boolean>map(ISlotType::isVisible).orElse(false)
               ? false
               : super.canEquip(slotContext, stack);
         }
      }
   }

   public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
      if (isIdentified(stack)) {
         getCharm(stack).ifPresent(trinketEffect -> trinketEffect.onUnEquip(slotContext.entity(), stack));
         super.onUnequip(slotContext, newStack, stack);
      }
   }

   public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
      return slotContext.entity() instanceof Player player && ServerVaults.get(player.level).isPresent() ? false : super.canUnequip(slotContext, stack);
   }

   @Override
   public Optional<UUID> getUuid(ItemStack stack) {
      return AttributeGearData.readUUID(stack);
   }

   @Override
   public boolean isValidInput(ItemStack input) {
      return !input.isEmpty() && AttributeGearData.hasData(input);
   }

   @Override
   public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return ModConfigs.VAULT_RECYCLER.getCharmRecyclingOutput();
   }

   @Override
   public float getResultPercentage(ItemStack input) {
      if (input.isEmpty()) {
         return 0.0F;
      } else {
         return !isIdentified(input) ? 1.0F : 1.0F - (float)getUsedVaults(input).size() / getUses(input);
      }
   }
}
