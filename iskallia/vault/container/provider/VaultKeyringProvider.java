package iskallia.vault.container.provider;

import iskallia.vault.container.inventory.VaultKeyringContainer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class VaultKeyringProvider implements MenuProvider {
   private final ServerPlayer player;
   private final int slot;
   private final ItemStack keyring;

   public VaultKeyringProvider(ServerPlayer player, int slot, ItemStack keyring) {
      this.player = player;
      this.slot = slot;
      this.keyring = keyring;
   }

   public Component getDisplayName() {
      return this.keyring.getDisplayName();
   }

   public Consumer<FriendlyByteBuf> extraDataWriter() {
      return buffer -> buffer.writeInt(this.slot);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return new VaultKeyringContainer(id, this.player.getInventory(), this.slot);
   }
}
