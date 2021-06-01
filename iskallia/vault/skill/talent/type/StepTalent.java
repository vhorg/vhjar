package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class StepTalent extends PlayerTalent {
   @Expose
   private final float stepHeightAddend;

   public StepTalent(int cost, float stepHeightAddend) {
      super(cost);
      this.stepHeightAddend = stepHeightAddend;
   }

   public float getStepHeightAddend() {
      return this.stepHeightAddend;
   }

   @Override
   public void onAdded(PlayerEntity player) {
      player.field_70138_W = player.field_70138_W + this.stepHeightAddend;
      set((ServerPlayerEntity)player, player.field_70138_W + this.stepHeightAddend);
   }

   @Override
   public void tick(PlayerEntity player) {
   }

   @Override
   public void onRemoved(PlayerEntity player) {
      set((ServerPlayerEntity)player, player.field_70138_W - this.stepHeightAddend);
   }

   @SubscribeEvent
   public static void onEntityCreated(EntityJoinWorldEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K) {
         if (event.getEntity() instanceof PlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
            TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);
            float totalStepHeight = 0.0F;

            for (TalentNode<?> node : abilities.getNodes()) {
               if (node.getTalent() instanceof StepTalent) {
                  StepTalent talent = (StepTalent)node.getTalent();
                  totalStepHeight += talent.getStepHeightAddend();
               }
            }

            if (totalStepHeight != 0.0F) {
               set(player, player.field_70138_W + totalStepHeight);
            }
         }
      }
   }

   public static void set(ServerPlayerEntity player, float stepHeight) {
      ModNetwork.CHANNEL.sendTo(new StepHeightMessage(stepHeight), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      player.field_70138_W = stepHeight;
   }
}
