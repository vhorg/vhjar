package iskallia.vault.network.message;

import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundOpenTalentsMessage {
   public static final ServerboundOpenTalentsMessage INSTANCE = new ServerboundOpenTalentsMessage();

   public static void encode(ServerboundOpenTalentsMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenTalentsMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenTalentsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerTalentsData playerTalentsData = PlayerTalentsData.get((ServerLevel)sender.level);
            final TalentTree talentTree = playerTalentsData.getTalents(sender);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.skills");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new NBTElementContainer(() -> ModContainers.TALENT_TAB_CONTAINER, i, playerInventory.player, talentTree);
               }
            }, buffer -> buffer.writeNbt(talentTree.serializeNBT()));
         }
      });
      context.setPacketHandled(true);
   }
}
