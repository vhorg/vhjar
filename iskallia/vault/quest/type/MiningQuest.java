package iskallia.vault.quest.type;

import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class MiningQuest extends Quest {
   public static final String MINING = "mining";

   public MiningQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("mining", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent
   public void onBreakBlock(BreakEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            Block targetBlock = (Block)ForgeRegistries.BLOCKS.getValue(this.targetId);
            if (targetBlock != null) {
               Block mined = event.getState().getBlock();
               if (mined == targetBlock) {
                  this.progress(player, 1.0F);
               }
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Mine the block.");
   }
}
