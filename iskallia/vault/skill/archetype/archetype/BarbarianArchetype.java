package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.BarbarianConfig;
import iskallia.vault.util.PlayerRageHelper;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BarbarianArchetype extends AbstractArchetype<BarbarianConfig> {
   public BarbarianArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.BARBARIAN, id);
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.RAGE_PER_HIT)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getRagePerHit()));
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.RAGE_DAMAGE)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getPlayerDamageDealtMultiplierPerRagePoint()));
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.HEALING_EFFECTIVENESS)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(
            this,
            data -> data.setValue(
               data.getValue() + this.getConfig().getPlayerHealingEfficiencyMultiplierPerRagePoint() * PlayerRageHelper.getCurrentRage(data.getEntity())
            )
         );
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Override
   public void onRemoved(MinecraftServer server, ServerPlayer player) {
      player.removeEffect(ModEffects.RAGE);
   }
}
