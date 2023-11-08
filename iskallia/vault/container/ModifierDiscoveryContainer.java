package iskallia.vault.container;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.DiscoverModifierMessage;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ModifierDiscoveryContainer extends AbstractElementContainer {
   private final ModifierDiscoveryTileEntity tileEntity;
   private final BlockPos tilePos;
   private final List<Tuple<Item, ResourceLocation>> gearModifiers;

   public ModifierDiscoveryContainer(int windowId, Level world, BlockPos pos, Player player, List<Tuple<Item, ResourceLocation>> gearModifiers) {
      super(ModContainers.MODIFIER_DISCOVERY_CONTAINER, windowId, player);
      this.tilePos = pos;
      this.gearModifiers = gearModifiers;
      if (world.getBlockEntity(this.tilePos) instanceof ModifierDiscoveryTileEntity modifierDiscovery) {
         this.tileEntity = modifierDiscovery;
      } else {
         this.tileEntity = null;
      }
   }

   public boolean stillValid(Player pPlayer) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
   }

   public ModifierDiscoveryTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public List<Tuple<Item, ResourceLocation>> getGearModifiers() {
      return this.gearModifiers;
   }

   public void discoverModifier(Tuple<Item, ResourceLocation> gearModifier) {
      ModNetwork.CHANNEL.sendToServer(new DiscoverModifierMessage(this.tilePos, gearModifier));
   }
}
