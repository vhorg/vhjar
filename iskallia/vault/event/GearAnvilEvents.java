package iskallia.vault.event;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ArtisanScrollItem;
import iskallia.vault.item.FlawedRubyItem;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.VaultGearHelper;
import iskallia.vault.item.gear.applicable.VaultPlateItem;
import iskallia.vault.item.gear.applicable.VaultRepairCoreItem;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.SideOnlyFixer;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GearAnvilEvents {
   @SubscribeEvent
   public static void onApplyT2Charm(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      if (left.func_77973_b() != ModItems.ETCHING) {
         if (left.func_77973_b() instanceof VaultGear && event.getRight().func_77973_b() == ModItems.GEAR_CHARM) {
            if (ModAttributes.GEAR_STATE.getOrDefault(left, VaultGear.State.UNIDENTIFIED).getValue(left) != VaultGear.State.UNIDENTIFIED) {
               return;
            }

            if (!ModAttributes.GEAR_ROLL_TYPE.exists(left)) {
               return;
            }

            if (ModAttributes.GEAR_TIER.getOrDefault(left, 0).getValue(left) >= 1) {
               return;
            }

            PlayerEntity player = MiscUtils.findPlayerUsingAnvil(left, event.getRight());
            if (player == null) {
               return;
            }

            int vaultLevel = SidedHelper.getVaultLevel(player);
            if (vaultLevel < 100) {
               return;
            }

            String pool = ModAttributes.GEAR_ROLL_TYPE.get(left).map(attribute -> attribute.getValue(left)).get();
            String upgraded = ModConfigs.VAULT_GEAR_UPGRADE.getUpgradedRarity(pool);
            ItemStack output = left.func_77946_l();
            ModAttributes.GEAR_TIER.create(output, 1);
            ModAttributes.GEAR_ROLL_TYPE.create(output, upgraded);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(20);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyT3Charm(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      if (left.func_77973_b() != ModItems.ETCHING) {
         if (left.func_77973_b() instanceof VaultGear && event.getRight().func_77973_b() == ModItems.GEAR_CHARM_T3) {
            if (ModAttributes.GEAR_STATE.getOrDefault(left, VaultGear.State.UNIDENTIFIED).getValue(left) != VaultGear.State.UNIDENTIFIED) {
               return;
            }

            if (!ModAttributes.GEAR_ROLL_TYPE.exists(left)) {
               return;
            }

            if (ModAttributes.GEAR_TIER.getOrDefault(left, 0).getValue(left) != 1) {
               return;
            }

            PlayerEntity player = MiscUtils.findPlayerUsingAnvil(left, event.getRight());
            if (player == null) {
               return;
            }

            int vaultLevel = SidedHelper.getVaultLevel(player);
            if (vaultLevel < 200) {
               return;
            }

            String pool = ModAttributes.GEAR_ROLL_TYPE.get(left).map(attribute -> attribute.getValue(left)).get();
            String upgraded = ModConfigs.VAULT_GEAR_UPGRADE.getUpgradedRarity(pool);
            ItemStack output = left.func_77946_l();
            ModAttributes.GEAR_TIER.create(output, 2);
            ModAttributes.GEAR_ROLL_TYPE.create(output, upgraded);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(20);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyBanishedSoul(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof IdolItem
         && event.getRight().func_77973_b() == ModItems.BANISHED_SOUL
         && event.getLeft().func_77952_i() == 0) {
         ItemStack output = event.getLeft().func_77946_l();
         if (ModAttributes.IDOL_AUGMENTED.exists(output) || !ModAttributes.GEAR_RANDOM_SEED.exists(output)) {
            return;
         }

         ModAttributes.IDOL_AUGMENTED.create(output, true);
         event.setOutput(output);
         event.setMaterialCost(1);
         event.setCost(15);
      }
   }

   @SubscribeEvent
   public static void onBreakBanishedSoul(AnvilRepairEvent event) {
      ItemStack result = event.getItemResult();
      ItemStack original;
      if (result.func_190926_b()) {
         result.func_190920_e(1);
         if (!(result.func_77973_b() instanceof IdolItem)) {
            return;
         }

         int originalSlot = SideOnlyFixer.getSlotFor(event.getPlayer().field_71071_by, result);
         if (originalSlot == -1) {
            return;
         }

         original = event.getPlayer().field_71071_by.func_70301_a(originalSlot);
      } else {
         original = event.getItemResult();
      }

      if (original.func_77973_b() instanceof IdolItem && event.getIngredientInput().func_77973_b() == ModItems.BANISHED_SOUL) {
         long seed = ModAttributes.GEAR_RANDOM_SEED.getBase(original).orElse(0L);
         Random r = new Random(seed);
         event.setBreakChance(1.0F);
         if (r.nextFloat() <= 0.33333334F) {
            original.func_190920_e(0);
            event.getPlayer().func_130014_f_().func_217379_c(1029, event.getPlayer().func_233580_cy_(), 0);
         } else {
            ModAttributes.DURABILITY.getBase(original).ifPresent(value -> ModAttributes.DURABILITY.create(original, value + 3000));
         }
      }
   }

   @SubscribeEvent
   public static void onApplyEtching(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultArmorItem && event.getRight().func_77973_b() == ModItems.ETCHING) {
         ItemStack output = event.getLeft().func_77946_l();
         if (ModAttributes.GEAR_SET.exists(output) && ModAttributes.GEAR_SET.getBase(output).orElse(VaultGear.Set.NONE) != VaultGear.Set.NONE) {
            return;
         }

         VaultGear.Set set = ModAttributes.GEAR_SET.getOrDefault(event.getRight(), VaultGear.Set.NONE).getValue(event.getRight());
         ModAttributes.GEAR_SET.create(output, set);
         event.setOutput(output);
         event.setMaterialCost(1);
         event.setCost(25);
      }
   }

   @SubscribeEvent
   public static void onApplyRepairCore(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() instanceof VaultRepairCoreItem) {
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            ItemStack repairCore = event.getRight();
            int repairLevel = ((VaultRepairCoreItem)repairCore.func_77973_b()).getVaultGearTier();
            if (ModAttributes.GEAR_TIER.getOrDefault(output, 0).getValue(output) != repairLevel) {
               return;
            }

            int maxRepairs = ModAttributes.MAX_REPAIRS.getOrDefault(output, -1).getValue(output);
            int curRepairs = ModAttributes.CURRENT_REPAIRS.getOrDefault(output, 0).getValue(output);
            if (maxRepairs == -1 || curRepairs >= maxRepairs) {
               return;
            }

            ModAttributes.CURRENT_REPAIRS.create(output, curRepairs + 1);
            ModAttributes.DURABILITY.getBase(output).ifPresent(value -> ModAttributes.DURABILITY.create(output, (int)(value.intValue() * 0.75F)));
            output.func_196085_b(0);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyPlating(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() instanceof VaultPlateItem) {
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            ItemStack plate = event.getRight();
            int plateLevel = ((VaultPlateItem)plate.func_77973_b()).getVaultGearTier();
            if (ModAttributes.GEAR_TIER.getOrDefault(output, 0).getValue(output) != plateLevel) {
               return;
            }

            int level = ModAttributes.ADD_PLATING.getOrDefault(output, 0).getValue(output);
            int decrement = Math.min(20 - level, event.getRight().func_190916_E());
            ModAttributes.ADD_PLATING.create(output, level + decrement);
            event.setOutput(output);
            event.setMaterialCost(decrement);
            event.setCost(decrement);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyWutaxShard(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.WUTAX_SHARD) {
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            ModAttributes.MIN_VAULT_LEVEL.getValue(output).ifPresent(level -> {
               int tier = ModAttributes.GEAR_TIER.get(event.getLeft()).map(attribute -> attribute.getValue(event.getLeft())).orElse(0);
               int tierMinLevel = ModConfigs.VAULT_GEAR.getTierConfig(tier).getMinLevel();
               int maxAllowedDecrement = Math.max(0, level - tierMinLevel);
               int decrement = Math.min(maxAllowedDecrement, event.getRight().func_190916_E());
               ModAttributes.MIN_VAULT_LEVEL.create(output, level - decrement);
               event.setOutput(output);
               event.setMaterialCost(decrement);
               event.setCost(decrement);
            });
         }
      }
   }

   @SubscribeEvent
   public static void onApplyWutaxCrystal(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.WUTAX_CRYSTAL) {
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            float level = ModAttributes.GEAR_LEVEL.getOrDefault(output, 0.0F).getValue(output);
            int max = ModAttributes.GEAR_MAX_LEVEL.getOrDefault(output, 0).getValue(output);
            int increment = Math.min(max - (int)level, event.getRight().func_190916_E());
            VaultGear.addLevel(output, increment);
            event.setOutput(output);
            event.setMaterialCost(increment);
            event.setCost(increment);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyVoidOrb(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.VOID_ORB) {
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            int maxRepairs = ModAttributes.MAX_REPAIRS.getOrDefault(output, -1).getValue(output);
            int curRepairs = ModAttributes.CURRENT_REPAIRS.getOrDefault(output, 0).getValue(output);
            float level = ModAttributes.GEAR_LEVEL.getOrDefault(output, 0.0F).getValue(output);
            if (maxRepairs == -1 || curRepairs >= maxRepairs || level == 0.0F) {
               return;
            }

            int rolls = ModAttributes.GEAR_MODIFIERS_TO_ROLL.getOrDefault(output, 0).getValue(output);
            if (rolls != 0) {
               return;
            }

            if (!VaultGearHelper.hasModifier(output) || !VaultGearHelper.hasUsedLevels(output)) {
               return;
            }

            ModAttributes.GEAR_MODIFIERS_TO_ROLL.create(output, -1);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyArtisanScroll(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.ARTISAN_SCROLL) {
         if (!event.getLeft().func_77951_h()) {
            if (event.getLeft().func_77973_b() instanceof VaultGear) {
               VaultGear<?> gearItem = (VaultGear<?>)event.getLeft().func_77973_b();
               PlayerEntity playerEntity = event.getPlayer();
               if (playerEntity != null) {
                  World world = playerEntity.func_130014_f_();
                  if (world instanceof ServerWorld) {
                     ItemStack output = event.getLeft().func_77946_l();
                     VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY
                        .get(output)
                        .map(attributex -> (VaultGear.Rarity)attributex.getValue(output))
                        .orElse(null);
                     if (rarity != null) {
                        int tier = ModAttributes.GEAR_TIER.getOrDefault(output, 0).getValue(output);
                        Pair<EquipmentSlotType, VAttribute<?, ?>> gearModifier = ArtisanScrollItem.getPredefinedRoll(event.getRight());
                        if (!ModAttributes.REFORGED.getOrDefault(output, false).getValue(output)) {
                           if (ModAttributes.GEAR_STATE.getOrDefault(output, VaultGear.State.UNIDENTIFIED).getValue(output) != VaultGear.State.UNIDENTIFIED) {
                              if (gearModifier == null || VaultGearHelper.canRollModifier(output, rarity, tier, (VAttribute<?, ?>)gearModifier.getSecond())) {
                                 VaultGearHelper.removeAllAttributes(output);
                                 ModAttributes.GEAR_STATE.create(output, VaultGear.State.UNIDENTIFIED);
                                 ModAttributes.REFORGED.create(output, true);
                                 if (gearModifier != null) {
                                    EquipmentSlotType slotType = (EquipmentSlotType)gearModifier.getFirst();
                                    VAttribute<?, ?> attribute = (VAttribute<?, ?>)gearModifier.getSecond();
                                    if (!gearItem.isIntendedForSlot(slotType)) {
                                       return;
                                    }

                                    ModAttributes.GUARANTEED_MODIFIER.create(output, attribute.getId().toString());
                                 }

                                 event.setCost(5);
                                 event.setMaterialCost(1);
                                 event.setOutput(output);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCreateArtisanScroll(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.FABRICATION_JEWEL) {
         if (!event.getLeft().func_77951_h()) {
            if (event.getLeft().func_77973_b() instanceof VaultGear) {
               ItemStack input = event.getLeft();
               PlayerEntity playerEntity = event.getPlayer();
               if (hasLearnedArtisan(playerEntity)) {
                  if (ModAttributes.GEAR_RANDOM_SEED.exists(input)) {
                     if (ModAttributes.GEAR_STATE.getOrDefault(input, VaultGear.State.UNIDENTIFIED).getValue(input) != VaultGear.State.UNIDENTIFIED) {
                        if (VaultGearHelper.hasModifier(input)) {
                           event.setCost(5);
                           event.setMaterialCost(1);
                           event.setOutput(new ItemStack(ModItems.ARTISAN_SCROLL));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCreateArtisanScroll(AnvilRepairEvent event) {
      ItemStack input = event.getItemInput();
      ItemStack result = event.getItemResult();
      if (!input.func_77951_h()) {
         if (input.func_77973_b() instanceof VaultGear) {
            if (result.func_77973_b() == ModItems.ARTISAN_SCROLL) {
               if (VaultGearHelper.hasModifier(input)) {
                  EquipmentSlotType slotType = ((VaultGear)input.func_77973_b()).getIntendedSlot();
                  if (slotType != null) {
                     if (ModAttributes.GEAR_RANDOM_SEED.exists(input)) {
                        long seed = ModAttributes.GEAR_RANDOM_SEED.getBase(input).orElse(0L);
                        Random rand = new Random(seed);
                        VAttribute<?, ?> randomModifier = VaultGearHelper.getRandomModifier(input, rand);
                        if (randomModifier != null && rand.nextFloat() < ModConfigs.VAULT_GEAR_UTILITIES.getFabricationJewelKeepModifierChance()) {
                           ArtisanScrollItem.setPredefinedRoll(result, slotType, randomModifier);
                        }

                        ArtisanScrollItem.setInitialized(result, true);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyArtisanPearl(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.FLAWED_RUBY) {
         PlayerEntity playerEntity = event.getPlayer();
         if (hasLearnedArtisan(playerEntity) || hasLearnedTreasureHunter(playerEntity)) {
            if (ModAttributes.IMBUED.getOrDefault(event.getLeft(), false).getValue(event.getLeft())) {
               return;
            }

            if (event.getLeft().func_77973_b() instanceof VaultGear) {
               ItemStack output = event.getLeft().func_77946_l();
               FlawedRubyItem.markApplied(output);
               event.setOutput(output);
               event.setMaterialCost(1);
               event.setCost(1);
            }
         }
      }
   }

   private static boolean hasLearnedArtisan(PlayerEntity player) {
      if (player == null) {
         return false;
      } else {
         World world = player.func_130014_f_();
         if (!(world instanceof ServerWorld)) {
            return false;
         } else {
            ServerWorld serverWorld = (ServerWorld)world;
            TalentTree talents = PlayerTalentsData.get(serverWorld).getTalents(player);
            return talents.hasLearnedNode(ModConfigs.TALENTS.ARTISAN);
         }
      }
   }

   private static boolean hasLearnedTreasureHunter(PlayerEntity player) {
      if (player == null) {
         return false;
      } else {
         World world = player.func_130014_f_();
         if (!(world instanceof ServerWorld)) {
            return false;
         } else {
            ServerWorld serverWorld = (ServerWorld)world;
            TalentTree talents = PlayerTalentsData.get(serverWorld).getTalents(player);
            return talents.hasLearnedNode(ModConfigs.TALENTS.TREASURE_HUNTER);
         }
      }
   }
}
