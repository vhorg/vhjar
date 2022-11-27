package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StepHeightMessage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;

@Deprecated
@EventBusSubscriber(
   bus = Bus.FORGE
)
public class StepTalent extends PlayerTalent {
   private static final Set<UUID> stepTrackList = new HashSet<>();
   @Expose
   private final float stepHeightAddend;

   public StepTalent(int cost, float stepHeightAddend) {
      super(cost);
      this.stepHeightAddend = stepHeightAddend;
   }

   public float getStepHeightAddend() {
      return this.stepHeightAddend;
   }

   @SubscribeEvent
   public static void onClone(Clone event) {
      refresh((ServerPlayer)event.getOriginal());
   }

   @SubscribeEvent
   public static void onTeleport(PlayerChangedDimensionEvent event) {
      refresh((ServerPlayer)event.getPlayer());
   }

   private static void refresh(ServerPlayer player) {
      player.getServer().tell(new TickTask(2, () -> set(player, player.maxUpStep)));
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      Player player = event.player;
      if (player.getCommandSenderWorld().isClientSide() || !(player.getCommandSenderWorld() instanceof ServerLevel) || !(player instanceof ServerPlayer)) {
         ;
      }
   }

   private static void set(ServerPlayer player, float stepHeight) {
      ModNetwork.CHANNEL.sendTo(new StepHeightMessage(stepHeight - 0.4F), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      player.maxUpStep = stepHeight;
   }
}
