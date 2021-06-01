package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import java.util.HashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class TankAbility extends EffectAbility {
   @Expose
   private int durationTicks;
   public static HashMap<PlayerEntity, Vector3d> prevPositions = new HashMap<>();

   public TankAbility(int cost, Effect effect, int level, int durationTicks, EffectAbility.Type type, PlayerAbility.Behavior behavior) {
      super(cost, effect, level, type, behavior);
      this.durationTicks = durationTicks;
   }

   @Override
   public void onTick(PlayerEntity player, boolean active) {
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      EffectInstance activeEffect = player.func_70660_b(this.getEffect());
      EffectInstance newEffect = new EffectInstance(
         ModEffects.TANK, this.getDurationTicks(), this.getAmplifier(), false, this.getType().showParticles, this.getType().showIcon
      );
      if (activeEffect == null) {
         player.func_195064_c(newEffect);
      }
   }

   @Override
   public void onBlur(PlayerEntity player) {
   }

   @SubscribeEvent
   public static void onDamage(LivingDamageEvent event) {
      LivingEntity entity = event.getEntityLiving();
      EffectInstance tank = entity.func_70660_b(ModEffects.TANK);
      if (tank != null) {
         float reduction = (tank.func_76458_c() + 1) * 0.1F;
         event.setAmount(event.getAmount() - event.getAmount() * reduction);
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.END && event.side != LogicalSide.CLIENT) {
         PlayerEntity player = event.player;
         EffectInstance tank = player.func_70660_b(ModEffects.TANK);
         if (tank != null) {
            double multiplier = tank == null ? 1.0 : 1.0 - Math.abs((50.0 - tank.func_76458_c() * 5.0) * 0.01);
            Vector3d currentPos = player.func_213303_ch();
            Vector3d prevPos = prevPositions.get(player) == null ? player.func_213303_ch() : prevPositions.get(player);
            Vector3d direction = new Vector3d(
               prevPos.func_82615_a() - currentPos.func_82615_a(),
               prevPos.func_82617_b() - currentPos.func_82617_b(),
               prevPos.func_82616_c() - currentPos.func_82616_c()
            );
            player.func_213293_j(direction.func_82615_a() * -multiplier, player.func_213322_ci().func_82617_b(), direction.func_82616_c() * -multiplier);
            player.field_70133_I = true;
         }

         prevPositions.put(player, player.func_213303_ch());
      }
   }
}
