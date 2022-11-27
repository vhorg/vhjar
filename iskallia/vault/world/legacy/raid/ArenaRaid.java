package iskallia.vault.world.legacy.raid;

import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.entity.entity.ArenaBossEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.ArenaRaidData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.StreamData;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class ArenaRaid implements INBTSerializable<CompoundTag> {
   public static final int REGION_SIZE = 2048;
   private UUID playerId;
   public BoundingBox box;
   private boolean isComplete;
   public BlockPos start;
   public ArenaSpawner spawner = new ArenaSpawner(this, ModConfigs.ARENA_GENERAL.BOSS_COUNT);
   public ArenaScoreboard scoreboard = new ArenaScoreboard(this);
   public ReturnInfo returnInfo = new ReturnInfo();
   private int time = 0;
   private int endDelay = 300;
   private int checkCooldown = 20;

   protected ArenaRaid() {
   }

   public ArenaRaid(UUID playerId, BoundingBox box) {
      this.playerId = playerId;
      this.box = box;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public boolean isComplete() {
      return this.isComplete && this.endDelay < 0;
   }

   public BlockPos getCenter() {
      return this.start;
   }

   public void tick(ServerLevel world) {
      if (this.start != null) {
         if (this.isComplete) {
            this.endDelay--;
         } else {
            this.time++;
            this.runIfPresent(
               world,
               player -> ModNetwork.CHANNEL
                  .sendTo(
                     VaultOverlayMessage.forArena(ModConfigs.ARENA_GENERAL.TICK_COUNTER - this.time),
                     player.connection.connection,
                     NetworkDirection.PLAY_TO_CLIENT
                  )
            );
            if (this.checkCooldown-- <= 0) {
               if (this.spawner.hasStarted()) {
                  if (this.time > ModConfigs.ARENA_GENERAL.TICK_COUNTER) {
                     this.spawner
                        .fighters
                        .stream()
                        .<Entity>map(world::getEntity)
                        .filter(Objects::nonNull)
                        .forEach(entity -> entity.setRemoved(RemovalReason.DISCARDED));
                  }

                  boolean bossLeft = this.spawner.bosses.stream().<Entity>map(world::getEntity).anyMatch(entity -> entity instanceof ArenaBossEntity);
                  boolean fighterLeft = true;
                  if (!bossLeft) {
                     this.onFighterWin(world);
                     this.runIfPresent(
                        world, player -> ModNetwork.CHANNEL.sendTo(VaultOverlayMessage.hide(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
                     );
                     this.isComplete = true;
                  } else if (!fighterLeft) {
                     this.onBossWin(world);
                     this.runIfPresent(
                        world, player -> ModNetwork.CHANNEL.sendTo(VaultOverlayMessage.hide(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
                     );
                     this.isComplete = true;
                  }
               } else if (this.time > 100) {
                  this.spawner.start(world);
               }
            }
         }
      }
   }

   private void onBossWin(ServerLevel world) {
      this.runIfPresent(world, playerEntity -> {
         TextComponent title = new TextComponent("You Lost");
         title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         MutableComponent subtitle = new TextComponent("F");
         subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         TextComponent actionBar;
         if (this.time > ModConfigs.ARENA_GENERAL.TICK_COUNTER) {
            actionBar = new TextComponent("Ran out of time.");
         } else {
            actionBar = new TextComponent("No subscribers left standing.");
         }

         actionBar.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
         ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
         playerEntity.connection.send(titlePacket);
         playerEntity.connection.send(subtitlePacket);
         playerEntity.displayClientMessage(actionBar, true);
      });
   }

   private void onFighterWin(ServerLevel world) {
      this.runIfPresent(
         world,
         playerEntity -> {
            TextComponent title = new TextComponent("You Win");
            title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
            MutableComponent subtitle = new TextComponent("GG");
            subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9563710)));
            TextComponent actionBar = new TextComponent(
               "With " + this.spawner.fighters.stream().<Entity>map(world::getEntity).filter(Objects::nonNull).count() + " subscribers left."
            );
            actionBar.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9563710)));
            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
            ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
            playerEntity.connection.send(titlePacket);
            playerEntity.connection.send(subtitlePacket);
            playerEntity.displayClientMessage(actionBar, true);
         }
      );

      for (int i = 0; i < 64; i++) {
         FireworkRocketEntity firework = new FireworkRocketEntity(
            world,
            this.getCenter().getX() + world.getRandom().nextInt(81) - 40,
            this.getCenter().getY() - 15,
            this.getCenter().getZ() + world.getRandom().nextInt(81) - 40,
            new ItemStack(Items.FIREWORK_ROCKET)
         );
         world.addWithUUID(firework);
      }

      this.scheduleLoot(world);
   }

   private void scheduleLoot(ServerLevel world) {
      this.runIfPresent(
         world,
         playerEntity -> {
            Builder builder = new Builder(world).withRandom(world.random).withLuck(playerEntity.getLuck());
            LootContext ctx = builder.create(LootContextParamSets.EMPTY);
            NonNullList<ItemStack> stacks = NonNullList.create();
            stacks.add(
               LootStatueBlockItem.getStatueBlockItem(
                  this.scoreboard
                     .get()
                     .entrySet()
                     .stream()
                     .sorted((o1, o2) -> Float.compare(o2.getValue(), o1.getValue()))
                     .map(Entry::getKey)
                     .findFirst()
                     .orElse("")
               )
            );
            int level = PlayerVaultStatsData.get(world).getVaultStats(playerEntity).getVaultLevel();
            LootTable rewardLootTable = world.getServer().getLootTables().get(ModConfigs.LOOT_TABLES.getForLevel(level).getArenaCrate());
            stacks.addAll(rewardLootTable.getRandomItems(ctx));
            if (ModConfigs.RAID_EVENT_CONFIG.isEnabled()) {
               ItemStack eventSeal = new ItemStack(ModItems.CRYSTAL_SEAL_RAID);
               ItemVaultCrystalSeal.setEventKey(eventSeal, "raid");
               stacks.add(eventSeal);
            }

            ItemStack crateStack = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.ARENA, stacks);
            ArenaRaidData.get(world).scheduleCrateToGive(playerEntity, crateStack);
         }
      );
   }

   public void finish(ServerLevel world, ServerPlayer player) {
      if (player != null) {
         this.returnInfo.apply(world.getServer(), player);
         StreamData.get(world).onArenaLeave(world.getServer(), this.playerId);
      }
   }

   public boolean runIfPresent(ServerLevel world, Consumer<ServerPlayer> action) {
      if (world == null) {
         return false;
      } else {
         ServerPlayer player = (ServerPlayer)world.getPlayerByUUID(this.playerId);
         if (player == null) {
            return false;
         } else {
            action.accept(player);
            return true;
         }
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putUUID("PlayerId", this.playerId);
      nbt.put("Box", NBTHelper.serializeBoundingBox(this.box));
      nbt.putBoolean("Completed", this.isComplete());
      nbt.putInt("Time", this.time);
      nbt.putInt("EndDelay", this.endDelay);
      if (this.start != null) {
         CompoundTag startNBT = new CompoundTag();
         startNBT.putInt("x", this.start.getX());
         startNBT.putInt("y", this.start.getY());
         startNBT.putInt("z", this.start.getZ());
         nbt.put("Start", startNBT);
      }

      nbt.put("Scoreboard", this.scoreboard.serializeNBT());
      nbt.put("ReturnInfo", this.returnInfo.serializeNBT());
      nbt.put("Spawner", this.spawner.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.playerId = nbt.getUUID("PlayerId");
      this.box = NBTHelper.deserializeBoundingBox(nbt.getIntArray("Box"));
      this.isComplete = nbt.getBoolean("Completed");
      this.time = nbt.getInt("Time");
      this.endDelay = nbt.getInt("EndDelay");
      if (nbt.contains("Start", 10)) {
         CompoundTag startNBT = nbt.getCompound("Start");
         this.start = new BlockPos(startNBT.getInt("x"), startNBT.getInt("y"), startNBT.getInt("z"));
      }

      if (nbt.contains("Scoreboard", 10)) {
         this.scoreboard.deserializeNBT(nbt.getCompound("Scoreboard"));
      }

      if (nbt.contains("ReturnInfo", 10)) {
         this.returnInfo.deserializeNBT(nbt.getCompound("ReturnInfo"));
      }

      if (nbt.contains("Spawner", 10)) {
         this.spawner.deserializeNBT(nbt.getCompound("Spawner"));
      }
   }

   public static ArenaRaid fromNBT(CompoundTag nbt) {
      ArenaRaid raid = new ArenaRaid();
      raid.deserializeNBT(nbt);
      return raid;
   }

   public void teleportToStart(ServerLevel world, ServerPlayer player) {
      if (this.start == null) {
         VaultMod.LOGGER.warn("No arena start was found.");
         player.teleportTo(
            world, this.box.minX() + this.box.getXSpan() / 2.0F, 256.0, this.box.minZ() + this.box.getZSpan() / 2.0F, player.getYRot(), player.getXRot()
         );
      } else {
         player.teleportTo(world, this.start.getX() + 0.5, this.start.getY() + 0.2, this.start.getZ() + 0.5, world.getRandom().nextFloat() * 360.0F, 0.0F);
         player.setOnGround(true);
      }
   }

   public void start(ServerLevel world, ServerPlayer player, ChunkPos chunkPos) {
      this.returnInfo = new ReturnInfo(player);

      label73:
      for (int x = -48; x < 48; x++) {
         for (int z = -48; z < 48; z++) {
            for (int y = 0; y < 48; y++) {
               BlockPos pos = chunkPos.getWorldPosition().offset(x, 32 + y, z);
               if (world.getBlockState(pos).getBlock() == Blocks.CRIMSON_PRESSURE_PLATE) {
                  world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                  this.start = pos;
                  break label73;
               }
            }
         }
      }

      if (this.start != null) {
         for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
               for (int yx = -4; yx <= 4; yx++) {
                  world.setBlockAndUpdate(this.start.offset(x, yx, z), Blocks.AIR.defaultBlockState());
               }
            }
         }

         for (int i = 0; i < this.start.getY() && !world.getBlockState(this.start.below(i)).canOcclude(); i++) {
            world.setBlockAndUpdate(this.start.below(i), Blocks.AIR.defaultBlockState());
         }
      }

      this.teleportToStart(world, player);
      player.setPortalCooldown();
      player.setGameMode(GameType.SPECTATOR);
      this.runIfPresent(world, playerEntity -> {
         TextComponent title = new TextComponent("The Arena");
         title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9563710)));
         MutableComponent subtitle = new TextComponent("Let the fight begin!");
         subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9563710)));
         TextComponent actionBar = new TextComponent("You have " + this.spawner.getFighterCount() + " subscribers on your side.");
         actionBar.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9563710)));
         ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
         ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
         playerEntity.connection.send(titlePacket);
         playerEntity.connection.send(subtitlePacket);
         playerEntity.displayClientMessage(actionBar, true);
      });
   }
}
