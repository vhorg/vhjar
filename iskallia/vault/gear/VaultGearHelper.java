package iskallia.vault.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.tool.ColorBlender;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class VaultGearHelper {
   public static void initializeGearRollType(ItemStack stack, ServerPlayer player) {
      initializeGearRollType(stack, player, JavaRandom.ofNanoTime());
   }

   public static void initializeGearRollType(ItemStack stack, ServerPlayer player, RandomSource random) {
      int playerLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player.getUUID()).getVaultLevel();
      initializeGearRollType(stack, playerLevel, random);
   }

   public static void initializeGearRollType(ItemStack stack, int gearLevel, RandomSource random) {
      VaultGearData data = VaultGearData.read(stack);
      if (data.hasAttribute(ModGearAttributes.GEAR_ROLL_TYPE_POOL) && !data.hasAttribute(ModGearAttributes.GEAR_ROLL_TYPE)) {
         data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE_POOL).ifPresent(typePool -> {
            String rollType = ModConfigs.VAULT_GEAR_TYPE_POOL_CONFIG.getGearRollType(typePool, gearLevel, random);
            if (rollType != null) {
               data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, rollType);
               data.write(stack);
            }
         });
      }

      if (!data.hasAttribute(ModGearAttributes.GEAR_ROLL_TYPE)) {
         data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll().getName());
         data.write(stack);
      }
   }

   public static InteractionResultHolder<ItemStack> rightClick(Level world, Player player, InteractionHand hand, InteractionResultHolder<ItemStack> defaultUse) {
      if (world.isClientSide()) {
         return defaultUse;
      } else if (VHSmpUtil.isArenaWorld(world)) {
         return defaultUse;
      } else {
         ItemStack stack = player.getItemInHand(hand);
         return !stack.isEmpty() && stack.getItem() instanceof VaultGearItem gearItem && gearItem.tryStartIdentification(player, stack)
            ? InteractionResultHolder.fail(stack)
            : defaultUse;
      }
   }

   public static Component getDisplayName(ItemStack stack, Component defaultName) {
      if (!ModConfigs.isInitialized()) {
         return defaultName;
      } else {
         GearDataCache clientCache = GearDataCache.of(stack);
         if (clientCache.getState() == VaultGearState.UNIDENTIFIED) {
            Style style = Optional.ofNullable(clientCache.getGearRollType())
               .flatMap(ModConfigs.VAULT_GEAR_TYPE_CONFIG::getRollPool)
               .map(pool -> defaultName.getStyle().withColor(TextColor.fromRgb(pool.getColor())))
               .orElse(defaultName.getStyle());
            return new TextComponent("Unidentified ").setStyle(style).append(defaultName.copy().setStyle(style));
         } else {
            ColorBlender colorBlender = new ColorBlender(1.0F);
            Optional.ofNullable(clientCache.getGearColorComponents()).ifPresent(colors -> colors.forEach(colorx -> colorBlender.add(colorx, 60.0F)));
            float time = (float)ClientScheduler.INSTANCE.getTick();
            int color = colorBlender.getColor(time);
            Optional<String> customName = Optional.ofNullable(clientCache.getGearName());
            return (Component)customName.<MutableComponent>map(s -> new TextComponent(s).setStyle(Style.EMPTY.withColor(color)))
               .orElseGet(() -> defaultName.copy().setStyle(defaultName.getStyle().withColor(color)));
         }
      }
   }

   public static int getGearColor(ItemStack stack) {
      GearDataCache clientCache = GearDataCache.of(stack);
      if (clientCache.getState() == VaultGearState.UNIDENTIFIED) {
         return -1;
      } else {
         CompoundTag displayTag = stack.getTagElement("display");
         return displayTag != null && displayTag.contains("color", 3) ? displayTag.getInt("color") : clientCache.getGearColor(-1);
      }
   }

   public static boolean shouldPlayGearReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !slotChanged && ItemStack.isSame(oldStack, newStack)
         ? AttributeGearData.readUUID(oldStack)
            .map(oldUuid -> AttributeGearData.readUUID(newStack).map(newUuid -> !oldUuid.equals(newUuid)).orElse(true))
            .orElse(true)
         : true;
   }

   public static Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, EquipmentSlot slot) {
      VaultGearItem gearItem = VaultGearItem.of(stack);
      if (!gearItem.isIntendedForSlot(stack, slot)) {
         return ImmutableMultimap.of();
      } else {
         return (Multimap<Attribute, AttributeModifier>)(gearItem.isBroken(stack) ? ImmutableMultimap.of() : getModifiers(VaultGearData.read(stack)));
      }
   }

   public static Multimap<Attribute, AttributeModifier> getModifiers(UUID uuid, Stream<VaultGearAttributeInstance<?>> instances) {
      AttributeGearData data = AttributeGearData.empty();
      data.setIdentifier(uuid);
      instances.forEach(instance -> data.createOrReplaceAttributeValue(instance.getAttribute(), instance.getValue()));
      return getModifiers(data);
   }

   public static Multimap<Attribute, AttributeModifier> getModifiers(AttributeGearData data) {
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      UUID identifier = data.getIdentifier();
      if (data.hasAttribute(ModGearAttributes.ATTACK_DAMAGE)) {
         double attackDamage = data.get(ModGearAttributes.ATTACK_DAMAGE, VaultGearAttributeTypeMerger.doubleSum());
         addAttribute(builder, Attributes.ATTACK_DAMAGE, attackDamage, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.ATTACK_SPEED)) {
         double attackSpeed = data.get(ModGearAttributes.ATTACK_SPEED, VaultGearAttributeTypeMerger.doubleSum());
         addAttribute(builder, Attributes.ATTACK_SPEED, attackSpeed, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.ATTACK_SPEED_PERCENT)) {
         double attackSpeed = data.get(ModGearAttributes.ATTACK_SPEED_PERCENT, VaultGearAttributeTypeMerger.doubleSum());
         addAttribute(builder, Attributes.ATTACK_SPEED, attackSpeed, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.ARMOR)) {
         double armor = data.get(ModGearAttributes.ARMOR, VaultGearAttributeTypeMerger.intSum()).intValue();
         addAttribute(builder, Attributes.ARMOR, armor, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.ARMOR_PERCENTILE)) {
         double incArmor = data.get(ModGearAttributes.ARMOR_PERCENTILE, VaultGearAttributeTypeMerger.floatSum()).floatValue();
         addAttribute(builder, Attributes.ARMOR, incArmor, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.ARMOR_TOUGHNESS)) {
         double armorToughness = data.get(ModGearAttributes.ARMOR_TOUGHNESS, VaultGearAttributeTypeMerger.intSum()).intValue();
         addAttribute(builder, Attributes.ARMOR_TOUGHNESS, armorToughness, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.KNOCKBACK_RESISTANCE)) {
         float knockbackResistance = data.get(ModGearAttributes.KNOCKBACK_RESISTANCE, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, Attributes.KNOCKBACK_RESISTANCE, knockbackResistance, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.HEALTH)) {
         float health = data.get(ModGearAttributes.HEALTH, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, Attributes.MAX_HEALTH, health, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.HEALTH_PERCENTILE)) {
         float healthPercent = data.get(ModGearAttributes.HEALTH_PERCENTILE, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, Attributes.MAX_HEALTH, healthPercent, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.REACH)) {
         double reach = data.get(ModGearAttributes.REACH, VaultGearAttributeTypeMerger.doubleSum());
         addAttribute(builder, (Attribute)ForgeMod.REACH_DISTANCE.get(), reach, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.ATTACK_RANGE)) {
         double reach = data.get(ModGearAttributes.ATTACK_RANGE, VaultGearAttributeTypeMerger.doubleSum());
         addAttribute(builder, (Attribute)ForgeMod.ATTACK_RANGE.get(), reach, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.MANA_REGEN_ADDITIVE_PERCENTILE)) {
         float manaRegen = data.get(ModGearAttributes.MANA_REGEN_ADDITIVE_PERCENTILE, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, ModAttributes.MANA_REGEN, manaRegen, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.MANA_ADDITIVE)) {
         int manaFlat = data.get(ModGearAttributes.MANA_ADDITIVE, VaultGearAttributeTypeMerger.intSum());
         addAttribute(builder, ModAttributes.MANA_MAX, manaFlat, identifier);
      }

      if (data.hasAttribute(ModGearAttributes.MANA_ADDITIVE_PERCENTILE)) {
         float manaPercent = data.get(ModGearAttributes.MANA_ADDITIVE_PERCENTILE, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, ModAttributes.MANA_MAX, manaPercent, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.HEALING_EFFECTIVENESS)) {
         float healingPercent = data.get(ModGearAttributes.HEALING_EFFECTIVENESS, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, ModAttributes.HEALING_MAX, healingPercent, identifier, Operation.MULTIPLY_BASE);
      }

      if (data.hasAttribute(ModGearAttributes.MOVEMENT_SPEED)) {
         float movementSpeed = data.get(ModGearAttributes.MOVEMENT_SPEED, VaultGearAttributeTypeMerger.floatSum());
         addAttribute(builder, Attributes.MOVEMENT_SPEED, movementSpeed, identifier, Operation.MULTIPLY_BASE);
      }

      return builder.build();
   }

   private static void addAttribute(Builder<Attribute, AttributeModifier> builder, Attribute attribute, double value, UUID seed) {
      addAttribute(builder, attribute, value, seed, Operation.ADDITION);
   }

   private static void addAttribute(Builder<Attribute, AttributeModifier> builder, Attribute attribute, double value, UUID seed, Operation operation) {
      builder.put(
         attribute, new AttributeModifier(seededId(seed, attribute, operation), "VaultGear %s".formatted(attribute.getDescriptionId()), value, operation)
      );
   }

   private static UUID seededId(UUID seed, Attribute attribute, Operation operation) {
      long attrHash = hash(attribute.getRegistryName().toString());
      attrHash ^= hash(operation.name());
      return new UUID(seed.getMostSignificantBits() ^ attrHash, seed.getLeastSignificantBits() ^ attrHash);
   }

   private static long hash(String str) {
      long hash = 1125899906842597L;
      int length = str.length();

      for (int i = 0; i < length; i++) {
         hash = 31L * hash + str.charAt(i);
      }

      return hash;
   }
}
