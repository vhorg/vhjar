package iskallia.vault.world.vault.logic;

import iskallia.vault.entity.VaultSandEntity;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class VaultSandEvent implements INBTSerializable<CompoundNBT>, IVaultTask {
   private static final Random rand = new Random();
   private final Map<UUID, VaultSandEvent.SandProgress> playerProgress = new HashMap<>();

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      if (world.func_82737_E() % 10L == 0L) {
         player.runIfPresent(world.func_73046_m(), this::sendUpdate);
      }
   }

   public void addSand(ServerPlayerEntity player, String contributor, TextFormatting contributorColor, int amount, Supplier<Boolean> onSandFilled) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(player.func_110124_au(), uuid -> new VaultSandEvent.SandProgress());
      int requiredTotal = ModConfigs.SAND_EVENT.getRedemptionsRequiredPerSand(player);
      int current = (int)(requiredTotal * progress.fillPercent);
      int newAmount = current + amount;
      if (newAmount > requiredTotal) {
         if (onSandFilled.get()) {
            progress.fillPercent = 0.0F;
            progress.spawnedSands++;
            player.func_71121_q()
               .func_184148_a(
                  null,
                  player.func_226277_ct_(),
                  player.func_226278_cu_(),
                  player.func_226281_cx_(),
                  SoundEvents.field_187802_ec,
                  SoundCategory.PLAYERS,
                  0.6F,
                  1.0F
               );
         }
      } else {
         progress.fillPercent = Math.min((float)newAmount / requiredTotal, 1.0F);
      }

      this.sendUpdate(player, progress);
      IFormattableTextComponent display = new StringTextComponent(contributor).func_240699_a_(contributorColor);
      ModNetwork.CHANNEL.sendTo(new SandEventContributorMessage(display), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public void pickupSand(ServerPlayerEntity player) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(player.func_110124_au(), uuid -> new VaultSandEvent.SandProgress());
      progress.collectedSands++;
      this.sendUpdate(player, progress);
   }

   private void sendUpdate(ServerPlayerEntity sPlayer) {
      VaultSandEvent.SandProgress progress = this.playerProgress.computeIfAbsent(sPlayer.func_110124_au(), uuid -> new VaultSandEvent.SandProgress());
      this.sendUpdate(sPlayer, progress);
   }

   private void sendUpdate(ServerPlayerEntity sPlayer, VaultSandEvent.SandProgress progress) {
      ModNetwork.CHANNEL.sendTo(progress.makeUpdatePacket(), sPlayer.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static boolean spawnSand(ServerWorld world, ServerPlayerEntity player) {
      int attempts = 100000;
      float min = ModConfigs.SAND_EVENT.getMinDistance();
      float max = ModConfigs.SAND_EVENT.getMaxDistance();

      for (BlockPos offset = player.func_233580_cy_(); attempts > 0; attempts--) {
         int x = Math.round(min + rand.nextFloat() * (max - min)) * (rand.nextBoolean() ? 1 : -1);
         int y = Math.round(rand.nextFloat() * 30.0F) * (rand.nextBoolean() ? 1 : -1);
         int z = Math.round(min + rand.nextFloat() * (max - min)) * (rand.nextBoolean() ? 1 : -1);
         BlockPos pos = offset.func_177982_a(x, y, z);
         if (world.isAreaLoaded(pos, 1)) {
            BlockState state = world.func_180495_p(pos);
            if (state.isAir(world, pos)) {
               world.func_217376_c(VaultSandEntity.create(world, pos));
               return true;
            }
         }
      }

      return false;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      this.playerProgress.forEach((uuid, progress) -> nbt.func_218657_a(uuid.toString(), progress.serialize()));
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.playerProgress.clear();

      for (String key : nbt.func_150296_c()) {
         UUID playerUUID;
         try {
            playerUUID = UUID.fromString(key);
         } catch (IllegalArgumentException var6) {
            continue;
         }

         CompoundNBT tag = nbt.func_74775_l(key);
         this.playerProgress.put(playerUUID, VaultSandEvent.SandProgress.deserialize(tag));
      }
   }

   private static class SandProgress {
      private float fillPercent = 0.0F;
      private int spawnedSands = 0;
      private int collectedSands = 0;

      private SandProgress() {
      }

      private CompoundNBT serialize() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74776_a("fillPercent", this.fillPercent);
         nbt.func_74768_a("spawnedSands", this.spawnedSands);
         nbt.func_74768_a("collectedSands", this.collectedSands);
         return nbt;
      }

      private static VaultSandEvent.SandProgress deserialize(CompoundNBT nbt) {
         VaultSandEvent.SandProgress progress = new VaultSandEvent.SandProgress();
         progress.fillPercent = nbt.func_74760_g("fillPercent");
         progress.spawnedSands = nbt.func_74762_e("spawnedSands");
         progress.collectedSands = nbt.func_74762_e("collectedSands");
         return progress;
      }

      private SandEventUpdateMessage makeUpdatePacket() {
         return new SandEventUpdateMessage(this.fillPercent, this.spawnedSands, this.collectedSands);
      }
   }
}
