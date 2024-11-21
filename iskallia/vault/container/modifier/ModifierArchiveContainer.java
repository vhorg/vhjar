package iskallia.vault.container.modifier;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.DiscoverModifierArchiveMessage;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ModifierArchiveContainer extends AbstractElementContainer implements IModifierDiscoveryContainer {
   private final ModifierDiscoveryTileEntity tileEntity;
   private final BlockPos tilePos;
   private final List<DiscoverableModifier> gearModifiers;

   public ModifierArchiveContainer(int windowId, Level world, BlockPos pos, Player player, List<DiscoverableModifier> gearModifiers) {
      super(ModContainers.MODIFIER_ARCHIVE_CONTAINER, windowId, player);
      this.tilePos = pos;
      this.gearModifiers = gearModifiers;
      if (world.getBlockEntity(this.tilePos) instanceof ModifierDiscoveryTileEntity modifierDiscovery) {
         this.tileEntity = modifierDiscovery;
      } else {
         this.tileEntity = null;
      }
   }

   public ModifierDiscoveryTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(player);
   }

   @Override
   public List<DiscoverableModifier> getGearModifiers() {
      return Collections.unmodifiableList(this.gearModifiers);
   }

   @Override
   public void tryDiscoverModifier(DiscoverableModifier gearModifier) {
      ModNetwork.CHANNEL.sendToServer(new DiscoverModifierArchiveMessage(this.tilePos, gearModifier));
   }
}
