package iskallia.vault.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.time.extension.AccelerationExtension;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class TimerAccelerationEffect extends Effect {
   public TimerAccelerationEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76403_b() {
      return false;
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (!event.side.isClient()) {
         if (event.phase == Phase.END) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.player;
            EffectInstance effect = player.func_70660_b(ModEffects.TIMER_ACCELERATION);
            if (effect != null) {
               VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
               if (vault != null) {
                  vault.getPlayers().forEach(vaultPlayer -> {
                     int extraTicks = effect.func_76458_c() * 6;
                     AccelerationExtension extension = new AccelerationExtension(-extraTicks);
                     vaultPlayer.getTimer().addTime(extension, 0);
                  });
               }
            }
         }
      }
   }
}
