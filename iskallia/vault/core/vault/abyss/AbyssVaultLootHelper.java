package iskallia.vault.core.vault.abyss;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class AbyssVaultLootHelper {
   private static final AbyssVaultLootHelper INSTANCE = new AbyssVaultLootHelper();

   private AbyssVaultLootHelper() {
   }

   public static void init() {
      CommonEvents.PLAYER_STAT.of(PlayerStat.ITEM_QUANTITY).register(INSTANCE, event -> {
         LivingEntity entity = event.getEntity();
         float effect = AbyssHelper.getAbyssEffect(entity) * AbyssHelper.getAbyssDistanceModifier(entity);
         event.setValue(event.getValue() * (1.0F + effect * ModConfigs.ABYSS.getItemQuantityBonus()));
      });
      CommonEvents.PLAYER_STAT.of(PlayerStat.ITEM_RARITY).register(INSTANCE, event -> {
         LivingEntity entity = event.getEntity();
         float effect = AbyssHelper.getAbyssEffect(entity) * AbyssHelper.getAbyssDistanceModifier(entity);
         event.setValue(event.getValue() * (1.0F + effect * ModConfigs.ABYSS.getItemRarityBonus()));
      });
      CommonEvents.CHEST_LOOT_GENERATION.post().register(INSTANCE, event -> {
         float multiplier = event.getState().is(ModBlocks.TREASURE_CHEST) ? 10.0F : 1.0F;
         float effect = AbyssHelper.getAbyssEffect(event.getPlayer()) * AbyssHelper.getAbyssDistanceModifier(event.getPlayer());
         if (event.getRandom().nextFloat() < effect * multiplier * ModConfigs.ABYSS.getAbyssalFocusChance()) {
            event.getLoot().add(new ItemStack(event.getRandom().nextBoolean() ? ModItems.WAXING_FOCUS : ModItems.WANING_FOCUS));
         }
      });
      MinecraftForge.EVENT_BUS.addListener(INSTANCE::onBreakSpeed);
   }

   private void onBreakSpeed(BreakSpeed event) {
      LivingEntity entity = event.getEntityLiving();
      float effect = AbyssHelper.getAbyssEffect(entity) * AbyssHelper.getAbyssDistanceModifier(entity);
      effect *= ModConfigs.ABYSS.getBreakSpeedReductionMultiplier();
      event.setNewSpeed(Math.max(event.getNewSpeed() - Math.min(event.getNewSpeed() * effect, effect * 30.0F), 0.4F));
   }

   public static float getCopiouslyChance(Entity entity) {
      float effect = AbyssHelper.getAbyssEffect(entity) * AbyssHelper.getAbyssDistanceModifier(entity);
      return effect * ModConfigs.ABYSS.getCopiouslyBonus();
   }
}
