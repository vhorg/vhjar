package iskallia.vault.item;

import iskallia.vault.config.FlawedRubyConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FlawedRubyItem extends BasicTooltipItem {
   public FlawedRubyItem(ResourceLocation id, Properties properties, ITextComponent... components) {
      super(id, properties, components);
   }

   public static void markApplied(ItemStack gearPiece) {
      if (gearPiece.func_77973_b() instanceof VaultGear) {
         CompoundNBT nbt = gearPiece.func_196082_o();
         nbt.func_74757_a("FlawedRubyApplied", true);
      }
   }

   public static void handleOutcome(ServerPlayerEntity player, ItemStack gearPiece) {
      if (gearPiece.func_77973_b() instanceof VaultGear) {
         if (shouldHandleOutcome(gearPiece)) {
            World world = player.func_130014_f_();
            if (!(world instanceof ServerWorld)) {
               return;
            }

            ServerWorld serverWorld = (ServerWorld)world;
            FlawedRubyConfig.Outcome outcome = FlawedRubyConfig.Outcome.FAIL;
            TalentTree talents = PlayerTalentsData.get(serverWorld).getTalents(player);
            if (talents.hasLearnedNode(ModConfigs.TALENTS.ARTISAN)) {
               outcome = ModConfigs.FLAWED_RUBY.getForArtisan();
            } else if (talents.hasLearnedNode(ModConfigs.TALENTS.TREASURE_HUNTER)) {
               outcome = ModConfigs.FLAWED_RUBY.getForTreasureHunter();
            }

            if (outcome == FlawedRubyConfig.Outcome.IMBUE) {
               int max = ModAttributes.GEAR_MAX_LEVEL.getOrDefault(gearPiece, 0).getValue(gearPiece);
               ModAttributes.GEAR_MAX_LEVEL.create(gearPiece, max + 1);
               ModAttributes.IMBUED.create(gearPiece, true);
            } else if (outcome == FlawedRubyConfig.Outcome.BREAK) {
               gearPiece.func_190920_e(0);
               player.func_130014_f_().func_184133_a(null, player.func_233580_cy_(), SoundEvents.field_187635_cQ, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            resetApplied(gearPiece);
         }
      }
   }

   static void resetApplied(ItemStack gearPiece) {
      if (gearPiece.func_77973_b() instanceof VaultGear) {
         CompoundNBT nbt = gearPiece.func_196082_o();
         nbt.func_74757_a("FlawedRubyApplied", false);
      }
   }

   public static boolean shouldHandleOutcome(ItemStack gearPiece) {
      if (!(gearPiece.func_77973_b() instanceof VaultGear)) {
         return false;
      } else {
         CompoundNBT nbt = gearPiece.func_196082_o();
         return nbt.func_74767_n("FlawedRubyApplied");
      }
   }
}
