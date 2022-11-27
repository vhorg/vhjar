package iskallia.vault.network.message;

import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.world.data.PlayerResearchesData;
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

public class ServerboundOpenResearchesMessage {
   public static final ServerboundOpenResearchesMessage INSTANCE = new ServerboundOpenResearchesMessage();

   public static void encode(ServerboundOpenResearchesMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenResearchesMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenResearchesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerResearchesData playerResearchesData = PlayerResearchesData.get((ServerLevel)sender.level);
            final ResearchTree researchTree = playerResearchesData.getResearches(sender);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.skills");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new NBTElementContainer(() -> ModContainers.RESEARCH_TAB_CONTAINER, i, playerInventory.player, researchTree);
               }
            }, buffer -> buffer.writeNbt(researchTree.serializeNBT()));
         }
      });
      context.setPacketHandled(true);
   }
}
