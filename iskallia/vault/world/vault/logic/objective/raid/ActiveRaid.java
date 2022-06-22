package iskallia.vault.world.vault.logic.objective.raid;

import iskallia.vault.config.RaidConfig;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.GlobalDifficultyData;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IEntityReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class ActiveRaid {
   private static final Random rand = new Random();
   private final BlockPos controller;
   private final AxisAlignedBB raidBox;
   private final RaidPreset preset;
   private int wave = -1;
   private int startDelay = 200;
   private final List<UUID> activeEntities = new ArrayList<>();
   private int totalWaveEntities = 0;
   private final List<UUID> participatingPlayers = new ArrayList<>();

   private ActiveRaid(BlockPos controller, AxisAlignedBB raidBox, RaidPreset preset) {
      this.controller = controller;
      this.raidBox = raidBox;
      this.preset = preset;
   }

   @Nullable
   public static ActiveRaid create(VaultRaid vault, ServerWorld world, BlockPos controller) {
      int raidIndex = vault.getProperties().getBaseOrDefault(VaultRaid.RAID_INDEX, 0);
      RaidPreset preset = vault.getProperties().exists(VaultRaid.PARENT) ? RaidPreset.randomFromFinalConfig(raidIndex) : RaidPreset.randomFromConfig();
      vault.getProperties().create(VaultRaid.RAID_INDEX, raidIndex + 1);
      if (preset == null) {
         return null;
      } else {
         VaultRoom room = vault.getGenerator().getPiecesAt(controller, VaultRoom.class).stream().findFirst().orElse(null);
         if (room == null) {
            return null;
         } else {
            AxisAlignedBB raidBox = AxisAlignedBB.func_216363_a(room.getBoundingBox());
            ActiveRaid raid = new ActiveRaid(controller, raidBox, preset);
            world.func_217357_a(PlayerEntity.class, raidBox).forEach(player -> raid.participatingPlayers.add(player.func_110124_au()));
            vault.getActiveObjective(RaidChallengeObjective.class).ifPresent(raidObjective -> raidObjective.onRaidStart(vault, world, raid, controller));
            raid.playSoundToPlayers(world, SoundEvents.field_191248_br, 1.0F, 0.7F);
            return raid;
         }
      }
   }

   public void tick(VaultRaid vault, ServerWorld world) {
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
               Entity e = world.func_217461_a(entityUid);
               if (!(e instanceof MobEntity)) {
                  return true;
               } else {
                  MobEntity mob = (MobEntity)e;
                  mob.func_110163_bv();
                  if (!vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
                     mob.func_184195_f(true);
                  }

                  if (!(mob.func_70638_az() instanceof PlayerEntity)) {
                     List<PlayerEntity> players = this.participatingPlayers
                        .stream()
                        .<PlayerEntity>map(world::func_217371_b)
                        .filter(Objects::nonNull)
                        .filter(playerx -> this.raidBox.func_186662_g(10.0).func_72318_a(playerx.func_213303_ch()))
                        .collect(Collectors.toList());
                     if (!players.isEmpty()) {
                        PlayerEntity player = MiscUtils.getRandomEntry(players, rand);
                        mob.func_70624_b(player);
                     }
                  }

                  return false;
               }
            }
         );
   }

   public void spawnWave(RaidPreset.CompoundWaveSpawn wave, VaultRaid vault, ServerWorld world) {
      int participantLevel = -1;

      for (PlayerEntity player : world.func_217357_a(PlayerEntity.class, this.getRaidBoundingBox())) {
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
               RaidConfig.MobPool pool;
               if (vault.getProperties().exists(VaultRaid.PARENT)) {
                  pool = ModConfigs.FINAL_RAID.getPool(spawn.getMobPool(), scalingLevel);
               } else {
                  pool = ModConfigs.RAID.getPool(spawn.getMobPool(), scalingLevel);
               }

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
                  if (!vault.getProperties().exists(VaultRaid.PARENT)) {
                     spawnCount = (int)(spawnCount * ModConfigs.RAID.getMobCountMultiplier(scalingLevel));
                  }

                  for (int i = 0; i < spawnCount; i++) {
                     String mobType = pool.getRandomMob();
                     EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobType));
                     if (type != null && type.func_200720_b()) {
                        Vector3f center = new Vector3f(
                           this.controller.func_177958_n() + 0.5F, this.controller.func_177956_o(), this.controller.func_177952_p() + 0.5F
                        );
                        Vector3f randomPos = MiscUtils.getRandomCirclePosition(center, new Vector3f(0.0F, 1.0F, 0.0F), 8.0F + rand.nextFloat() * 6.0F);
                        BlockPos spawnAt = MiscUtils.getEmptyNearby(
                              world, new BlockPos(randomPos.func_195899_a(), randomPos.func_195900_b(), randomPos.func_195902_c())
                           )
                           .orElse(BlockPos.field_177992_a);
                        if (!spawnAt.equals(BlockPos.field_177992_a)) {
                           Entity spawned = type.func_220331_a(world, null, null, spawnAt, SpawnReason.EVENT, true, false);
                           if (spawned instanceof MobEntity) {
                              GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.get(world).getVaultDifficulty();
                              MobEntity mob = (MobEntity)spawned;
                              this.processSpawnedMob(mob, vault, difficulty, scalingLevel);
                              this.activeEntities.add(mob.func_110124_au());
                           }
                        }
                     }
                  }
               }
            }
         );
      this.totalWaveEntities = this.activeEntities.size();
      this.playSoundToPlayers(world, SoundEvents.field_219690_jn, 64.0F, 1.0F);
   }

   private void processSpawnedMob(MobEntity mob, VaultRaid vault, GlobalDifficultyData.Difficulty difficulty, int level) {
      level += vault.getActiveObjective(RaidChallengeObjective.class)
         .map(
            raidObjective -> raidObjective.getModifiersOfType(MonsterLevelModifier.class)
               .entrySet()
               .stream()
               .mapToInt(entry -> entry.getKey().getLevelAdded(entry.getValue()))
               .sum()
         )
         .orElse(0);
      mob.func_110163_bv();
      EntityScaler.setScaledEquipment(mob, vault, difficulty, level, new Random(), EntityScaler.Type.MOB);
      EntityScaler.setScaled(mob);
      vault.getActiveObjective(RaidChallengeObjective.class)
         .ifPresent(raidObjective -> raidObjective.getAllModifiers().forEach((modifier, value) -> modifier.affectRaidMob(mob, value)));
   }

   public boolean isFinished() {
      return this.wave >= 0 && this.preset.getWave(this.wave) == null;
   }

   List<UUID> getActiveEntities() {
      return this.activeEntities;
   }

   public boolean isPlayerInRaid(PlayerEntity player) {
      return this.isPlayerInRaid(player.func_110124_au());
   }

   public boolean isPlayerInRaid(UUID playerId) {
      return this.participatingPlayers.contains(playerId);
   }

   public AxisAlignedBB getRaidBoundingBox() {
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

   public void finish(VaultRaid raid, ServerWorld world) {
      raid.getActiveObjective(RaidChallengeObjective.class).ifPresent(raidChallenge -> raidChallenge.onRaidFinish(raid, world, this, this.controller));
      this.playSoundToPlayers(world, SoundEvents.field_194228_if, 0.7F, 0.5F);
   }

   private void playSoundToPlayers(IEntityReader world, SoundEvent event, float volume, float pitch) {
      this.participatingPlayers
         .forEach(
            playerId -> {
               PlayerEntity player = world.func_217371_b(playerId);
               if (player instanceof ServerPlayerEntity) {
                  SPlaySoundEffectPacket pkt = new SPlaySoundEffectPacket(
                     event, SoundCategory.BLOCKS, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), volume, pitch
                  );
                  ((ServerPlayerEntity)player).field_71135_a.func_147359_a(pkt);
               }
            }
         );
   }

   public BlockPos getController() {
      return this.controller;
   }

   public void serialize(CompoundNBT tag) {
      tag.func_218657_a("pos", NBTHelper.serializeBlockPos(this.controller));
      tag.func_218657_a(
         "boxFrom", NBTHelper.serializeBlockPos(new BlockPos(this.raidBox.field_72340_a, this.raidBox.field_72338_b, this.raidBox.field_72339_c))
      );
      tag.func_218657_a("boxTo", NBTHelper.serializeBlockPos(new BlockPos(this.raidBox.field_72336_d, this.raidBox.field_72337_e, this.raidBox.field_72334_f)));
      tag.func_74768_a("wave", this.wave);
      tag.func_218657_a("waves", this.preset.serialize());
      tag.func_74768_a("startDelay", this.startDelay);
      tag.func_74768_a("totalWaveEntities", this.totalWaveEntities);
      NBTHelper.writeList(tag, "entities", this.activeEntities, StringNBT.class, uuid -> StringNBT.func_229705_a_(uuid.toString()));
      NBTHelper.writeList(tag, "players", this.participatingPlayers, StringNBT.class, uuid -> StringNBT.func_229705_a_(uuid.toString()));
   }

   public static ActiveRaid deserializeNBT(CompoundNBT nbt) {
      BlockPos controller = NBTHelper.deserializeBlockPos(nbt.func_74775_l("pos"));
      BlockPos from = NBTHelper.deserializeBlockPos(nbt.func_74775_l("boxFrom"));
      BlockPos to = NBTHelper.deserializeBlockPos(nbt.func_74775_l("boxTo"));
      RaidPreset waves = RaidPreset.deserialize(nbt.func_74775_l("waves"));
      ActiveRaid raid = new ActiveRaid(controller, new AxisAlignedBB(from, to), waves);
      raid.startDelay = nbt.func_74762_e("startDelay");
      raid.wave = nbt.func_74762_e("wave");
      raid.totalWaveEntities = nbt.func_74762_e("totalWaveEntities");
      raid.activeEntities.addAll(NBTHelper.readList(nbt, "entities", StringNBT.class, idString -> UUID.fromString(idString.func_150285_a_())));
      raid.participatingPlayers.addAll(NBTHelper.readList(nbt, "players", StringNBT.class, idString -> UUID.fromString(idString.func_150285_a_())));
      return raid;
   }
}
