package iskallia.vault.container.provider;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.modifier.DiscoverableModifier;
import iskallia.vault.container.modifier.ModifierScrollContainer;
import iskallia.vault.item.ModifierScrollItem;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ModifierScrollProvider implements MenuProvider {
   private final ServerPlayer sPlayer;
   private final int slot;
   private final UUID scrollUuid;
   private final ItemStack stack;

   public ModifierScrollProvider(ServerPlayer sPlayer, int slot, UUID scrollUuid, ItemStack stack) {
      this.sPlayer = sPlayer;
      this.slot = slot;
      this.scrollUuid = scrollUuid;
      this.stack = stack;
   }

   public Consumer<FriendlyByteBuf> extraDataWriter() {
      List<DiscoverableModifier> discoverableModifiers = this.getStoredDiscoverableModifiers();
      return buffer -> {
         buffer.writeInt(this.slot);
         buffer.writeUUID(this.scrollUuid);
         CompoundTag gearModifiersTag = new CompoundTag();
         gearModifiersTag.put("gearModifiers", ModifierDiscoveryTileEntity.writeGearModifiers(discoverableModifiers));
         buffer.writeNbt(gearModifiersTag);
      };
   }

   private List<DiscoverableModifier> getStoredDiscoverableModifiers() {
      DiscoveredWorkbenchModifiersData data = DiscoveredWorkbenchModifiersData.get(this.sPlayer.getLevel());
      return ModifierScrollItem.getDiscoverableModifiers(this.stack).stream().map(mod -> {
         boolean discovered = data.hasDiscoveredCraft(this.sPlayer, mod.item(), mod.modifierId());
         return new DiscoverableModifier(mod.item(), mod.modifierId(), discovered);
      }).collect(Collectors.toList());
   }

   public Component getDisplayName() {
      return this.stack.getDisplayName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player player) {
      return new ModifierScrollContainer(pContainerId, this.slot, this.scrollUuid, player, this.getStoredDiscoverableModifiers());
   }
}
