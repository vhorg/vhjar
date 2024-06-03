package iskallia.vault.gear;

import iskallia.vault.config.UniqueGearConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.LegendaryExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GearRollHelper {
   public static final Random rand = new Random();
   private static final int ROLL_TIME = 120;
   private static final int ENTRIES_PER_ROLL = 50;

   public static void tickGearRoll(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearItem item = VaultGearItem.of(stack);
      VaultGearRarity rarity = data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE)
         .flatMap(rollTypeStr -> ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(rollTypeStr))
         .orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll())
         .getRandom(rand);
      data.setRarity(rarity);
      data.write(stack);
      ResourceLocation modelKey = item.getRandomModel(stack, rand);
      if (modelKey != null) {
         data.updateAttribute(ModGearAttributes.GEAR_MODEL, modelKey);
      }

      data.write(stack);
   }

   public static void initializeAndDiscoverGear(ItemStack stack, Player player) {
      initializeGear(stack, player);
      if (player instanceof ServerPlayer sPlayer && VaultGearData.read(stack).getRarity() != VaultGearRarity.UNIQUE) {
         DiscoveredModelsData worldData = DiscoveredModelsData.get(sPlayer.getLevel().getServer());
         worldData.discoverModelAndBroadcast(stack, sPlayer);
      }
   }

   public static void initializeGear(ItemStack stack) {
      initializeGear(stack, null);
   }

   public static void initializeGear(ItemStack stack, Player player) {
      VaultGearData data = VaultGearData.read(stack);
      data.setState(VaultGearState.IDENTIFIED);
      data.write(stack);
      VaultGearModifierHelper.reRollRepairSlots(stack, rand);
      if (data.getRarity() == VaultGearRarity.UNIQUE) {
         JavaRandom random = JavaRandom.ofNanoTime();
         UniqueGearConfig.Entry entry = ModConfigs.UNIQUE_GEAR.getRandomEntry(stack, data.getItemLevel(), random).orElseThrow();
         data.updateAttribute(ModGearAttributes.GEAR_MODEL, entry.getModel());
         data.write(stack);
         VaultGearTierConfig.getConfig(stack).ifPresent(config -> {
            entry.getModifierIdentifiers().forEach((affix, identifiers) -> {
               for (ResourceLocation id : identifiers) {
                  VaultGearTierConfig.ModifierTierGroup group = config.getTierGroup(id);
                  generateModifiers(data, random, affix, group);
               }
            });
            entry.getModifierTags().forEach((affix, tags) -> {
               for (String tag : tags) {
                  for (VaultGearTierConfig.ModifierTierGroup group : config.getTierGroups(tag)) {
                     generateModifiers(data, random, affix, group);
                  }
               }
            });
         });
         data.write(stack);
         stack.setHoverName(new TextComponent(entry.getName()));
      } else {
         VaultGearCraftingHelper.reRollCraftingPotential(stack);
         VaultGearModifierHelper.generateAffixSlots(stack, rand);
         VaultGearModifierHelper.generateImplicits(stack, rand);
         VaultGearModifierHelper.generateModifiers(stack, rand);
         float extraLegendaryChance = 0.0F;
         if (player instanceof ServerPlayer && player.level instanceof ServerLevel sLevel) {
            ExpertiseTree expertises = PlayerExpertisesData.get(sLevel).getExpertises(player);

            for (LegendaryExpertise expertise : expertises.getAll(LegendaryExpertise.class, Skill::isUnlocked)) {
               extraLegendaryChance += expertise.getExtraLegendaryChance();
            }
         }

         if (data.getFirstValue(ModGearAttributes.IS_LOOT).orElse(false)
            && rand.nextFloat() < ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getLegendaryModifierChance() + extraLegendaryChance) {
            VaultGearModifierHelper.generateLegendaryModifier(stack, rand);
         }
      }
   }

   private static void generateModifiers(VaultGearData data, JavaRandom random, VaultGearModifier.AffixType affix, VaultGearTierConfig.ModifierTierGroup group) {
      if (group != null) {
         WeightedList<VaultGearTierConfig.ModifierTier<?>> tiers = new WeightedList<>();

         for (VaultGearTierConfig.ModifierTier<?> tier : group.getModifiersForLevel(data.getItemLevel())) {
            tiers.add(tier, tier.getWeight());
         }

         tiers.getRandom(random).ifPresent(tierx -> data.addModifier(affix, tierx.makeModifier(group, random.asRandomView())));
      }
   }

   public static void tickToll(ItemStack stack, Player player, BiConsumer<ItemStack, Player> onRollTick, Consumer<ItemStack> onFinish) {
      Level world = player.getLevel();
      CompoundTag rollTag = stack.getOrCreateTagElement("RollHelper");
      int ticks = rollTag.getInt("RollTicks");
      int lastHit = rollTag.getInt("LastHit");
      double displacement = getDisplacement(ticks);
      if (ticks >= 120) {
         onFinish.accept(stack);
         stack.removeTagKey("RollHelper");
         world.playSound(null, player.blockPosition(), ModSounds.IDENTIFICATION_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
      } else {
         if ((int)displacement != lastHit || ticks == 0) {
            onRollTick.accept(stack, player);
            rollTag.putInt("LastHit", (int)displacement);
            world.playSound(null, player.blockPosition(), ModSounds.RAFFLE_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
         }

         rollTag.putInt("RollTicks", ticks + 1);
      }
   }

   private static double getDisplacement(int tick) {
      double c = 7200.0;
      return (-tick * tick * tick / 6.0 + c * tick) * 50.0 / (-288000.0 + c * 120.0);
   }
}
