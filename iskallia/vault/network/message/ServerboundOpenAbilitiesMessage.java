package iskallia.vault.network.message;

import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
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

public class ServerboundOpenAbilitiesMessage {
   public static final ServerboundOpenAbilitiesMessage INSTANCE = new ServerboundOpenAbilitiesMessage();

   public static void encode(ServerboundOpenAbilitiesMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenAbilitiesMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenAbilitiesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            final AbilityTree abilityTree = playerAbilitiesData.getAbilities(sender);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.abilities");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new NBTElementContainer(() -> ModContainers.ABILITY_TAB_CONTAINER, i, playerInventory.player, abilityTree);
               }
            }, buffer -> buffer.writeNbt(abilityTree.serializeNBT()));
         }
      });
      context.setPacketHandled(true);
   }
}
