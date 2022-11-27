package iskallia.vault.entity.entity;

import com.google.common.base.Strings;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.StreamData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class VaultFighterEntity extends FighterEntity {
   public VaultFighterEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
   }

   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag
   ) {
      SpawnGroupData livingData = super.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
      ServerLevel sWorld = (ServerLevel)this.level;
      if (!this.level.isClientSide()) {
         VaultRaid vault = null;
         if (vault != null) {
            StreamData.Subscribers.Instance sub = vault.getPlayers()
               .stream()
               .map(VaultPlayer::getPlayerId)
               .map(uuid -> StreamData.get(sWorld).getSubscribers(uuid))
               .reduce(StreamData.Subscribers::merge)
               .get()
               .getRandom(world.getRandom());
            String star = String.valueOf('✦');
            int count = Math.max(ModEntities.VAULT_FIGHTER_TYPES.indexOf(this.getType()), 0);
            MutableComponent customName = new TextComponent("")
               .append(new TextComponent(Strings.repeat(star, count)).withStyle(ChatFormatting.GOLD))
               .append(" ");
            if (sub != null) {
               customName.append(
                  new TextComponent("[")
                     .append(new TextComponent(String.valueOf(sub.getMonths())).withStyle(ChatFormatting.AQUA))
                     .append(new TextComponent("] " + sub.getName()))
               );
               this.getPersistentData().putString("VaultPlayerName", sub.getName());
            } else {
               List<ServerPlayer> players = vault.getPlayers()
                  .stream()
                  .map(VaultPlayer::getPlayerId)
                  .map(uuid -> sWorld.getServer().getPlayerList().getPlayer(uuid))
                  .filter(Objects::nonNull)
                  .toList();
               String name = players.isEmpty() ? "" : players.get(world.getRandom().nextInt(players.size())).getName().getString();
               customName.append(new TextComponent(name));
               this.getPersistentData().putString("VaultPlayerName", name);
            }

            this.setCustomName(customName);
         }
      }

      return livingData;
   }
}
