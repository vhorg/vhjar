package iskallia.vault.world.vault.logic.objective.raid;

import com.mojang.math.Vector3f;
import iskallia.vault.config.RaidConfig;
import iskallia.vault.entity.LegacyEntityScaler;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterAmountModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.MonsterLevelModifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class ActiveRaid {
   private static final Random rand = new Random();
   private final BlockPos controller;
   private final AABB raidBox;
   private final RaidPreset preset;
   private int wave = -1;
   private int startDelay = 200;
   private final List<UUID> activeEntities = new ArrayList<>();
   private int totalWaveEntities = 0;
   private final List<UUID> participatingPlayers = new ArrayList<>();

   private ActiveRaid(BlockPos controller, AABB raidBox, RaidPreset preset) {
      this.controller = controller;
      this.raidBox = raidBox;
      this.preset = preset;
   }

   @Nullable
   public static ActiveRaid create(VaultRaid vault, ServerLevel world, BlockPos controller) {
      RaidPreset preset = RaidPreset.randomFromConfig();
      if (preset == null) {
         return null;
      } else {
         VaultRoom room = vault.getGenerator().getPiecesAt(controller, VaultRoom.class).stream().findFirst().orElse(null);
         if (room == null) {
            return null;
         } else {
            AABB raidBox = AABB.of(room.getBoundingBox());
            ActiveRaid raid = new ActiveRaid(controller, raidBox, preset);
            world.getEntitiesOfClass(Player.class, raidBox).forEach(player -> raid.participatingPlayers.add(player.getUUID()));
            vault.getActiveObjective(RaidChallengeObjective.class).ifPresent(raidObjective -> raidObjective.onRaidStart(vault, world, raid, controller));
            raid.playSoundToPlayers(world, SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 0.7F);
            return raid;
         }
      }
   }

   public void tick(VaultRaid vault, ServerLevel world) {
      if (this.activeEntities.isEmpty() && this.startDelay <= 0) {
         this.wave++;
         RaidPreset.CompoundWaveSpawn wave = this.preset.getWave(this.wave);
         if (wave != null) {
            this.spawnWave(wave, vault, world);
         }
      }

      if (this.startDelay > 0) {
         this.startDelay--;
      }

      this.activeEntities
         .removeIf(
            entityUid -> {
               if (!(world.getEntity(entityUid) instanceof Mob mob)) {
                  return true;
               } else {
                  mob.setPersistenceRequired();
                  if (!vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
                     mob.setGlowingTag(true);
                  }

                  if (!(mob.getTarget() instanceof Player)) {
                     List<Player> players = this.participatingPlayers
                        .stream()
                        .<Player>map(world::getPlayerByUUID)
                        .filter(Objects::nonNull)
                        .filter(playerx -> this.raidBox.inflate(10.0).contains(playerx.position()))
                        .collect(Collectors.toList());
                     if (!players.isEmpty()) {
                        Player player = MiscUtils.getRandomEntry(players, rand);
                        mob.setTarget(player);
                     }
                  }

                  return false;
               }
            }
         );
   }

   public void spawnWave(RaidPreset.CompoundWaveSpawn wave, VaultRaid vault, ServerLevel world) {
      int participantLevel = -1;

      for (Player player : world.getEntitiesOfClass(Player.class, this.getRaidBoundingBox())) {
         int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
         if (participantLevel == -1) {
            participantLevel = playerLevel;
         } else if (participantLevel > playerLevel) {
            participantLevel = playerLevel;
         }
      }

      if (participantLevel == -1) {
         participantLevel = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      }

      int scalingLevel = participantLevel;
      int playerCount = this.participatingPlayers.size();
      wave.getWaveSpawns()
         .forEach(
            spawn -> {
               RaidConfig.MobPool pool = ModConfigs.RAID_CONFIG.getPool(spawn.getMobPool(), scalingLevel);
               if (pool != null) {
                  int spawnCount = spawn.getMobCount();
                  spawnCount = (int)(
                     spawnCount
                        * (
                           1.0
                              + vault.getActiveObjective(RaidChallengeObjective.class)
                                 .map(
                                    raidObjective -> raidObjective.getModifiersOfType(MonsterAmountModifier.class)
                                       .values()
                                       .stream()
                                       .mapToDouble(Float::doubleValue)
                                       .sum()
                                 )
                                 .orElse(0.0)
                        )
                        * playerCount
                  );
                  spawnCount = (int)(spawnCount * ModConfigs.RAID_CONFIG.getMobCountMultiplier(scalingLevel));

                  for (int i = 0; i < spawnCount; i++) {
                     String mobType = pool.getRandomMob();
                     EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobType));
                     if (type != null && type.canSummon()) {
                        Vector3f center = new Vector3f(this.controller.getX() + 0.5F, this.controller.getY(), this.controller.getZ() + 0.5F);
                        Vector3f randomPos = MiscUtils.getRandomCirclePosition(center, new Vector3f(0.0F, 1.0F, 0.0F), 8.0F + rand.nextFloat() * 6.0F);
                        BlockPos spawnAt = MiscUtils.getEmptyNearby(world, new BlockPos(randomPos.x(), randomPos.y(), randomPos.z())).orElse(BlockPos.ZERO);
                        if (!spawnAt.equals(BlockPos.ZERO) && type.spawn(world, null, null, spawnAt, MobSpawnType.EVENT, true, false) instanceof Mob mob) {
                           this.processSpawnedMob(mob, vault, scalingLevel);
                           this.activeEntities.add(mob.getUUID());
                        }
                     }
                  }
               }
            }
         );
      this.totalWaveEntities = this.activeEntities.size();
      this.playSoundToPlayers(world, SoundEvents.RAID_HORN, 64.0F, 1.0F);
   }

   private void processSpawnedMob(Mob mob, VaultRaid vault, int level) {
      level += vault.getActiveObjective(RaidChallengeObjective.class)
         .map(
            raidObjective -> raidObjective.getModifiersOfType(MonsterLevelModifier.class)
               .entrySet()
               .stream()
               .mapToInt(entry -> entry.getKey().getLevelAdded(entry.getValue()))
               .sum()
         )
         .orElse(0);
      mob.setPersistenceRequired();
      LegacyEntityScaler.setScaledEquipmentLegacy(mob, vault, level, new Random(), LegacyEntityScaler.Type.MOB);
      LegacyEntityScaler.setScaled(mob);
      vault.getActiveObjective(RaidChallengeObjective.class)
         .ifPresent(raidObjective -> raidObjective.getAllModifiers().forEach((modifier, value) -> modifier.affectRaidMob(mob, value)));
   }

   public boolean isFinished() {
      return this.wave >= 0 && this.preset.getWave(this.wave) == null;
   }

   List<UUID> getActiveEntities() {
      return this.activeEntities;
   }

   public boolean isPlayerInRaid(Player player) {
      return this.isPlayerInRaid(player.getUUID());
   }

   public boolean isPlayerInRaid(UUID playerId) {
      return this.participatingPlayers.contains(playerId);
   }

   public AABB getRaidBoundingBox() {
      return this.raidBox;
   }

   public int getWave() {
      return this.wave;
   }

   public int getTotalWaves() {
      return this.preset.getWaves();
   }

   public int getAliveEntities() {
      return this.activeEntities.size();
   }

   public int getTotalWaveEntities() {
      return this.totalWaveEntities;
   }

   public int getStartDelay() {
      return this.startDelay;
   }

   void setStartDelay(int startDelay) {
      this.startDelay = startDelay;
   }

   boolean hasNextWave() {
      return this.preset.getWave(this.wave + 1) != null;
   }

   public void finish(VaultRaid raid, ServerLevel world) {
      raid.getActiveObjective(RaidChallengeObjective.class).ifPresent(raidChallenge -> raidChallenge.onRaidFinish(raid, world, this, this.controller));
      this.playSoundToPlayers(world, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.7F, 0.5F);
   }

   private void playSoundToPlayers(EntityGetter world, SoundEvent event, float volume, float pitch) {
      this.participatingPlayers.forEach(playerId -> {
         Player player = world.getPlayerByUUID(playerId);
         if (player instanceof ServerPlayer) {
            ClientboundSoundPacket pkt = new ClientboundSoundPacket(event, SoundSource.BLOCKS, player.getX(), player.getY(), player.getZ(), volume, pitch);
            ((ServerPlayer)player).connection.send(pkt);
         }
      });
   }

   public BlockPos getController() {
      return this.controller;
   }

   public void serialize(CompoundTag tag) {
      tag.put("pos", NBTHelper.serializeBlockPos(this.controller));
      tag.put("boxFrom", NBTHelper.serializeBlockPos(new BlockPos(this.raidBox.minX, this.raidBox.minY, this.raidBox.minZ)));
      tag.put("boxTo", NBTHelper.serializeBlockPos(new BlockPos(this.raidBox.maxX, this.raidBox.maxY, this.raidBox.maxZ)));
      tag.putInt("wave", this.wave);
      tag.put("waves", this.preset.serialize());
      tag.putInt("startDelay", this.startDelay);
      tag.putInt("totalWaveEntities", this.totalWaveEntities);
      NBTHelper.writeCollection(tag, "entities", this.activeEntities, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
      NBTHelper.writeCollection(tag, "players", this.participatingPlayers, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
   }

   public static ActiveRaid deserializeNBT(CompoundTag nbt) {
      BlockPos controller = NBTHelper.deserializeBlockPos(nbt.getCompound("pos"));
      BlockPos from = NBTHelper.deserializeBlockPos(nbt.getCompound("boxFrom"));
      BlockPos to = NBTHelper.deserializeBlockPos(nbt.getCompound("boxTo"));
      RaidPreset waves = RaidPreset.deserialize(nbt.getCompound("waves"));
      ActiveRaid raid = new ActiveRaid(controller, new AABB(from, to), waves);
      raid.startDelay = nbt.getInt("startDelay");
      raid.wave = nbt.getInt("wave");
      raid.totalWaveEntities = nbt.getInt("totalWaveEntities");
      raid.activeEntities.addAll(NBTHelper.readList(nbt, "entities", StringTag.class, idString -> UUID.fromString(idString.getAsString())));
      raid.participatingPlayers.addAll(NBTHelper.readList(nbt, "players", StringTag.class, idString -> UUID.fromString(idString.getAsString())));
      return raid;
   }
}
