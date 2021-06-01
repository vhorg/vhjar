package iskallia.vault.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.attribute.DoubleAttribute;
import iskallia.vault.item.gear.attribute.EnumAttribute;
import iskallia.vault.item.gear.attribute.FloatAttribute;
import iskallia.vault.item.gear.attribute.IntegerAttribute;
import iskallia.vault.skill.set.PlayerSet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeColor;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeItem;

public interface VaultGear<T extends Item> extends IForgeItem {
   UUID[] ARMOR_MODIFIERS = new UUID[]{
      UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
      UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
      UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
      UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
   };
   int ROLL_TIME = 120;
   int ENTRIES_PER_ROLL = 50;
   DyeColor[] BASE_COLORS = new DyeColor[]{
      DyeColor.BLUE,
      DyeColor.BROWN,
      DyeColor.CYAN,
      DyeColor.GREEN,
      DyeColor.LIGHT_BLUE,
      DyeColor.LIME,
      DyeColor.MAGENTA,
      DyeColor.ORANGE,
      DyeColor.PINK,
      DyeColor.PURPLE,
      DyeColor.RED,
      DyeColor.WHITE,
      DyeColor.YELLOW
   };

   int getModelsFor(VaultGear.Rarity var1);

   default boolean isDamageable(T item, ItemStack stack) {
      return ModAttributes.DURABILITY.exists(stack);
   }

   default int getMaxDamage(T item, ItemStack stack, int maxDamage) {
      return ModAttributes.DURABILITY.getOrDefault(stack, maxDamage).getValue(stack);
   }

   default ITextComponent getDisplayName(T item, ItemStack stack, ITextComponent name) {
      if (ModAttributes.GEAR_STATE.getOrDefault(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.IDENTIFIED) {
         if (item == ModItems.ETCHING) {
            return name;
         } else {
            VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, VaultGear.Rarity.COMMON).getValue(stack);
            return ((IFormattableTextComponent)name).func_240699_a_(rarity.color);
         }
      } else {
         TextComponent prefix = new StringTextComponent("Unidentified ");
         return prefix.func_230530_a_(name.func_150256_b()).func_230529_a_(name);
      }
   }

   default boolean canApply(ItemStack stack, Enchantment enchantment) {
      return !(enchantment instanceof MendingEnchantment);
   }

   default ActionResult<ItemStack> onItemRightClick(T item, World world, PlayerEntity player, Hand hand, ActionResult<ItemStack> result) {
      ItemStack stack = player.func_184586_b(hand);
      if (world.field_72995_K) {
         if (stack.func_77973_b() == ModItems.DAGGER && hand == Hand.OFF_HAND) {
            ((VaultDaggerItem)stack.func_77973_b()).attackOffHand();
            return ActionResult.func_226248_a_(stack);
         } else {
            return result;
         }
      } else {
         Optional<EnumAttribute<VaultGear.State>> attribute = ModAttributes.GEAR_STATE.get(stack);
         if (attribute.isPresent() && attribute.get().getValue(stack) == VaultGear.State.UNIDENTIFIED) {
            attribute.get().setBaseValue(VaultGear.State.ROLLING);
            return ActionResult.func_226251_d_(stack);
         } else {
            return result;
         }
      }
   }

   default void inventoryTick(T item, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.field_72995_K) {
         if (ModAttributes.GEAR_STATE.getOrCreate(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.ROLLING) {
            this.tickRoll(item, stack, world, entity, itemSlot, isSelected);
         }

         if (!ModAttributes.GEAR_ROLL_TYPE.exists(stack)) {
            ModAttributes.GEAR_ROLL_TYPE.create(stack, VaultGear.RollType.ALL);
         }

         update(stack, world.func_201674_k());
      }
   }

   default void tickRoll(T item, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      int rollTicks = stack.func_196082_o().func_74762_e("RollTicks");
      int lastModelHit = stack.func_196082_o().func_74762_e("LastModelHit");
      double displacement = this.getDisplacement(rollTicks);
      if (rollTicks >= 120) {
         initialize(stack, world.func_201674_k());
         ModAttributes.GEAR_STATE.create(stack, VaultGear.State.IDENTIFIED);
         stack.func_196082_o().func_82580_o("RollTicks");
         stack.func_196082_o().func_82580_o("LastModelHit");
         world.func_184133_a(null, entity.func_233580_cy_(), ModSounds.CONFETTI_SFX, SoundCategory.PLAYERS, 0.5F, 1.0F);
      } else {
         if ((int)displacement != lastModelHit) {
            VaultGear.Rarity rarity = ModAttributes.GEAR_ROLL_TYPE.getOrCreate(stack, VaultGear.RollType.ALL).getValue(stack).get(world.field_73012_v);
            ModAttributes.GEAR_RARITY.create(stack, rarity);
            ModAttributes.GEAR_MODEL.create(stack, world.field_73012_v.nextInt(this.getModelsFor(rarity)));
            ModAttributes.GEAR_COLOR.create(stack, randomBaseColor(world.func_201674_k()));
            if (item == ModItems.ETCHING) {
               VaultGear.Set set = VaultGear.Set.values()[world.field_73012_v.nextInt(VaultGear.Set.values().length)];
               ModAttributes.GEAR_SET.create(stack, set);
            }

            stack.func_196082_o().func_74768_a("LastModelHit", (int)displacement);
            world.func_184133_a(null, entity.func_233580_cy_(), ModSounds.RAFFLE_SFX, SoundCategory.PLAYERS, 1.2F, 1.0F);
         }

         stack.func_196082_o().func_74768_a("RollTicks", rollTicks + 1);
      }
   }

   default double getDisplacement(int tick) {
      double c = 7200.0;
      return (-tick * tick * tick / 6.0 + c * tick) * 50.0 / (-288000.0 + c * 120.0);
   }

   default void addInformation(T item, ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      ModAttributes.GEAR_STATE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            state -> {
               if (state != VaultGear.State.IDENTIFIED) {
                  ModAttributes.GEAR_ROLL_TYPE
                     .get(stack)
                     .map(attribute -> attribute.getValue(stack))
                     .ifPresent(
                        roll -> tooltip.add(
                           new StringTextComponent("Roll: ").func_230529_a_(new StringTextComponent(roll.name()).func_240699_a_(TextFormatting.GREEN))
                        )
                     );
               }
            }
         );
      ModAttributes.GEAR_RARITY.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(rarity -> {
         if (item != ModItems.ETCHING) {
            tooltip.add(new StringTextComponent("Rarity: ").func_230529_a_(new StringTextComponent(rarity.name()).func_240699_a_(rarity.color)));
         }
      });
      ModAttributes.GEAR_SET.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
         tooltip.add(new StringTextComponent(""));
         tooltip.add(new StringTextComponent("Etching: ").func_230529_a_(new StringTextComponent(value.name()).func_240699_a_(TextFormatting.RED)));
         if (item == ModItems.ETCHING) {
            tooltip.add(new StringTextComponent(""));

            for (TextComponent descriptionLine : value.getLore()) {
               tooltip.add(descriptionLine.func_240701_a_(new TextFormatting[]{TextFormatting.ITALIC, TextFormatting.GRAY}));
            }

            tooltip.add(new StringTextComponent(""));

            for (TextComponent descriptionLine : value.getDescription()) {
               tooltip.add(descriptionLine.func_240699_a_(TextFormatting.GRAY));
            }
         }
      });
      ModAttributes.MAX_REPAIRS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> {
               int current = ModAttributes.CURRENT_REPAIRS.getOrDefault(stack, 0).getValue(stack);
               int unfilled = value - current;
               tooltip.add(
                  new StringTextComponent("Repairs: ")
                     .func_230529_a_(tooltipDots(current, TextFormatting.YELLOW))
                     .func_230529_a_(tooltipDots(unfilled, TextFormatting.GRAY))
               );
            }
         );
      ModAttributes.GEAR_MAX_LEVEL
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> {
               int current = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack).intValue();
               int unfilled = value - current;
               tooltip.add(
                  new StringTextComponent("Level: ")
                     .func_230529_a_(tooltipDots(current, TextFormatting.YELLOW))
                     .func_230529_a_(tooltipDots(unfilled, TextFormatting.GRAY))
               );
            }
         );
      ModAttributes.ADD_ARMOR
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value, 5) + " Armor").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.ADD_ARMOR_TOUGHNESS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value, 5) + " Armor Toughness").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.ADD_KNOCKBACK_RESISTANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value, 5) + " Knockback Resistance").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.ADD_ATTACK_DAMAGE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value, 5) + " Attack Damage").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.ADD_ATTACK_SPEED
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value, 5) + " Attack Speed").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.ADD_DURABILITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + value + " Durability").func_240699_a_(TextFormatting.DARK_GRAY)));
      ModAttributes.EXTRA_LEECH_RATIO
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value * 100.0F, 5) + "% Leech").func_240699_a_(TextFormatting.RED)));
      ModAttributes.EXTRA_PARRY_CHANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value * 100.0F, 5) + "% Parry").func_240699_a_(TextFormatting.RED)));
      ModAttributes.EXTRA_HEALTH
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(value -> tooltip.add(new StringTextComponent("+" + format(value.floatValue(), 5) + " Health").func_240699_a_(TextFormatting.RED)));
      ModAttributes.EXTRA_EFFECTS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> value.forEach(
               effect -> tooltip.add(
                  new StringTextComponent("+" + effect.getAmplifier() + " ")
                     .func_230529_a_(new TranslationTextComponent(effect.getEffect().func_76393_a()))
                     .func_240699_a_(TextFormatting.GREEN)
               )
            )
         );
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.func_82781_a(stack);
      if (enchantments.size() > 0) {
         tooltip.add(new StringTextComponent(""));
      }

      ModAttributes.MIN_VAULT_LEVEL
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("Requires level: ").func_230529_a_(new StringTextComponent(value + "").func_240699_a_(TextFormatting.YELLOW))
            )
         );
   }

   static String format(double value, int scale) {
      return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
   }

   default boolean canElytraFly(T item, ItemStack stack, LivingEntity entity) {
      return entity instanceof PlayerEntity ? PlayerSet.isActive(VaultGear.Set.DRAGON, (PlayerEntity)entity) : false;
   }

   default boolean elytraFlightTick(T item, ItemStack stack, LivingEntity entity, int flightTicks) {
      return this.canElytraFly(item, stack, entity);
   }

   default int getColor(T item, ItemStack stack) {
      EnumAttribute<VaultGear.State> stateAttribute = ModAttributes.GEAR_STATE.get(stack).orElse(null);
      if (stateAttribute != null && stateAttribute.getValue(stack) != VaultGear.State.UNIDENTIFIED) {
         VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, VaultGear.Rarity.SCRAPPY).getValue(stack);
         Integer dyeColor = getDyeColor(stack);
         if (rarity == VaultGear.Rarity.SCRAPPY && dyeColor == null) {
            return -1;
         } else {
            IntegerAttribute colorAttribute = ModAttributes.GEAR_COLOR.get(stack).orElse(null);
            int baseColor = colorAttribute == null ? -1 : colorAttribute.getValue(stack);
            return dyeColor != null ? dyeColor : baseColor;
         }
      } else {
         return -1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   default <A extends BipedModel<?>> A getArmorModel(T item, LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
      Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, VaultGear.Rarity.SCRAPPY).getValue(itemStack);
      if (rarity == VaultGear.Rarity.SCRAPPY) {
         return null;
      } else {
         ModModels.GearModel gearModel = ModModels.GearModel.REGISTRY.get(modelId);
         return (A)(gearModel == null ? null : gearModel.forSlotType(armorSlot));
      }
   }

   @OnlyIn(Dist.CLIENT)
   default String getArmorTexture(T item, ItemStack itemStack, Entity entity, EquipmentSlotType slot, String type) {
      Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, VaultGear.Rarity.SCRAPPY).getValue(itemStack);
      if (rarity == VaultGear.Rarity.SCRAPPY) {
         return ModModels.GearModel.SCRAPPY.getTextureName(slot, type);
      } else {
         ModModels.GearModel gearModel = ModModels.GearModel.REGISTRY.get(modelId);
         return gearModel == null ? null : gearModel.getTextureName(slot, type);
      }
   }

   default Multimap<Attribute, AttributeModifier> getAttributeModifiers(
      T item, EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> parent
   ) {
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      Optional<DoubleAttribute> attackDamage = ModAttributes.ATTACK_DAMAGE.get(stack);
      Optional<DoubleAttribute> attackSpeed = ModAttributes.ATTACK_SPEED.get(stack);
      Optional<DoubleAttribute> armor = ModAttributes.ARMOR.get(stack);
      Optional<DoubleAttribute> armorToughness = ModAttributes.ARMOR_TOUGHNESS.get(stack);
      Optional<DoubleAttribute> knockbackResistance = ModAttributes.KNOCKBACK_RESISTANCE.get(stack);
      Optional<FloatAttribute> extraHealth = ModAttributes.EXTRA_HEALTH.get(stack);
      parent.forEach(
         (attribute, modifier) -> {
            if (attribute == Attributes.field_233823_f_ && attackDamage.isPresent()) {
               builder.put(
                  Attributes.field_233823_f_,
                  new AttributeModifier(
                     UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "Weapon modifier", attackDamage.get().getValue(stack), Operation.ADDITION
                  )
               );
            } else if (attribute == Attributes.field_233825_h_ && attackSpeed.isPresent()) {
               builder.put(
                  Attributes.field_233825_h_,
                  new AttributeModifier(
                     UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "Weapon modifier", attackSpeed.get().getValue(stack), Operation.ADDITION
                  )
               );
            } else if (attribute == Attributes.field_233826_i_ && armor.isPresent()) {
               builder.put(
                  Attributes.field_233826_i_,
                  new AttributeModifier(ARMOR_MODIFIERS[slot.func_188454_b()], "Armor modifier", armor.get().getValue(stack), Operation.ADDITION)
               );
            } else if (attribute == Attributes.field_233827_j_ && armorToughness.isPresent()) {
               builder.put(
                  Attributes.field_233827_j_,
                  new AttributeModifier(ARMOR_MODIFIERS[slot.func_188454_b()], "Armor toughness", armorToughness.get().getValue(stack), Operation.ADDITION)
               );
            } else if (attribute == Attributes.field_233820_c_ && knockbackResistance.isPresent()) {
               builder.put(
                  Attributes.field_233820_c_,
                  new AttributeModifier(
                     ARMOR_MODIFIERS[slot.func_188454_b()], "Armor knockback resistance", knockbackResistance.get().getValue(stack), Operation.ADDITION
                  )
               );
            } else {
               builder.put(attribute, modifier);
            }
         }
      );
      if ((item == ModItems.SWORD || item == ModItems.AXE) && slot == EquipmentSlotType.MAINHAND
         || item == ModItems.DAGGER && (slot == EquipmentSlotType.MAINHAND || slot == EquipmentSlotType.OFFHAND)
         || item instanceof VaultArmorItem && item.getEquipmentSlot(stack) == slot) {
         extraHealth.ifPresent(
            floatAttribute -> builder.put(
               Attributes.field_233818_a_,
               new AttributeModifier(
                  new UUID(1234L, item.getRegistryName().toString().hashCode()),
                  "Extra Health",
                  floatAttribute.getValue(stack).floatValue(),
                  Operation.ADDITION
               )
            )
         );
      }

      return builder.build();
   }

   static void addLevel(ItemStack stack, float amount) {
      if (stack.func_77973_b() instanceof VaultGear) {
         int maxLevel = ModAttributes.GEAR_MAX_LEVEL.getOrDefault(stack, 0).getValue(stack);
         float current = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack);
         if ((int)current < maxLevel) {
            float newLevel = current + amount;
            int difference = (int)newLevel - (int)current;
            ModAttributes.GEAR_LEVEL.create(stack, newLevel);
            int toRoll = ModAttributes.GEAR_MODIFIERS_TO_ROLL.getOrDefault(stack, 0).getValue(stack) + difference;
            if (toRoll != 0) {
               ModAttributes.GEAR_MODIFIERS_TO_ROLL.create(stack, toRoll);
            }
         }
      }
   }

   static void initialize(ItemStack stack, Random random) {
      ModAttributes.GEAR_RARITY.get(stack).ifPresent(attribute -> VaultGearConfig.get(attribute.getValue(stack)).initializeAttributes(stack, random));
   }

   static void update(ItemStack stack, Random random) {
      ModAttributes.GEAR_RARITY.get(stack).ifPresent(attribute -> VaultGearConfig.get(attribute.getValue(stack)).initializeModifiers(stack, random));
   }

   static int randomBaseColor(Random rand) {
      return BASE_COLORS[rand.nextInt(BASE_COLORS.length)].getColorValue();
   }

   static Integer getDyeColor(ItemStack stack) {
      CompoundNBT compoundnbt = stack.func_179543_a("display");
      return compoundnbt != null && compoundnbt.func_150297_b("color", 3) ? compoundnbt.func_74762_e("color") : null;
   }

   static ITextComponent tooltipDots(int amount, TextFormatting formatting) {
      StringBuilder text = new StringBuilder();

      for (int i = 0; i < amount; i++) {
         text.append("⬢ ");
      }

      return new StringTextComponent(text.toString()).func_240699_a_(formatting);
   }

   public static class Material implements IArmorMaterial {
      public static final VaultGear.Material INSTANCE = new VaultGear.Material();

      private Material() {
      }

      public int func_200896_a(EquipmentSlotType slot) {
         return 0;
      }

      public int func_200902_b(EquipmentSlotType slot) {
         return 0;
      }

      public int func_200900_a() {
         return ArmorMaterial.DIAMOND.func_200900_a();
      }

      public SoundEvent func_200899_b() {
         return ArmorMaterial.DIAMOND.func_200899_b();
      }

      public Ingredient func_200898_c() {
         return Ingredient.field_193370_a;
      }

      public String func_200897_d() {
         return "vault_dummy";
      }

      public float func_200901_e() {
         return 0.0F;
      }

      public float func_230304_f_() {
         return 1.0E-4F;
      }
   }

   public static enum Rarity {
      COMMON(TextFormatting.AQUA),
      RARE(TextFormatting.YELLOW),
      EPIC(TextFormatting.LIGHT_PURPLE),
      OMEGA(TextFormatting.GREEN),
      SCRAPPY(TextFormatting.GRAY);

      public final TextFormatting color;

      private Rarity(TextFormatting color) {
         this.color = color;
      }
   }

   public static enum RollType {
      SCRAPPY_ONLY,
      TREASURE_ONLY,
      ALL;

      public VaultGear.Rarity get(Random rand) {
         return ModConfigs.VAULT_GEAR.ROLLS.get(this.name()).getRandom(rand);
      }
   }

   public static enum Set {
      NONE("", ""),
      PHOENIX(
         "Reborn from the ashes!",
         "Next time you take a lethal damage in the Vaults, become invulnerable for 3 seconds and get fully healed. (Can be triggered only once per Vault instance)"
      ),
      GOBLIN("Hoard all the way!", "Grants better loot chance (+1 Luck)"),
      GOLEM("Steady as rock!", "Grants +8% resistance"),
      ASSASSIN("Fast as wind!", "Increases speed and grants +10% dodge chance"),
      SLAYER("Slay them all!", "Grants +2 Strength"),
      RIFT("Become one with the Vault Rifts!", "Reduce all ability cooldowns by 50%"),
      DRAGON("Breath of the ender!", "Gain elytra and gliding powers without an elytra item"),
      BRUTE("Angry as the Piglins!", "Grants +1 Strength"),
      TITAN("Sturdy as a titan!", "Grants +14% resistance"),
      DRYAD("Touch of the nature!", "Grants +2 Regeneration"),
      VAMPIRE("Smell the blood!", "Grants 5% life leech"),
      NINJA("Can't hit me!", "Grants +20% parry chance"),
      TREASURE_HUNTER("Leave no chest behind!", "Grants better loot chance (+3 Luck)");

      String lore;
      String description;

      private Set(String lore, String description) {
         this.lore = lore;
         this.description = description;
      }

      public List<TextComponent> getDescription() {
         return this.getTooltip(this.description);
      }

      public List<TextComponent> getLore() {
         return this.getTooltip(this.lore);
      }

      private List<TextComponent> getTooltip(String text) {
         LinkedList<TextComponent> tooltip = new LinkedList<>();
         StringBuilder sb = new StringBuilder();

         for (String word : text.split("\\s+")) {
            sb.append(word + " ");
            if (sb.length() >= 30) {
               tooltip.add(new StringTextComponent(sb.toString().trim()));
               sb = new StringBuilder();
            }
         }

         if (sb.length() > 0) {
            tooltip.add(new StringTextComponent(sb.toString().trim()));
         }

         return tooltip;
      }
   }

   public static enum State {
      UNIDENTIFIED,
      ROLLING,
      IDENTIFIED;
   }

   public static class Tier implements IItemTier {
      public static final VaultGear.Tier INSTANCE = new VaultGear.Tier();

      private Tier() {
      }

      public int func_200926_a() {
         return 0;
      }

      public float func_200928_b() {
         return 0.0F;
      }

      public float func_200929_c() {
         return 0.0F;
      }

      public int func_200925_d() {
         return ItemTier.DIAMOND.func_200925_d();
      }

      public int func_200927_e() {
         return ItemTier.DIAMOND.func_200927_e();
      }

      public Ingredient func_200924_f() {
         return Ingredient.field_193370_a;
      }
   }

   public static enum Type {
      SWORD,
      AXE,
      ARMOR;
   }
}
