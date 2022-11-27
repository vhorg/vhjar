package iskallia.vault.world.vault.logic;

import iskallia.vault.entity.entity.VaultSandEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.SandEventContributorMessage;
import iskallia.vault.network.message.SandEventUpdateMessage;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class VaultSandEvent implements INBTSerializable<CompoundTag>, IVaultTask {
   private static final Random rand = new Random();
   private final Map<UUID, VaultSandEvent.SandProgress> playerProgress = new HashMap<>();

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      if (world.getGameTime() % 10L == 0L) {
         player.runIfPresent(world.getServer(), this::sendUpdate);
      }
   }

   public void addSand(ServerPlayer player, String contributor, ChatFormatting contributorColor, int amount, Supplier<Boolean> onSandFilled) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(player.getUUID(), uuid -> new VaultSandEvent.SandProgress());
      int requiredTotal = ModConfigs.SAND_EVENT.getRedemptionsRequiredPerSand(player);
      int current = (int)(requiredTotal * progress.fillPercent);
      int newAmount = current + amount;
      if (newAmount > requiredTotal) {
         if (onSandFilled.get()) {
            progress.fillPercent = 0.0F;
            progress.spawnedSands++;
            player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 1.0F);
         }
      } else {
         progress.fillPercent = Math.min((float)newAmount / requiredTotal, 1.0F);
      }

      this.sendUpdate(player, progress);
      MutableComponent display = new TextComponent(contributor).withStyle(contributorColor);
      ModNetwork.CHANNEL.sendTo(new SandEventContributorMessage(display), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void pickupSand(ServerPlayer player) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(player.getUUID(), uuid -> new VaultSandEvent.SandProgress());
      progress.collectedSands++;
      this.sendUpdate(player, progress);
   }

   private void sendUpdate(ServerPlayer sPlayer) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(sPlayer.getUUID(), uuid -> new VaultSandEvent.SandProgress());
      this.sendUpdate(sPlayer, progress);
   }

   private void sendUpdate(ServerPlayer sPlayer, VaultSandEvent.SandProgress progress) {
      ModNetwork.CHANNEL.sendTo(progress.makeUpdatePacket(), sPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static boolean spawnSand(ServerLevel world, ServerPlayer player) {
      int attempts = 100000;
      float min = ModConfigs.SAND_EVENT.getMinDistance();
      float max = ModConfigs.SAND_EVENT.getMaxDistance();

      for (BlockPos offset = player.blockPosition(); attempts > 0; attempts--) {
         int x = Math.round(min + rand.nextFloat() * (max - min)) * (rand.nextBoolean() ? 1 : -1);
         int y = Math.round(rand.nextFloat() * 30.0F) * (rand.nextBoolean() ? 1 : -1);
         int z = Math.round(min + rand.nextFloat() * (max - min)) * (rand.nextBoolean() ? 1 : -1);
         BlockPos pos = offset.offset(x, y, z);
         if (world.isAreaLoaded(pos, 1)) {
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) {
               world.addFreshEntity(VaultSandEntity.create(world, pos));
               return true;
            }
         }
      }

      return false;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      this.playerProgress.forEach((uuid, progress) -> nbt.put(uuid.toString(), progress.serialize()));
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.playerProgress.clear();

      for (String key : nbt.getAllKeys()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var6) {
            continue;
         }

         CompoundTag tag = nbt.getCompound(key);
         this.playerProgress.put(playerUUID, VaultSandEvent.SandProgress.deserialize(tag));
      }
   }

   private static class SandProgress {
      private float fillPercent = 0.0F;
      private int spawnedSands = 0;
      private int collectedSands = 0;

      private CompoundTag serialize() {
         CompoundTag nbt = new CompoundTag();
         nbt.putFloat("fillPercent", this.fillPercent);
         nbt.putInt("spawnedSands", this.spawnedSands);
         nbt.putInt("collectedSands", this.collectedSands);
         return nbt;
      }

      private static VaultSandEvent.SandProgress deserialize(CompoundTag nbt) {
         VaultSandEvent.SandProgress progress = new VaultSandEvent.SandProgress();
         progress.fillPercent = nbt.getFloat("fillPercent");
         progress.spawnedSands = nbt.getInt("spawnedSands");
         progress.collectedSands = nbt.getInt("collectedSands");
         return progress;
      }

      private SandEventUpdateMessage makeUpdatePacket() {
         return new SandEventUpdateMessage(this.fillPercent, this.spawnedSands, this.collectedSands);
      }
   }
}
