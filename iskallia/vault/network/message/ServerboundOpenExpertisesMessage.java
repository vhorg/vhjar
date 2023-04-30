package iskallia.vault.network.message;

import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
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

public class ServerboundOpenExpertisesMessage {
   public static final ServerboundOpenExpertisesMessage INSTANCE = new ServerboundOpenExpertisesMessage();

   public static void encode(ServerboundOpenExpertisesMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenExpertisesMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenExpertisesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerExpertisesData playerExpertisesData = PlayerExpertisesData.get((ServerLevel)sender.level);
            final ExpertiseTree expertiseTree = playerExpertisesData.getExpertises(sender);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.expertises");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new NBTElementContainer(() -> ModContainers.EXPERTISE_TAB_CONTAINER, i, playerInventory.player, expertiseTree);
               }
            }, buffer -> {
               ArrayBitBuffer buffer1 = ArrayBitBuffer.empty();
               expertiseTree.writeBits(buffer1);
               buffer.writeLongArray(buffer1.toLongArray());
            });
         }
      });
      context.setPacketHandled(true);
   }
}
