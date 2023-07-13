package iskallia.vault.item;

import com.google.common.base.Functions;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.BottleItemRenderer;
import iskallia.vault.item.tool.IManualModelLoading;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.AlchemistTalent;
import iskallia.vault.skill.talent.type.PrudentTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.ServerVaults;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class BottleItem extends Item implements VaultGearItem, IManualModelLoading {
   public static final String TYPE = "type";
   public static final String RECHARGE = "recharge";
   public static final String VAULT = "vault";
   public static final String PROGRESS = "progress";
   public static final String CHARGES = "charges";

   public BottleItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public boolean isDamageable(ItemStack stack) {
      return getType(stack).isPresent();
   }

   public int getMaxDamage(ItemStack stack) {
      return ModConfigs.POTION.getPotion(getType(stack).orElse(BottleItem.Type.VIAL)).getCharges();
   }

   public int getDamage(ItemStack stack) {
      return stack.hasTag() ? this.getMaxDamage(stack) - stack.getOrCreateTag().getInt("charges") : 0;
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entity) {
      if (entity instanceof ServerPlayer player) {
         Vault vault = ServerVaults.get(player.level).orElse(null);
         if (vault != null && isActive(vault, stack) && stack.getOrCreateTag().getInt("charges") > 0) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            VaultGearData data = VaultGearData.read(stack);
            getEffect(stack, player).ifPresent(entity::addEffect);
            getType(stack).ifPresent(type -> entity.heal(ModConfigs.POTION.getPotion(type).getHealing()));
            consumeCharge(stack, player);
            world.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
            AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
         }

         return stack;
      } else {
         return stack;
      }
   }

   public static Optional<MobEffectInstance> getEffect(ItemStack stack, ServerPlayer player) {
      return getType(stack).map(type -> {
         int duration = ModConfigs.POTION.getPotion(type).getEffectDuration();
         float increase = 0.0F;
         TalentTree talents = PlayerTalentsData.get((ServerLevel)player.level).getTalents(player);

         for (AlchemistTalent talent : talents.getAll(AlchemistTalent.class, Skill::isUnlocked)) {
            increase += talent.getDurationIncrease();
         }

         duration = (int)(duration * (1.0F + increase));
         return new MobEffectInstance(ModEffects.BOTTLE, duration, 0, false, false, true);
      });
   }

   public static void consumeCharge(ItemStack stack, ServerPlayer player) {
      float probability = 0.0F;
      TalentTree talents = PlayerTalentsData.get((ServerLevel)player.level).getTalents(player);

      for (PrudentTalent talent : talents.getAll(PrudentTalent.class, Skill::isUnlocked)) {
         probability += talent.getProbability();
      }

      if (player.level.getRandom().nextFloat() >= probability) {
         int currentCharges = stack.getOrCreateTag().getInt("charges");
         stack.getOrCreateTag().putInt("charges", currentCharges - 1);
      }
   }

   public int getUseDuration(@NotNull ItemStack stack) {
      if (stack.hasTag() && stack.getOrCreateTag().contains("vault")) {
         int currentCharges = stack.getOrCreateTag().getInt("charges");
         if (currentCharges > 0) {
            return 32;
         }
      }

      return Integer.MAX_VALUE;
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack stack) {
      return UseAnim.DRINK;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.hasTag() && stack.getOrCreateTag().contains("vault")) {
         int currentCharges = stack.getOrCreateTag().getInt("charges");
         if (currentCharges > 0) {
            return ItemUtils.startUsingInstantly(world, player, hand);
         }
      }

      return super.use(world, player, hand);
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      return new TranslatableComponent(getType(stack).map(type -> this.getDescriptionId() + "." + type.getName()).orElseGet(() -> super.getDescriptionId()))
         .setStyle(Style.EMPTY.withColor(getColor(stack)));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      if (ModConfigs.POTION != null) {
         getType(stack)
            .ifPresent(
               type -> {
                  tooltip.add(
                     new TextComponent("Heals ")
                        .append(new TextComponent(String.valueOf(ModConfigs.POTION.getPotion(type).getHealing())).setStyle(Style.EMPTY.withColor(8254855)))
                        .append(new TextComponent(" hitpoints"))
                  );
                  getRecharge(stack)
                     .ifPresent(
                        recharge -> {
                           switch (recharge) {
                              case TIME:
                                 tooltip.add(
                                    new TextComponent("Recharges every ")
                                       .append(
                                          new TextComponent(ModConfigs.POTION.getPotion(type).getTimeRecharge() / 1200 + " minutes")
                                             .setStyle(Style.EMPTY.withColor(getColor(stack)))
                                       )
                                 );
                                 break;
                              case MOBS:
                                 tooltip.add(
                                    new TextComponent("Recharges every ")
                                       .append(
                                          new TextComponent(ModConfigs.POTION.getPotion(type).getMobRecharge() + " mob kills")
                                             .setStyle(Style.EMPTY.withColor(getColor(stack)))
                                       )
                                 );
                           }
                        }
                     );
               }
            );
      }

      tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
   }

   @Override
   public void addTooltipItemLevel(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   @Override
   public void addRepairTooltip(List<Component> tooltip, int usedRepairs, int totalRepairs) {
   }

   @Override
   public void addTooltipRarity(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   @Override
   public void addTooltipCraftingPotential(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   @Override
   public void addTooltipDurability(List<Component> tooltip, ItemStack stack) {
      if (stack.isDamageableItem() && stack.getMaxDamage() > 0) {
         tooltip.add(
            new TextComponent("Charges: ")
               .append(new TextComponent("%d/%d".formatted(stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage())).withStyle(ChatFormatting.GRAY))
         );
      }
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         this.vaultGearTick(stack, player);
         if (stack.getOrCreateTag().contains("vault")) {
            Vault vault = ServerVaults.get(UUID.fromString(stack.getOrCreateTag().getString("vault"))).orElse(null);
            if (vault == null || !vault.get(Vault.LISTENERS).contains(entity.getUUID())) {
               stack.getOrCreateTag().remove("vault");
               getType(stack).ifPresent(type -> stack.getOrCreateTag().putInt("charges", ModConfigs.POTION.getPotion(type).getCharges()));
            }
         }
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return VaultGearHelper.shouldPlayGearReequipAnimation(oldStack, newStack, slotChanged);
   }

   @Override
   public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return null;
   }

   @Nonnull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.DRINK;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.UNKNOWN;
   }

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return null;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return null;
   }

   public void fillItemCategory(@NotNull CreativeModeTab category, @NotNull NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         for (BottleItem.Type type : BottleItem.Type.values()) {
            for (BottleItem.Recharge recharge : BottleItem.Recharge.values()) {
               items.add(create(type, recharge));
            }
         }
      }
   }

   @Override
   public void addTooltipAffixGroup(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, List<Component> tooltip, boolean displayDetails) {
      List<VaultGearModifier<?>> affixes = data.getModifiers(type);
      int emptyAffixes = getEmptyModifierSlots(stack);
      if (displayDetails) {
         tooltip.add(new TextComponent(type.getPlural() + ":").withStyle(ChatFormatting.GRAY));
      }

      affixes.forEach(modifier -> modifier.getDisplay(data, type, stack, displayDetails).ifPresent(tooltip::add));
      if (displayDetails && type == VaultGearModifier.AffixType.SUFFIX) {
         for (int i = 0; i < emptyAffixes; i++) {
            tooltip.add(this.addTooltipEmptyAffixes(type));
         }
      }
   }

   public static ItemStack create(BottleItem.Type type, BottleItem.Recharge recharge) {
      ItemStack stack = new ItemStack(ModItems.BOTTLE);
      VaultGearData data = VaultGearData.read(stack);
      data.setState(VaultGearState.IDENTIFIED);
      if (type != null) {
         stack.getOrCreateTag().putString("type", type.getName());
         stack.getOrCreateTag().putInt("charges", 6);
      }

      if (recharge != null) {
         stack.getOrCreateTag().putString("recharge", recharge.getName());
      }

      data.write(stack);
      return stack;
   }

   public static Optional<BottleItem.Type> getType(ItemStack stack) {
      if (stack != null && stack.hasTag()) {
         String raw = stack.getOrCreateTag().getString("type");
         return Optional.ofNullable(BottleItem.Type.fromString(raw));
      } else {
         return Optional.empty();
      }
   }

   public static Optional<BottleItem.Recharge> getRecharge(ItemStack stack) {
      if (stack != null && stack.hasTag()) {
         String raw = stack.getOrCreateTag().getString("recharge");
         return Optional.ofNullable(BottleItem.Recharge.fromString(raw));
      } else {
         return Optional.empty();
      }
   }

   public static int getCharges(ItemStack stack) {
      return stack != null && stack.hasTag() ? stack.getOrCreateTag().getInt("charges") : 0;
   }

   public static int getEmptyModifierSlots(ItemStack stack) {
      return getType(stack).map(type -> ModConfigs.POTION.getPotion(type).getModifiers() - getCraftedModifierSlots(stack)).orElse(0);
   }

   public static int getCraftedModifierSlots(ItemStack stack) {
      if (stack.getItem() instanceof BottleItem && stack.hasTag()) {
         BottleItem.Type type = getType(stack).orElse(null);
         if (type == null) {
            return 0;
         } else {
            VaultGearData data = VaultGearData.read(stack);
            int size = 0;

            for (VaultGearModifier<?> ignored : data.getAllModifierAffixes()) {
               size++;
            }

            return size;
         }
      } else {
         return 0;
      }
   }

   public static boolean isActive(Vault vault, ItemStack stack) {
      if (stack.getItem() instanceof BottleItem && stack.hasTag()) {
         String uuid = stack.getOrCreateTag().getString("vault");
         return vault.get(Vault.ID).toString().equals(uuid);
      } else {
         return false;
      }
   }

   public static Optional<ItemStack> getActive(Vault vault, ServerPlayer player) {
      int size = ((InventorySnapshotData.InventoryAccessor)player.getInventory()).getSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (isActive(vault, stack)) {
            return Optional.of(stack);
         }
      }

      return Optional.empty();
   }

   public static Optional<ItemStack> getAnyInactive(ServerPlayer player) {
      int size = ((InventorySnapshotData.InventoryAccessor)player.getInventory()).getSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (stack.getItem() instanceof BottleItem && stack.hasTag() && !stack.getOrCreateTag().contains("vault")) {
            return Optional.of(stack);
         }
      }

      return Optional.empty();
   }

   public static void onMobKill(ItemStack stack) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && recharge == BottleItem.Recharge.MOBS) {
         CompoundTag nbt = stack.getOrCreateTag();
         int value = nbt.getInt("progress") + 1;
         nbt.putInt("progress", value);
         if (value % ModConfigs.POTION.getPotion(type).getMobRecharge() == 0 && nbt.getInt("charges") < ModConfigs.POTION.getPotion(type).getCharges()) {
            nbt.putInt("charges", nbt.getInt("charges") + 1);
         }
      }
   }

   public static void onTimeTick(ItemStack stack) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && recharge == BottleItem.Recharge.TIME) {
         CompoundTag nbt = stack.getOrCreateTag();
         int value = nbt.getInt("progress") + 1;
         nbt.putInt("progress", value);
         if (value % ModConfigs.POTION.getPotion(type).getTimeRecharge() == 0 && nbt.getInt("charges") < ModConfigs.POTION.getPotion(type).getCharges()) {
            nbt.putInt("charges", nbt.getInt("charges") + 1);
         }
      }
   }

   public static int getColor(ItemStack stack) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && recharge != null) {
         return switch (recharge) {
            case TIME -> 16150747;
            case MOBS -> 11932520;
         };
      } else {
         return 16777215;
      }
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      for (BottleItem.Type type : BottleItem.Type.values()) {
         consumer.accept(new ModelResourceLocation("the_vault:bottle/%s/empty#inventory".formatted(type.getName())));
         consumer.accept(new ModelResourceLocation("the_vault:bottle/%s/juice#inventory".formatted(type.getName())));
      }
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return BottleItemRenderer.INSTANCE;
         }
      });
   }

   public static enum Recharge {
      TIME,
      MOBS;

      private static final Map<String, BottleItem.Recharge> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(BottleItem.Recharge::getName, Functions.identity()));

      public String getName() {
         return this.name().toLowerCase();
      }

      public static BottleItem.Recharge fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase());
      }
   }

   public static enum Type {
      VIAL(6, 0, 6000, 150, 400, 4),
      POTION(6, 1, 6000, 150, 400, 6),
      MIXTURE(6, 2, 6000, 150, 400, 8),
      BREW(6, 3, 6000, 150, 400, 10);

      private static final Map<String, BottleItem.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(BottleItem.Type::getName, Functions.identity()));
      private final int charges;
      private final int modifiers;
      private final int timeRecharge;
      private final int mobRecharge;
      private final int effectDuration;
      private final int healing;

      private Type(int charges, int modifiers, int timeRecharge, int mobRecharge, int effectDuration, int healing) {
         this.charges = charges;
         this.modifiers = modifiers;
         this.timeRecharge = timeRecharge;
         this.mobRecharge = mobRecharge;
         this.effectDuration = effectDuration;
         this.healing = healing;
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public static BottleItem.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase());
      }
   }
}
