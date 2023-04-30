package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.mixin.AccessorServerPlayerGameMode;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class GhostWalkSpiritAbility extends GhostWalkAbility {
   public GhostWalkSpiritAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int durationTicks) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, durationTicks);
   }

   public GhostWalkSpiritAbility() {
   }

   @Override
   protected MobEffect getEffect() {
      return ModEffects.GHOST_WALK_SPIRIT_WALK;
   }

   @SubscribeEvent
   public static void on(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer player && ((AccessorServerPlayerGameMode)player.gameMode).isDestroyingBlock()) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

         for (GhostWalkSpiritAbility ability : abilities.getAll(GhostWalkSpiritAbility.class, Skill::isUnlocked)) {
            player.removeEffect(ability.getEffect());
         }
      }
   }

   @SubscribeEvent
   public static void on(Open event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

         for (GhostWalkSpiritAbility ability : abilities.getAll(GhostWalkSpiritAbility.class, Skill::isUnlocked)) {
            player.removeEffect(ability.getEffect());
         }
      }
   }
}
