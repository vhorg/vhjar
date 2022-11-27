package iskallia.vault.integration;

import iskallia.vault.init.ModGameRules;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import xaero.common.effect.Effects;

public class IntegrationWorldMap {
   @SubscribeEvent
   public static void onPotionTick(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.getLevel().isClientSide() && ServerVaults.isInVault(player)) {
         if (player.tickCount % 5 == 0) {
            List<MobEffect> effectsToApply = new ArrayList<>();
            if (ModList.get().isLoaded("xaeroworldmap")) {
               effectsToApply.add((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("xaeroworldmap", "no_world_map")));
            }

            if (!player.getLevel().getGameRules().getBoolean(ModGameRules.VAULT_ALLOW_WAYPOINTS)) {
               effectsToApply.add(Effects.NO_WAYPOINTS);
            }

            effectsToApply.add(Effects.NO_RADAR);
            effectsToApply.forEach(effect -> player.addEffect(new MobEffectInstance(effect, 59, 0, false, false, false)));
         }
      }
   }
}
