package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.util.calc.GrantedEffectHelper;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

public class GrantedEffectEvent extends Event<GrantedEffectEvent, GrantedEffectEvent.Data> {
   public GrantedEffectEvent() {
   }

   protected GrantedEffectEvent(GrantedEffectEvent parent) {
      super(parent);
   }

   public GrantedEffectEvent createChild() {
      return new GrantedEffectEvent(this);
   }

   public GrantedEffectEvent.Data invoke(GrantedEffectHelper.GrantedEffects effects, ServerLevel world, Player player, Predicate<MobEffect> filter) {
      return this.invoke(new GrantedEffectEvent.Data(effects, world, player, filter));
   }

   public static class Data {
      private final GrantedEffectHelper.GrantedEffects effects;
      private final ServerLevel world;
      private final Player player;
      private final Predicate<MobEffect> filter;

      public Data(GrantedEffectHelper.GrantedEffects effects, ServerLevel world, Player player, Predicate<MobEffect> filter) {
         this.effects = effects;
         this.world = world;
         this.player = player;
         this.filter = filter;
      }

      public GrantedEffectHelper.GrantedEffects getEffects() {
         return this.effects;
      }

      public ServerLevel getWorld() {
         return this.world;
      }

      public Player getPlayer() {
         return this.player;
      }

      public Predicate<MobEffect> getFilter() {
         return this.filter;
      }
   }
}
