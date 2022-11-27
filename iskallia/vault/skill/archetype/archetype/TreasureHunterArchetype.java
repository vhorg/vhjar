package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.TreasureHunterConfig;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TreasureHunterArchetype extends AbstractArchetype<TreasureHunterConfig> {
   public TreasureHunterArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.TREASURE_HUNTER, id);
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.ITEM_QUANTITY)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getAdditionalItemQuantity()));
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.ITEM_RARITY)
         .filter(data -> this.hasThisArchetype(data.getEntity()))
         .register(this, data -> data.setValue(data.getValue() + this.getConfig().getAdditionalItemRarity()));
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void on(LivingHurtEvent event) {
      if (this.hasThisArchetype(event.getEntityLiving())) {
         event.setAmount(event.getAmount() * this.getConfig().getPlayerDamageTakenMultiplier());
      }
   }
}
