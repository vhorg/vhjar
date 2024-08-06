package iskallia.vault.item;

import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.provider.AntiqueCollectorBookProvider;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerStoredAntiquesData;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class AntiqueStampCollectorBook extends Item {
   public AntiqueStampCollectorBook(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack inHand = player.getItemInHand(hand);
      if (player instanceof ServerPlayer sPlayer && ServerVaults.get(player.getLevel()).isEmpty()) {
         int slot = hand == InteractionHand.MAIN_HAND ? sPlayer.getInventory().selected : 40;
         openBook(sPlayer, slot, inHand);
         return InteractionResultHolder.success(inHand);
      } else {
         return InteractionResultHolder.pass(inHand);
      }
   }

   public static void openBook(ServerPlayer player, int bookSlot, ItemStack bookStack) {
      AntiqueCollectorBookProvider provider = new AntiqueCollectorBookProvider(player, bookSlot, bookStack);
      NetworkHooks.openGui(player, provider, provider.extraDataWriter());
   }

   public static Container getAntiqueStorage(ServerPlayer player, Predicate<Player> stillValidCheck) {
      return getAntiqueStorage(PlayerStoredAntiquesData.get(player.getLevel()).getStoredAntiques(player), stillValidCheck);
   }

   public static Container getAntiqueStorage(PlayerStoredAntiquesData.StoredAntiques storedAntiques, Predicate<Player> stillValidCheck) {
      OverSizedInventory container = new OverSizedInventory(AntiqueRegistry.getRegistry().getValues().size(), () -> {}, stillValidCheck);
      List<Antique> sortedAntiques = AntiqueRegistry.sorted().toList();

      for (int slot = 0; slot < sortedAntiques.size(); slot++) {
         Antique antique = sortedAntiques.get(slot);
         if (storedAntiques.containsKey(antique.getRegistryName())) {
            int count = storedAntiques.get(antique.getRegistryName());
            if (count > 0) {
               container.setItem(slot, AntiqueItem.createStack(antique, count));
            }
         }
      }

      return container;
   }
}
