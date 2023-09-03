package iskallia.vault.snapshot;

import iskallia.vault.client.ClientEternalData;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.PlayerSnapshotMessage;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.EternalsData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.ObjectUtils;

public class AttributeSnapshotHelper {
   private static final AttributeSnapshotHelper instance = new AttributeSnapshotHelper();
   private final Map<UUID, AttributeSnapshot> playerSnapshots = new HashMap<>();
   private AttributeSnapshot clientPlayerSnapshot = null;

   private AttributeSnapshotHelper() {
   }

   public static AttributeSnapshotHelper getInstance() {
      return instance;
   }

   public static boolean canHaveSnapshot(LivingEntity entity) {
      return entity instanceof EternalEntity || entity instanceof Player;
   }

   @Nonnull
   public AttributeSnapshot getSnapshot(LivingEntity entity) {
      if (entity instanceof Player player) {
         return this.getOrCreatePlayerSnapshot(player);
      } else {
         if (entity instanceof EternalEntity eternal) {
            if (eternal.getLevel() instanceof ServerLevel sWorld) {
               EternalData eternalData = EternalsData.get(sWorld).getEternal(eternal.getEternalId());
               if (eternalData != null) {
                  return eternalData.getAttributeSnapshot();
               }
            } else {
               EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(eternal.getEternalId());
               if (snapshot != null) {
                  return snapshot.getAttributeSnapshot();
               }
            }
         }

         return AttributeSnapshot.EMPTY;
      }
   }

   @Nonnull
   private AttributeSnapshot getOrCreatePlayerSnapshot(Player player) {
      if (!player.getCommandSenderWorld().isClientSide() && player instanceof ServerPlayer) {
         AttributeSnapshot snapshot = this.playerSnapshots.get(player.getUUID());
         return snapshot != null ? snapshot : this.createAndCacheSnapshot((ServerPlayer)player);
      } else {
         return (AttributeSnapshot)ObjectUtils.firstNonNull(new AttributeSnapshot[]{this.clientPlayerSnapshot, AttributeSnapshot.EMPTY});
      }
   }

   public void refreshSnapshotDelayed(ServerPlayer player) {
      ServerScheduler.INSTANCE.schedule(1, () -> this.refreshSnapshot(player));
   }

   public void refreshSnapshot(ServerPlayer player) {
      this.playerSnapshots.remove(player.getUUID());
      this.createAndCacheSnapshot(player);
   }

   @Nonnull
   public AttributeSnapshot makeGearSnapshot(Function<EquipmentSlot, ItemStack> equipmentFn) {
      AttributeSnapshot snapshot = new AttributeSnapshot();
      AttributeSnapshotCalculator.computeGearSnapshot(equipmentFn, item -> false, Integer.MAX_VALUE, snapshot);
      return snapshot;
   }

   @Nonnull
   private AttributeSnapshot createAndCacheSnapshot(ServerPlayer player) {
      AttributeSnapshot snapshot = new AttributeSnapshot();
      AttributeSnapshotCalculator.computeSnapshot(player, snapshot);
      this.playerSnapshots.put(player.getUUID(), snapshot);
      PlayerSnapshotMessage msg = PlayerSnapshotMessage.of(snapshot);
      ModNetwork.CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
      return snapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public void receiveClientSnapshot(AttributeSnapshot snapshot) {
      this.clientPlayerSnapshot = snapshot;
   }
}
