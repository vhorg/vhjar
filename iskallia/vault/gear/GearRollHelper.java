package iskallia.vault.gear;

import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GearRollHelper {
   private static final Random rand = new Random();
   private static final int ROLL_TIME = 120;
   private static final int ENTRIES_PER_ROLL = 50;

   public static void tickRollVaultGear(ItemStack stack, ServerPlayer player) {
      if (VaultGearData.read(stack).getState() == VaultGearState.ROLLING) {
         tickToll(
            stack,
            player,
            inStack -> {
               VaultGearData data = VaultGearData.read(inStack);
               VaultGearItem item = VaultGearItem.of(inStack);
               VaultGearRarity rarity = data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE)
                  .flatMap(rollTypeStr -> ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(rollTypeStr))
                  .orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll())
                  .getRandom(rand);
               data.setRarity(rarity);
               data.write(inStack);
               ResourceLocation modelKey = item.getRandomModel(inStack, rand);
               if (modelKey != null) {
                  data.updateAttribute(ModGearAttributes.GEAR_MODEL, modelKey);
               }

               data.write(inStack);
            },
            inStack -> {
               initializeGear(inStack);
               DiscoveredModelsData worldData = DiscoveredModelsData.get(player.getLevel().getServer());
               worldData.discoverModelAndBroadcast(stack, player);
            }
         );
      }
   }

   public static void initializeGear(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      data.setState(VaultGearState.IDENTIFIED);
      data.write(stack);
      VaultGearModifierHelper.reRollRepairSlots(stack, rand);
      VaultGearCraftingHelper.reRollCraftingPotential(stack);
      VaultGearModifierHelper.generateAffixSlots(stack, rand);
      VaultGearModifierHelper.generateImplicits(stack, rand);
      VaultGearModifierHelper.generateModifiers(stack, rand);
      if (data.getFirstValue(ModGearAttributes.IS_LOOT).orElse(false) && rand.nextFloat() < ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getLegendaryModifierChance()) {
         VaultGearModifierHelper.generateLegendaryModifier(stack, rand);
      }
   }

   public static void tickToll(ItemStack stack, ServerPlayer player, Consumer<ItemStack> onRollTick, Consumer<ItemStack> onFinish) {
      Level world = player.getLevel();
      CompoundTag rollTag = stack.getOrCreateTagElement("RollHelper");
      int ticks = rollTag.getInt("RollTicks");
      int lastHit = rollTag.getInt("LastHit");
      double displacement = getDisplacement(ticks);
      if (ticks < 120 && player.getItemInHand(InteractionHand.OFF_HAND).getItem() != ModItems.IDENTIFICATION_TOME) {
         if ((int)displacement != lastHit || ticks == 0) {
            onRollTick.accept(stack);
            rollTag.putInt("LastHit", (int)displacement);
            world.playSound(null, player.blockPosition(), ModSounds.RAFFLE_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
         }

         rollTag.putInt("RollTicks", ticks + 1);
      } else {
         if (ticks == 0) {
            onRollTick.accept(stack);
         }

         onFinish.accept(stack);
         stack.removeTagKey("RollHelper");
         world.playSound(null, player.blockPosition(), ModSounds.CONFETTI_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
      }
   }

   private static double getDisplacement(int tick) {
      double c = 7200.0;
      return (-tick * tick * tick / 6.0 + c * tick) * 50.0 / (-288000.0 + c * 120.0);
   }
}
