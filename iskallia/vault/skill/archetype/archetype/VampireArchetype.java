package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.VampireConfig;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VampireArchetype extends AbstractArchetype<VampireConfig> {
   public VampireArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.VAMPIRE, id);
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.LEECH)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getAdditionalPercentageLifeLeech()));
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void on(LivingHealEvent event) {
      if (this.hasThisArchetype(event.getEntityLiving()) && !ActiveFlags.IS_LEECHING.isSet()) {
         event.setAmount(0.0F);
      }
   }
}
