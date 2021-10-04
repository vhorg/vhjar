package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

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
      refresh((ServerPlayerEntity)event.getOriginal());
   }

   @SubscribeEvent
   public static void onTeleport(PlayerChangedDimensionEvent event) {
      refresh((ServerPlayerEntity)event.getPlayer());
   }

   private static void refresh(ServerPlayerEntity player) {
      player.func_184102_h().func_212871_a_(new TickDelayedTask(2, () -> set(player, player.field_70138_W)));
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      PlayerEntity player = event.player;
      if (!player.func_130014_f_().func_201670_d() && player.func_130014_f_() instanceof ServerWorld && player instanceof ServerPlayerEntity) {
         ServerWorld sWorld = (ServerWorld)player.func_130014_f_();
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
         UUID playerUUID = player.func_110124_au();
         TalentTree talentTree = PlayerTalentsData.get(sWorld).getTalents(player);
         TalentNode<?> node = talentTree.getNodeOf(ModConfigs.TALENTS.STEP);
         if (node.getTalent() instanceof StepTalent) {
            StepTalent talent = (StepTalent)node.getTalent();
            if (node.isLearned() && !player.func_213453_ef()) {
               stepTrackList.add(playerUUID);
               float targetHeight = 1.0F + talent.getStepHeightAddend();
               if (sPlayer.field_70138_W < targetHeight) {
                  set(sPlayer, targetHeight);
               }
            } else if (stepTrackList.contains(playerUUID)) {
               set(sPlayer, 1.0F);
               stepTrackList.remove(playerUUID);
            }
         }
      }
   }

   private static void set(ServerPlayerEntity player, float stepHeight) {
      ModNetwork.CHANNEL.sendTo(new StepHeightMessage(stepHeight - 0.4F), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      player.field_70138_W = stepHeight;
   }
}
