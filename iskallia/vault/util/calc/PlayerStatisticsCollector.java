package iskallia.vault.util.calc;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.world.data.PlayerInfluences;
import iskallia.vault.world.data.VaultSnapshots;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber
public class PlayerStatisticsCollector {
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer sPlayer) {
         if (sPlayer.tickCount % 20 == 0) {
            CompoundTag reputationStats = new CompoundTag();

            for (VaultGod type : VaultGod.values()) {
               reputationStats.putInt(type.getName(), PlayerInfluences.getReputation(sPlayer.getUUID(), type));
            }

            CompoundTag serialized = new CompoundTag();
            serialized.put("reputation", reputationStats);
            PlayerInfluences.getFavour(sPlayer.getUUID()).ifPresent(god -> serialized.putString("favour", god.getName()));
            PlayerStatisticsMessage pkt = new PlayerStatisticsMessage(serialized);
            ModNetwork.CHANNEL.sendTo(pkt, sPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   public static class AttributeSnapshot {
      private final String unlocAttributeName;
      private final String parentAttributeName;
      private final double value;
      private final boolean isPercentage;
      private double limit = -1.0;

      public AttributeSnapshot(String unlocAttributeName, double value, boolean isPercentage) {
         this(unlocAttributeName, null, value, isPercentage);
      }

      public AttributeSnapshot(String unlocAttributeName, String parentAttributeName, double value, boolean isPercentage) {
         this.unlocAttributeName = unlocAttributeName;
         this.parentAttributeName = parentAttributeName;
         this.value = value;
         this.isPercentage = isPercentage;
      }

      private PlayerStatisticsCollector.AttributeSnapshot setLimit(double limit) {
         this.limit = limit;
         return this;
      }

      public String getAttributeName() {
         return this.unlocAttributeName;
      }

      public String getParentAttributeName() {
         return this.parentAttributeName != null ? this.parentAttributeName : this.getAttributeName();
      }

      public double getValue() {
         return this.value;
      }

      public boolean isPercentage() {
         return this.isPercentage;
      }

      public boolean hasLimit() {
         return this.limit != -1.0;
      }

      public double getLimit() {
         return this.limit;
      }

      public boolean hasHitLimit() {
         return this.hasLimit() && this.getValue() > this.getLimit();
      }

      public CompoundTag serialize() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("key", this.getAttributeName());
         nbt.putString("parent", this.getParentAttributeName());
         nbt.putDouble("value", this.getValue());
         nbt.putBoolean("isPercentage", this.isPercentage());
         nbt.putDouble("limit", this.getLimit());
         return nbt;
      }

      public static PlayerStatisticsCollector.AttributeSnapshot deserialize(CompoundTag nbt) {
         return new PlayerStatisticsCollector.AttributeSnapshot(
               nbt.getString("key"), nbt.getString("parent"), nbt.getDouble("value"), nbt.getBoolean("isPercentage")
            )
            .setLimit(nbt.getDouble("limit"));
      }
   }

   public static class VaultRunsSnapshot {
      public int completed;
      public int survived;
      public int failed;
      public int artifacts;

      public static PlayerStatisticsCollector.VaultRunsSnapshot ofPlayer(ServerPlayer sPlayer) {
         PlayerStatisticsCollector.VaultRunsSnapshot data = new PlayerStatisticsCollector.VaultRunsSnapshot();

         for (VaultSnapshot snapshot : VaultSnapshots.getAll()) {
            Vault vault = snapshot.getEnd();
            if (vault != null) {
               vault.ifPresent(Vault.STATS, collector -> {
                  StatCollector stats = collector.get(sPlayer.getUUID());
                  if (stats != null) {
                     switch ((Completion)stats.get(StatCollector.COMPLETION)) {
                        case COMPLETED:
                           data.completed++;
                           break;
                        case BAILED:
                           data.survived++;
                           break;
                        case FAILED:
                           data.failed++;
                     }

                     for (ItemStack reward : stats.get(StatCollector.REWARD)) {
                        Block patt3210$temp = ((BlockItem)reward.getItem()).getBlock();
                        if (patt3210$temp instanceof VaultCrateBlock) {
                           VaultCrateBlock block = (VaultCrateBlock)patt3210$temp;
                           if (reward.getTag() != null) {
                              CompoundTag tag = reward.getOrCreateTag().getCompound("BlockEntityTag").copy();
                              tag.putString("id", ModBlocks.VAULT_CRATE_TILE_ENTITY.getRegistryName().toString());
                              BlockEntity te = BlockEntity.loadStatic(BlockPos.ZERO, block.defaultBlockState(), tag);
                              if (te != null) {
                                 te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                                    for (int i = 0; i < handler.getSlots(); i++) {
                                       ItemStack stack = handler.getStackInSlot(i);
                                       if (stack.getItem() == ModItems.UNIDENTIFIED_ARTIFACT) {
                                          data.artifacts++;
                                       }
                                    }
                                 });
                              }
                           }
                        }
                     }
                  }
               });
            }
         }

         return data;
      }
   }
}
