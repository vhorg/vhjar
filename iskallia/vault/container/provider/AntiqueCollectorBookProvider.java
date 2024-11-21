package iskallia.vault.container.provider;

import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.container.inventory.AntiqueCollectorBookContainer;
import iskallia.vault.item.AntiqueStampCollectorBook;
import iskallia.vault.world.data.PlayerStoredAntiquesData;
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

public class AntiqueCollectorBookProvider implements MenuProvider {
   private final ServerPlayer player;
   private final int bookSlot;
   private final ItemStack bookStack;

   public AntiqueCollectorBookProvider(ServerPlayer player, int bookSlot, ItemStack bookStack) {
      this.player = player;
      this.bookSlot = bookSlot;
      this.bookStack = bookStack;
   }

   public Component getDisplayName() {
      return this.bookStack.getDisplayName();
   }

   public Consumer<FriendlyByteBuf> extraDataWriter() {
      return buffer -> buffer.writeInt(this.bookSlot);
   }

   private void migrateStoredAntiques() {
      AntiqueStampCollectorBook.StoredAntiques antiques = AntiqueStampCollectorBook.getStoredAntiques(this.bookStack);
      PlayerStoredAntiquesData data = PlayerStoredAntiquesData.get(this.player.getLevel());
      AntiqueStampCollectorBook.StoredAntiques legacyAntiquesData = data.getStoredAntiques(this.player);
      if (legacyAntiquesData != null) {
         AntiqueRegistry.getRegistry().forEach(antique -> {
            int existingCount = legacyAntiquesData.getInfo(antique).getCount();
            antiques.getInfo(antique).addCount(existingCount);
         });
         data.removeStoredAntiques(this.player);
         AntiqueStampCollectorBook.setStoredAntiques(this.bookStack, antiques);
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      this.migrateStoredAntiques();
      return new AntiqueCollectorBookContainer(id, this.player.getInventory(), this.bookSlot);
   }
}
