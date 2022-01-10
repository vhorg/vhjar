package iskallia.vault.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import iskallia.vault.Vault;
import iskallia.vault.attribute.DoubleAttribute;
import iskallia.vault.attribute.EnumAttribute;
import iskallia.vault.attribute.FloatAttribute;
import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.config.VaultGearScalingConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.FlawedRubyItem;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.ArtisanTalent;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.calc.CooldownHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeItem;

public interface VaultGear<T extends Item & VaultGear<? extends Item>> extends IForgeItem {
   DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.##");
   int MAX_EXPECTED_TIER = 3;
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

   @Nullable
   EquipmentSlotType getIntendedSlot();

   default boolean isIntendedForSlot(EquipmentSlotType slotType) {
      return this.getIntendedSlot() == slotType;
   }

   default boolean isDamageable(T item, ItemStack stack) {
      return ModAttributes.DURABILITY.exists(stack);
   }

   default int getMaxDamage(T item, ItemStack stack, int maxDamage) {
      return ModAttributes.DURABILITY.getOrDefault(stack, maxDamage).getValue(stack);
   }

   default ITextComponent getDisplayName(T item, ItemStack stack, ITextComponent name) {
      String customName = ModAttributes.GEAR_NAME.getOrDefault(stack, "").getValue(stack);
      if (!customName.isEmpty()) {
         VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, VaultGear.Rarity.COMMON).getValue(stack);
         Style style = name.func_150256_b().func_240718_a_(rarity.getColor());
         return new StringTextComponent(customName).func_230530_a_(style);
      } else if (ModAttributes.GEAR_STATE.getOrDefault(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.IDENTIFIED) {
         if (item == ModItems.ETCHING) {
            return name;
         } else {
            VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, VaultGear.Rarity.COMMON).getValue(stack);
            Style style = name.func_150256_b().func_240718_a_(rarity.getColor());
            return ((IFormattableTextComponent)name).func_230530_a_(style);
         }
      } else {
         TextComponent prefix = new StringTextComponent("Unidentified ");
         return prefix.func_230530_a_(name.func_150256_b()).func_230529_a_(name);
      }
   }

   default boolean canApply(ItemStack stack, Enchantment enchantment) {
      return !(enchantment instanceof MendingEnchantment) && !(enchantment instanceof ThornsEnchantment);
   }

   default ActionResult<ItemStack> onItemRightClick(T item, World world, PlayerEntity player, Hand hand, ActionResult<ItemStack> result) {
      ItemStack stack = player.func_184586_b(hand);
      if (world.func_201670_d()) {
         return result;
      } else {
         if (world.func_234923_W_() != Vault.VAULT_KEY) {
            Optional<EnumAttribute<VaultGear.State>> attribute = ModAttributes.GEAR_STATE.get(stack);
            if (attribute.isPresent() && attribute.get().getValue(stack) == VaultGear.State.UNIDENTIFIED) {
               attribute.get().setBaseValue(VaultGear.State.ROLLING);
               return ActionResult.func_226251_d_(stack);
            }
         }

         return result;
      }
   }

   default void fillItemGroup(NonNullList<ItemStack> items) {
      for (int tier = 0; tier < 3; tier++) {
         ItemStack stack = new ItemStack((IItemProvider)this);
         ModAttributes.GEAR_TIER.create(stack, tier);
         items.add(stack);
      }
   }

   default void splitStack(T item, ItemStack stack, World world, Entity entity) {
      if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         if (stack.func_190916_E() > 1) {
            while (stack.func_190916_E() > 1) {
               stack.func_190918_g(1);
               ItemStack gearPiece = stack.func_77946_l();
               gearPiece.func_190920_e(1);
               MiscUtils.giveItem(player, gearPiece);
            }
         }
      }
   }

   default void inventoryTick(T item, ItemStack stack, World world, ServerPlayerEntity player, int itemSlot, boolean isSelected) {
      if (!ModAttributes.GEAR_RANDOM_SEED.exists(stack)) {
         ModAttributes.GEAR_RANDOM_SEED.create(stack, world.field_73012_v.nextLong());
      }

      if (ModAttributes.GEAR_STATE.getOrCreate(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.ROLLING) {
         this.tickRoll(item, stack, world, player, itemSlot, isSelected);
      }

      if (!ModAttributes.GEAR_ROLL_TYPE.exists(stack) && ModAttributes.GEAR_ROLL_POOL.exists(stack)) {
         ModAttributes.GEAR_ROLL_POOL.getBase(stack).ifPresent(pool -> {
            int playerLevel = PlayerVaultStatsData.get(player.func_71121_q()).getVaultStats(player.func_110124_au()).getVaultLevel();
            VaultGearScalingConfig.GearRarityOutcome outcome = ModConfigs.VAULT_GEAR_SCALING.getGearRollType(pool, playerLevel);
            if (outcome != null) {
               ModAttributes.GEAR_TIER.create(stack, outcome.getTier());
               ModAttributes.GEAR_ROLL_TYPE.create(stack, outcome.getRarity());
            }
         });
      }

      if (!ModAttributes.GEAR_ROLL_TYPE.exists(stack)) {
         ModAttributes.GEAR_ROLL_TYPE.create(stack, this.getDefaultRoll(player).getName());
      }

      if (!ModAttributes.GEAR_TIER.exists(stack)) {
         ModAttributes.GEAR_TIER.create(stack, this.getDefaultGearTier(player));
      }

      update(stack, world.func_201674_k());
      FlawedRubyItem.handleOutcome(player, stack);
   }

   default void tickRoll(T item, ItemStack stack, World world, ServerPlayerEntity player, int itemSlot, boolean isSelected) {
      int rollTicks = stack.func_196082_o().func_74762_e("RollTicks");
      int lastModelHit = stack.func_196082_o().func_74762_e("LastModelHit");
      double displacement = getDisplacement(rollTicks);
      if (player.func_184586_b(Hand.OFF_HAND).func_77973_b() == ModItems.IDENTIFICATION_TOME) {
         String roll = ModAttributes.GEAR_ROLL_TYPE.getOrCreate(stack, this.getDefaultRoll(player).getName()).getValue(stack);
         VaultGear.Rarity rarity = ModConfigs.VAULT_GEAR.getRoll(roll).orElse(this.getDefaultRoll(player)).getRandom(world.func_201674_k());
         ModAttributes.GEAR_RARITY.create(stack, rarity);
         ModAttributes.GEAR_MODEL.create(stack, world.field_73012_v.nextInt(this.getModelsFor(rarity)));
         ModAttributes.GEAR_COLOR.create(stack, item instanceof VaultArmorItem ? -1 : randomBaseColor(world.field_73012_v));
         if (item == ModItems.ETCHING) {
            VaultGear.Set set = VaultGear.Set.values()[world.field_73012_v.nextInt(VaultGear.Set.values().length)];
            ModAttributes.GEAR_SET.create(stack, set);
         }

         initialize(stack, world.func_201674_k());
         ModAttributes.GEAR_STATE.create(stack, VaultGear.State.IDENTIFIED);
         stack.func_196082_o().func_82580_o("RollTicks");
         stack.func_196082_o().func_82580_o("LastModelHit");
         world.func_184133_a(null, player.func_233580_cy_(), ModSounds.CONFETTI_SFX, SoundCategory.PLAYERS, 0.3F, 1.0F);
      } else if (rollTicks >= 120) {
         initialize(stack, world.func_201674_k());
         ModAttributes.GEAR_STATE.create(stack, VaultGear.State.IDENTIFIED);
         stack.func_196082_o().func_82580_o("RollTicks");
         stack.func_196082_o().func_82580_o("LastModelHit");
         world.func_184133_a(null, player.func_233580_cy_(), ModSounds.CONFETTI_SFX, SoundCategory.PLAYERS, 0.5F, 1.0F);
      } else {
         if ((int)displacement != lastModelHit) {
            String roll = ModAttributes.GEAR_ROLL_TYPE.getOrCreate(stack, this.getDefaultRoll(player).getName()).getValue(stack);
            VaultGear.Rarity rarity = ModConfigs.VAULT_GEAR.getRoll(roll).orElse(this.getDefaultRoll(player)).getRandom(world.func_201674_k());
            ModAttributes.GEAR_RARITY.create(stack, rarity);
            ModAttributes.GEAR_MODEL.create(stack, world.field_73012_v.nextInt(this.getModelsFor(rarity)));
            ModAttributes.GEAR_COLOR.create(stack, item instanceof VaultArmorItem ? -1 : randomBaseColor(world.field_73012_v));
            if (item == ModItems.ETCHING) {
               VaultGear.Set set = VaultGear.Set.values()[world.field_73012_v.nextInt(VaultGear.Set.values().length)];
               ModAttributes.GEAR_SET.create(stack, set);
            }

            stack.func_196082_o().func_74768_a("LastModelHit", (int)displacement);
            world.func_184133_a(null, player.func_233580_cy_(), ModSounds.RAFFLE_SFX, SoundCategory.PLAYERS, 0.4F, 1.0F);
         }

         stack.func_196082_o().func_74768_a("RollTicks", rollTicks + 1);
      }
   }

   default VaultGearConfig.General.Roll getDefaultRoll(ServerPlayerEntity player) {
      TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);
      TalentNode<?> artisanNode = talents.getNodeOf(ModConfigs.TALENTS.ARTISAN);
      VaultGearConfig.General.Roll defaultRoll = ModConfigs.VAULT_GEAR.getDefaultRoll();
      if (artisanNode.isLearned() && artisanNode.getTalent() instanceof ArtisanTalent) {
         defaultRoll = ModConfigs.VAULT_GEAR.getRoll(((ArtisanTalent)artisanNode.getTalent()).getDefaultRoll()).orElse(defaultRoll);
      }

      return defaultRoll;
   }

   default int getDefaultGearTier(ServerPlayerEntity sPlayer) {
      int vaultLevel = PlayerVaultStatsData.get(sPlayer.func_71121_q()).getVaultStats(sPlayer).getVaultLevel();
      return ModConfigs.VAULT_GEAR_CRAFTING_SCALING.getRandomTier(vaultLevel);
   }

   static double getDisplacement(int tick) {
      double c = 7200.0;
      return (-tick * tick * tick / 6.0 + c * tick) * 50.0 / (-288000.0 + c * 120.0);
   }

   @OnlyIn(Dist.CLIENT)
   default void addInformation(T item, ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      ModAttributes.GEAR_CRAFTED_BY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            crafter -> {
               if (!crafter.isEmpty()) {
                  tooltip.add(
                     new StringTextComponent("Crafted by: ")
                        .func_230529_a_(new StringTextComponent(crafter).func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16770048))))
                  );
               }
            }
         );
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
                        rollName -> {
                           Optional<VaultGearConfig.General.Roll> roll = ModConfigs.VAULT_GEAR.getRoll(rollName);
                           if (roll.isPresent()) {
                              tooltip.add(
                                 new StringTextComponent("Roll: ")
                                    .func_230529_a_(
                                       new StringTextComponent(roll.get().getName())
                                          .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(roll.get().getColor())))
                                    )
                              );
                           }
                        }
                     );
               }
            }
         );
      ModAttributes.GEAR_STATE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(state -> ModAttributes.GEAR_TIER.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(tierId -> {
            if (item != ModItems.ETCHING) {
               if (ModConfigs.VAULT_GEAR != null) {
                  ITextComponent displayTxt = ModConfigs.VAULT_GEAR.getTierConfig(tierId).getDisplay();
                  if (!displayTxt.getString().isEmpty()) {
                     tooltip.add(new StringTextComponent("Tier: ").func_230529_a_(displayTxt));
                  }
               }
            }
         }));
      ModAttributes.IDOL_TYPE.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
         if (item instanceof IdolItem) {
            tooltip.add(((IdolItem)item).getType().getIdolDescription());
         }
      });
      ModAttributes.GEAR_RARITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            rarityx -> {
               if (item != ModItems.ETCHING) {
                  IFormattableTextComponent rarityText = new StringTextComponent("Rarity: ").func_230529_a_(rarityx.getName());
                  if (Screen.func_231173_s_()) {
                     ModAttributes.GEAR_MODEL
                        .get(stack)
                        .map(attribute -> attribute.getValue(stack))
                        .ifPresent(
                           modelx -> rarityText.func_230529_a_(new StringTextComponent(" | ").func_240699_a_(TextFormatting.BLACK))
                              .func_230529_a_(new StringTextComponent("" + modelx).func_240699_a_(TextFormatting.GRAY))
                        );
                  }

                  tooltip.add(rarityText);
               }
            }
         );
      if (item instanceof VaultArmorItem) {
         EquipmentSlotType equipmentSlot = ((VaultArmorItem)item).getEquipmentSlot(stack);
         VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(stack, VaultGear.Rarity.SCRAPPY).getValue(stack);
         Integer gearModel = ModAttributes.GEAR_MODEL.getOrDefault(stack, -1).getValue(stack);
         Integer gearSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getOrDefault(stack, -1).getValue(stack);
         if (gearSpecialModel != -1 && gearSpecialModel == ModModels.SpecialGearModel.FAIRY_SET.modelForSlot(equipmentSlot).getId()) {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Required in \"Grasshopper Ninja\" advancement").func_240699_a_(TextFormatting.GREEN));
         }

         if (rarity == VaultGear.Rarity.UNIQUE && gearSpecialModel != -1 && item.getIntendedSlot() != null) {
            ModModels.SpecialGearModel model = ModModels.SpecialGearModel.getModel(item.getIntendedSlot(), gearSpecialModel);
            if (model != null) {
               tooltip.add(new StringTextComponent(model.getDisplayName() + " Armor Set"));
            }
         }
      }

      ModAttributes.GEAR_SET.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(value -> {
         if (value != VaultGear.Set.NONE) {
            tooltip.add(StringTextComponent.field_240750_d_);
            tooltip.add(new StringTextComponent("Etching: ").func_230529_a_(new StringTextComponent(value.name()).func_240699_a_(TextFormatting.RED)));
         }
      });
      ModAttributes.MAX_REPAIRS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> {
               if (value > 0) {
                  int current = ModAttributes.CURRENT_REPAIRS.getOrDefault(stack, 0).getValue(stack);
                  int unfilled = value - current;
                  tooltip.add(
                     new StringTextComponent("Repairs: ")
                        .func_230529_a_(tooltipDots(current, TextFormatting.YELLOW))
                        .func_230529_a_(tooltipDots(unfilled, TextFormatting.GRAY))
                  );
               }
            }
         );
      ModAttributes.GEAR_MAX_LEVEL
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> {
               if (value > 0) {
                  int current = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack).intValue();
                  int unfilled = value - current;
                  tooltip.add(
                     new StringTextComponent("Levels: ")
                        .func_230529_a_(tooltipDots(current, TextFormatting.YELLOW))
                        .func_230529_a_(tooltipDots(unfilled, TextFormatting.GRAY))
                  );
               }
            }
         );
      if (ModAttributes.GEAR_STATE.getOrDefault(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.IDENTIFIED) {
         this.addModifierInformation(stack, tooltip, flag);
      }

      ModAttributes.REFORGED.get(stack).map(attribute -> attribute.getValue(stack)).filter(b -> b).ifPresent(value -> {
         tooltip.add(new StringTextComponent("Reforged").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14833698))));
         if (Screen.func_231173_s_()) {
            tooltip.add(new StringTextComponent(" Has been reforged with Artisan Scroll").func_240699_a_(TextFormatting.DARK_GRAY));
         }
      });
      ModAttributes.IMBUED.get(stack).map(attribute -> attribute.getValue(stack)).filter(b -> b).ifPresent(value -> {
         tooltip.add(new StringTextComponent("Imbued").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16772263))));
         if (Screen.func_231173_s_()) {
            tooltip.add(new StringTextComponent(" Has been imbued with a Flawed Ruby").func_240699_a_(TextFormatting.DARK_GRAY));
         }
      });
      ModAttributes.IDOL_AUGMENTED.get(stack).map(attribute -> attribute.getValue(stack)).filter(b -> b).ifPresent(value -> {
         tooltip.add(new StringTextComponent("Hallowed").func_240700_a_(style -> style.func_240718_a_(Color.func_240743_a_(16746496))));
         if (Screen.func_231173_s_()) {
            tooltip.add(new StringTextComponent(" Adds +3000 Durability").func_240699_a_(TextFormatting.DARK_GRAY));
         }
      });
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.func_82781_a(stack);
      if (enchantments.size() > 0) {
         tooltip.add(new StringTextComponent(""));
      }

      ModAttributes.MIN_VAULT_LEVEL
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("Requires level: ")
                  .func_230529_a_(new StringTextComponent(value + "").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16770048))))
            )
         );
      if (FlawedRubyItem.shouldHandleOutcome(stack)) {
         tooltip.add(new StringTextComponent("Flawed Ruby Applied").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16772263))));
         if (Screen.func_231173_s_()) {
            tooltip.add(new StringTextComponent(" A Flawed Ruby has been applied").func_240699_a_(TextFormatting.DARK_GRAY));
            tooltip.add(new StringTextComponent(" and is unstable. This may break").func_240699_a_(TextFormatting.DARK_GRAY));
            tooltip.add(new StringTextComponent(" or become imbued and gain an").func_240699_a_(TextFormatting.DARK_GRAY));
            tooltip.add(new StringTextComponent(" additional modifier slot.").func_240699_a_(TextFormatting.DARK_GRAY));
            tooltip.add(new StringTextComponent(" Also a small chance nothing will happen.").func_240699_a_(TextFormatting.DARK_GRAY));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   default void addModifierInformation(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flag) {
      ModAttributes.ADD_ARMOR
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Armor").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4766456)))
            )
         );
      ModAttributes.ADD_ARMOR_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Armor").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4766456)))
            )
         );
      ModAttributes.ADD_ARMOR_TOUGHNESS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Armor Toughness")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(13302672)))
            )
         );
      ModAttributes.ADD_ARMOR_TOUGHNESS_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Armor Toughness")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(13302672)))
            )
         );
      ModAttributes.THORNS_CHANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Thorns Chance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(7195648)))
            )
         );
      ModAttributes.THORNS_DAMAGE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Thorns Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(3646976)))
            )
         );
      ModAttributes.ADD_KNOCKBACK_RESISTANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0) + "% Knockback Resistance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16756751)))
            )
         );
      ModAttributes.ADD_KNOCKBACK_RESISTANCE_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0) + "% Knockback Resistance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16756751)))
            )
         );
      ModAttributes.ADD_ATTACK_DAMAGE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Attack Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(13116966)))
            )
         );
      ModAttributes.ADD_ATTACK_DAMAGE_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Attack Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(13116966)))
            )
         );
      ModAttributes.ADD_ATTACK_SPEED
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Attack Speed")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16767592)))
            )
         );
      ModAttributes.ADD_ATTACK_SPEED_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value, 5) + " Attack Speed")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16767592)))
            )
         );
      ModAttributes.DAMAGE_INCREASE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value * 100.0F, 5) + "% increased Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16739072)))
            )
         );
      ModAttributes.DAMAGE_INCREASE_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value * 100.0F, 5) + "% increased Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16739072)))
            )
         );
      ModAttributes.ON_HIT_CHAIN
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Chaining Attacks").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(6119096)))
            )
         );
      ModAttributes.ON_HIT_AOE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Attack AoE").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12085504)))
            )
         );
      ModAttributes.ON_HIT_STUN
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Stun Attack Chance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(1681124)))
            )
         );
      ModAttributes.DAMAGE_ILLAGERS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Spiteful")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(40882)))
            )
         );
      ModAttributes.DAMAGE_SPIDERS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Baneful")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(8281694)))
            )
         );
      ModAttributes.DAMAGE_UNDEAD
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Holy")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16382128)))
            )
         );
      ModAttributes.ADD_DURABILITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Durability").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14668030)))
            )
         );
      ModAttributes.ADD_DURABILITY_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Durability").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14668030)))
            )
         );
      ModAttributes.ADD_PLATING
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Plating").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14668030)))
            )
         );
      ModAttributes.ADD_REACH
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Reach").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(8706047)))
            )
         );
      ModAttributes.ADD_REACH_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + value + " Reach").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(8706047)))
            )
         );
      ModAttributes.ADD_FEATHER_FEET
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value * 100.0F, 5) + "% Feather Feet")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(13499899)))
            )
         );
      ModAttributes.ADD_MIN_VAULT_LEVEL
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent((value < 0 ? "-" : "+") + Math.abs(value) + " Min Vault Level")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(15523772)))
            )
         );
      ModAttributes.ADD_COOLDOWN_REDUCTION
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent((value < 0.0F ? "-" : "+") + format(Math.abs(value) * 100.0F, 5) + "% Cooldown Reduction")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(63668)))
            )
         );
      ModAttributes.ADD_COOLDOWN_REDUCTION_2
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent((value < 0.0F ? "-" : "+") + format(Math.abs(value) * 100.0F, 5) + "% Cooldown Reduction")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(63668)))
            )
         );
      ModAttributes.EXTRA_LEECH_RATIO
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value * 100.0F, 5) + "% Leech")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16716820)))
            )
         );
      ModAttributes.ADD_EXTRA_LEECH_RATIO
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value * 100.0F, 5) + "% Leech")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16716820)))
            )
         );
      ModAttributes.FATAL_STRIKE_CHANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Fatal Strike Chance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16523264)))
            )
         );
      ModAttributes.FATAL_STRIKE_DAMAGE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Fatal Strike Damage")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12520704)))
            )
         );
      ModAttributes.EXTRA_HEALTH
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value.floatValue(), 5) + " Health")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(2293541)))
            )
         );
      ModAttributes.ADD_EXTRA_HEALTH
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + format(value.floatValue(), 5) + " Health")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(2293541)))
            )
         );
      ModAttributes.EXTRA_PARRY_CHANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Parry")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(11534098)))
            )
         );
      ModAttributes.ADD_EXTRA_PARRY_CHANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Parry")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(11534098)))
            )
         );
      ModAttributes.EXTRA_RESISTANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Resistance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16702720)))
            )
         );
      ModAttributes.ADD_EXTRA_RESISTANCE
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Resistance")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16702720)))
            )
         );
      ModAttributes.CHEST_RARITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> tooltip.add(
               new StringTextComponent("+" + formatPercent(value * 100.0F) + "% Chest Rarity")
                  .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(11073085)))
            )
         );
      ModAttributes.EFFECT_IMMUNITY
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> value.forEach(
               effect -> tooltip.add(
                  new StringTextComponent("+")
                     .func_230529_a_(new TranslationTextComponent(effect.toEffect().func_76393_a()))
                     .func_230529_a_(new StringTextComponent(" Immunity"))
                     .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(10801083)))
               )
            )
         );
      ModAttributes.EFFECT_CLOUD
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> value.forEach(
               effect -> tooltip.add(
                  new StringTextComponent("+" + effect.getName() + " Cloud")
                     .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(15007916)))
               )
            )
         );
      ModAttributes.EXTRA_EFFECTS
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> value.forEach(
               effect -> tooltip.add(
                  new StringTextComponent("+" + effect.getAmplifier() + " ")
                     .func_230529_a_(new TranslationTextComponent(effect.getEffect().func_76393_a()))
                     .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14111487)))
               )
            )
         );
      ModAttributes.SOULBOUND.get(stack).map(attribute -> attribute.getValue(stack)).filter(b -> b).ifPresent(value -> {
         tooltip.add(new StringTextComponent("Soulbound").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9856253))));
         if (Screen.func_231173_s_()) {
            tooltip.add(new StringTextComponent(" Keep item on death in vault").func_240699_a_(TextFormatting.DARK_GRAY));
         }
      });
   }

   static String formatPercent(double value) {
      return PERCENT_FORMAT.format(value);
   }

   static String format(double value, int scale) {
      return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
   }

   default boolean canElytraFly(T item, ItemStack stack, LivingEntity entity) {
      return entity instanceof PlayerEntity ? PlayerSet.isActive(VaultGear.Set.DRAGON, entity) : false;
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
      Integer specialModelId = ModAttributes.GEAR_SPECIAL_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      if (specialModelId != -1) {
         ModModels.SpecialGearModel specialGearModel = ModModels.SpecialGearModel.getRegistryForSlot(armorSlot).get(specialModelId);
         if (specialGearModel != null) {
            return (A)specialGearModel.getModel();
         }
      }

      Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, VaultGear.Rarity.SCRAPPY).getValue(itemStack);
      ModModels.GearModel gearModel = rarity == VaultGear.Rarity.SCRAPPY
         ? ModModels.GearModel.SCRAPPY_REGISTRY.get(modelId)
         : ModModels.GearModel.REGISTRY.get(modelId);
      return (A)(gearModel == null ? null : gearModel.forSlotType(armorSlot));
   }

   @OnlyIn(Dist.CLIENT)
   default String getArmorTexture(T item, ItemStack itemStack, Entity entity, EquipmentSlotType slot, String type) {
      Integer specialModelId = ModAttributes.GEAR_SPECIAL_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      if (specialModelId != -1) {
         ModModels.SpecialGearModel specialGearModel = ModModels.SpecialGearModel.getRegistryForSlot(slot).get(specialModelId);
         if (specialGearModel != null) {
            return specialGearModel.getTextureName(slot, type);
         }
      }

      Integer modelId = ModAttributes.GEAR_MODEL.getOrDefault(itemStack, -1).getValue(itemStack);
      VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, VaultGear.Rarity.SCRAPPY).getValue(itemStack);
      ModModels.GearModel gearModel = rarity == VaultGear.Rarity.SCRAPPY
         ? ModModels.GearModel.SCRAPPY_REGISTRY.get(modelId)
         : ModModels.GearModel.REGISTRY.get(modelId);
      return gearModel == null ? null : gearModel.getTextureName(slot, type);
   }

   default Multimap<Attribute, AttributeModifier> getAttributeModifiers(
      T item, EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> parent
   ) {
      if (!item.isIntendedForSlot(slot)) {
         return parent;
      } else {
         Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
         Optional<DoubleAttribute> attackDamage = ModAttributes.ATTACK_DAMAGE.get(stack);
         Optional<DoubleAttribute> attackSpeed = ModAttributes.ATTACK_SPEED.get(stack);
         Optional<DoubleAttribute> armor = ModAttributes.ARMOR.get(stack);
         Optional<DoubleAttribute> armorToughness = ModAttributes.ARMOR_TOUGHNESS.get(stack);
         Optional<DoubleAttribute> knockbackResistance = ModAttributes.KNOCKBACK_RESISTANCE.get(stack);
         Optional<FloatAttribute> extraHealth = ModAttributes.EXTRA_HEALTH.get(stack);
         Optional<FloatAttribute> extraHealth2 = ModAttributes.ADD_EXTRA_HEALTH.get(stack);
         Optional<DoubleAttribute> reach = ModAttributes.REACH.get(stack);
         parent.forEach(
            (attribute, modifier) -> {
               if (attribute == Attributes.field_233823_f_ && attackDamage.isPresent()) {
                  builder.put(
                     Attributes.field_233823_f_,
                     new AttributeModifier(modifier.func_111167_a(), "Weapon modifier", attackDamage.get().getValue(stack), Operation.ADDITION)
                  );
               } else if (attribute == Attributes.field_233825_h_ && attackSpeed.isPresent()) {
                  builder.put(
                     Attributes.field_233825_h_,
                     new AttributeModifier(modifier.func_111167_a(), "Weapon modifier", attackSpeed.get().getValue(stack), Operation.ADDITION)
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
         float health = extraHealth.<Float>map(attribute -> attribute.getValue(stack)).orElse(0.0F)
            + extraHealth2.<Float>map(attribute -> attribute.getValue(stack)).orElse(0.0F);
         if (health != 0.0F) {
            builder.put(Attributes.field_233818_a_, new AttributeModifier(itemHash(item, 0L), "Extra Health", health, Operation.ADDITION));
         }

         reach.ifPresent(
            attribute -> builder.put(
               ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(itemHash(item, 1L), "Reach", attribute.getValue(stack), Operation.ADDITION)
            )
         );
         return builder.build();
      }
   }

   static int getCooldownReduction(ServerPlayerEntity player, AbilityGroup<?, ?> abilityGroup, int cooldown) {
      float totalCooldown = MathHelper.func_76131_a(CooldownHelper.getCooldownMultiplier(player, abilityGroup), 0.0F, 1.0F);
      if (PlayerSet.isActive(VaultGear.Set.RIFT, player)) {
         cooldown /= 2;
      }

      return Math.round(cooldown * (1.0F - totalCooldown));
   }

   static UUID itemHash(Item item, long salt) {
      return new UUID(salt, item.hashCode());
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

   static void decrementLevel(ItemStack stack, int removed) {
      float currentLevel = ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack);
      ModAttributes.GEAR_LEVEL.create(stack, Math.max(currentLevel - removed, 0.0F));
   }

   static void incrementRepairs(ItemStack stack) {
      int curRepairs = ModAttributes.CURRENT_REPAIRS.getOrDefault(stack, 0).getValue(stack);
      ModAttributes.CURRENT_REPAIRS.create(stack, curRepairs + 1);
   }

   static void initialize(ItemStack stack, Random random) {
      ModAttributes.GEAR_RARITY.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(rarity -> {
         int tier = ModAttributes.GEAR_TIER.getOrDefault(stack, 0).getValue(stack);
         VaultGearConfig.Tier tierConfig = VaultGearConfig.get(rarity).TIERS.get(tier);
         tierConfig.getAttributes(stack).ifPresent(modifiers -> modifiers.initialize(stack, random));
      });
   }

   static void update(ItemStack stack, Random random) {
      ModAttributes.GEAR_RARITY.get(stack).map(attribute -> attribute.getValue(stack)).ifPresent(rarity -> {
         int tier = ModAttributes.GEAR_TIER.getOrDefault(stack, 0).getValue(stack);
         VaultGearConfig.Tier tierConfig = VaultGearConfig.get(rarity).TIERS.get(tier);
         tierConfig.getModifiers(stack).ifPresent(modifiers -> {
            if (ModAttributes.GEAR_STATE.getOrCreate(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.IDENTIFIED) {
               ModAttributes.GUARANTEED_MODIFIER.getBase(stack).ifPresent(modifierKey -> {
                  VAttribute<?, ?> modifier = ModAttributes.REGISTRY.get(new ResourceLocation(modifierKey));
                  if (modifier != null) {
                     VaultGearHelper.applyGearModifier(stack, tierConfig, modifier);
                  }

                  VaultGearHelper.removeAttribute(stack, ModAttributes.GUARANTEED_MODIFIER);
               });
            }

            modifiers.initialize(stack, random);
         });
      });
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
      UNIQUE(Color.func_240743_a_(-1213660)),
      SCRAPPY(TextFormatting.GRAY);

      private final Color color;

      private Rarity(TextFormatting color) {
         this(Color.func_240744_a_(color));
      }

      private Rarity(Color color) {
         this.color = color;
      }

      public Color getColor() {
         return this.color;
      }

      public ITextComponent getName() {
         Style style = Style.field_240709_b_.func_240718_a_(this.getColor());
         return new StringTextComponent(this.name()).func_240703_c_(style);
      }
   }

   public static enum Set {
      NONE,
      DRAGON,
      ZOD,
      NINJA,
      GOLEM,
      RIFT,
      CARAPACE,
      DIVINITY,
      DRYAD,
      BLOOD,
      VAMPIRE,
      TREASURE,
      ASSASSIN,
      PHOENIX,
      DREAM,
      PORCUPINE;
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
