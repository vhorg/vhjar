package iskallia.vault.quest.type;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInteractionQuest extends Quest {
   public static final String BLOCK_INTERACT = "block_interact";

   public BlockInteractionQuest(
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("block_interact", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onBlockActivated(RightClickBlock event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            if (this.id.equals("complete_god_altar")) {
               if (!(player.getLevel().getBlockEntity(event.getPos()) instanceof FillableAltarTileEntity altar)) {
                  return;
               }

               if (altar.isCompleted() && !altar.isConsumed()) {
                  this.progress(player, 1.0F);
               }
            } else {
               Block target = (Block)ForgeRegistries.BLOCKS.getValue(this.targetId);
               if (target != null) {
                  Block activated = player.getLevel().getBlockState(event.getPos()).getBlock();
                  if (activated == target) {
                     this.progress(player, 1.0F);
                  }
               }
            }
         }
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Activate the target block");
   }
}
