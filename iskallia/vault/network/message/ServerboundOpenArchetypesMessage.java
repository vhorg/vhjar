package iskallia.vault.network.message;

import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import iskallia.vault.world.data.PlayerArchetypeData;
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

public class ServerboundOpenArchetypesMessage {
   public static final ServerboundOpenArchetypesMessage INSTANCE = new ServerboundOpenArchetypesMessage();

   public static void encode(ServerboundOpenArchetypesMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenArchetypesMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenArchetypesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerArchetypeData playerArchetypeData = PlayerArchetypeData.get((ServerLevel)sender.level);
            final ArchetypeContainer archetypeContainer = playerArchetypeData.getArchetypeContainer(sender);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.archetypes");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new NBTElementContainer(() -> ModContainers.ARCHETYPE_TAB_CONTAINER, i, playerInventory.player, archetypeContainer);
               }
            }, buffer -> buffer.writeNbt(archetypeContainer.serializeNBT()));
         }
      });
      context.setPacketHandled(true);
   }
}
