package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.screen.quest.QuestOverviewElementScreen;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.quest.QuestRequestSyncMessage;
import iskallia.vault.quest.client.ClientQuestState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class QuestBookItem extends BasicItem {
   public QuestBookItem(ResourceLocation id) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      if (level.isClientSide()) {
         this.openQuestScreen();
      }

      return super.use(level, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   private void openQuestScreen() {
      if (ClientQuestState.INSTANCE.getState() == null) {
         VaultMod.LOGGER.debug("Client Quest State is null. Requesting sync.");
         ModNetwork.CHANNEL.sendToServer(new QuestRequestSyncMessage());
      } else {
         Minecraft.getInstance().setScreen(new QuestOverviewElementScreen());
      }
   }
}
