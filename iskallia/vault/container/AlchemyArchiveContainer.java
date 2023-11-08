package iskallia.vault.container;

import iskallia.vault.block.entity.AlchemyArchiveTileEntity;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AlchemyArchiveDiscoverEffectMessage;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AlchemyArchiveContainer extends AbstractElementContainer {
   private final AlchemyArchiveTileEntity tileEntity;
   private final BlockPos tilePos;
   private final List<String> effects;

   public AlchemyArchiveContainer(int windowId, Level world, BlockPos pos, Player player, List<String> effects) {
      super(ModContainers.ALCHEMY_ARCHIVE_CONTAINER, windowId, player);
      this.tilePos = pos;
      this.effects = effects;
      if (world.getBlockEntity(this.tilePos) instanceof AlchemyArchiveTileEntity archive) {
         this.tileEntity = archive;
      } else {
         this.tileEntity = null;
      }
   }

   public boolean stillValid(Player pPlayer) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
   }

   public AlchemyArchiveTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public List<String> getEffects() {
      return this.effects;
   }

   public void discoverEffect(String effectId) {
      ModNetwork.CHANNEL.sendToServer(new AlchemyArchiveDiscoverEffectMessage(this.tilePos, effectId));
   }
}
