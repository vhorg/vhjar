package iskallia.vault.world.raid;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.CrystalData;
import iskallia.vault.network.message.VaultInfoMessage;
import iskallia.vault.network.message.VaultRaidTickMessage;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultSetsData;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreCriteria.RenderType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class VaultRaid implements INBTSerializable<CompoundNBT> {
   public static final PortalPlacer PORTAL_PLACER = new PortalPlacer(
      (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.func_176223_P().func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
      (pos, random, facing) -> {
         Block[] blocks = new Block[]{
            Blocks.field_235406_np_, Blocks.field_235406_np_, Blocks.field_235410_nt_, Blocks.field_235411_nu_, Blocks.field_235412_nv_
         };
         return blocks[random.nextInt(blocks.length)].func_176223_P();
      }
   );
   public static final PortalPlacer FINAL_PORTAL_PLACER = new PortalPlacer(
      (pos, random, facing) -> (BlockState)ModBlocks.FINAL_VAULT_PORTAL.func_176223_P().func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
      (pos, random, facing) -> {
         Block[] blocks = new Block[]{
            Blocks.field_235406_np_, Blocks.field_235406_np_, Blocks.field_235410_nt_, Blocks.field_235411_nu_, Blocks.field_235412_nv_
         };
         return blocks[random.nextInt(blocks.length)].func_176223_P();
      }
   );
   public static final DamageSource VAULT_FAILED = new DamageSource("vaultFailed").func_76348_h().func_76359_i();
   public static final int REGION_SIZE = 2048;
   public List<UUID> playerIds;
   public List<UUID> spectatorIds;
   public List<VaultRaid.Spectator> spectators = new ArrayList<>();
   public List<UUID> bosses = new ArrayList<>();
   public MutableBoundingBox box;
   public int level;
   public int rarity;
   public int sTickLeft;
   public int ticksLeft;
   public String playerBossName;
   public BlockPos start;
   public Direction facing;
   public boolean won;
   public boolean cannotExit;
   public boolean summonedBoss;
   public VaultSpawner spawner = new VaultSpawner(this);
   public VaultModifiers modifiers = new VaultModifiers(this);
   public boolean finished = false;
   public int timer = 1200;
   public boolean isFinalVault;

   protected VaultRaid() {
   }

   public VaultRaid(List<ServerPlayerEntity> players, List<ServerPlayerEntity> spectators, MutableBoundingBox box, int level, int rarity, String playerBossName) {
      this.playerIds = players.stream().<UUID>map(Entity::func_110124_au).collect(Collectors.toList());
      this.spectatorIds = spectators.stream().<UUID>map(Entity::func_110124_au).collect(Collectors.toList());
      this.box = box;
      this.level = level;
      this.rarity = rarity;
      this.playerBossName = playerBossName;
      this.sTickLeft = ModConfigs.VAULT_TIMER.getForLevel(this.level);
      this.ticksLeft = this.sTickLeft;
      players.stream()
         .map(player -> VaultSetsData.get(player.func_71121_q()).getExtraTime(player.func_110124_au()))
         .max(Integer::compare)
         .ifPresent(extraTime -> {
            this.sTickLeft = this.sTickLeft + extraTime;
            this.ticksLeft = this.ticksLeft + extraTime;
         });
   }

   public List<UUID> getPlayerIds() {
      return this.playerIds;
   }

   public List<VaultRaid.Spectator> getSpectators() {
      return this.spectators;
   }

   public boolean isComplete() {
      return this.ticksLeft <= 0 || this.finished;
   }

   public void tick(ServerWorld world) {
      if (!this.finished) {
         if (!this.won && this.summonedBoss && this.bosses.isEmpty()) {
            this.won = true;
            this.ticksLeft = 400;
         }

         if (this.playerIds.size() == 1) {
            this.runForPlayers(world.func_73046_m(), player -> {
               this.modifiers.tick(world, player);
               this.ticksLeft--;
               this.syncTicksLeft(world.func_73046_m());
            });
         } else {
            this.ticksLeft--;
            this.syncTicksLeft(world.func_73046_m());
         }

         if (this.ticksLeft <= 0) {
            if (this.won) {
               this.onFinishRaid(world);
            } else {
               this.runForAll(world.func_73046_m(), player -> {
                  player.func_145747_a(new StringTextComponent("Time has run out!").func_240699_a_(TextFormatting.GREEN), player.func_110124_au());
                  player.field_71071_by.func_234564_a_(stack -> true, -1, player.field_71069_bz.func_234641_j_());
                  player.field_71070_bA.func_75142_b();
                  player.field_71069_bz.func_75130_a(player.field_71071_by);
                  player.func_71113_k();
                  player.func_70097_a(VAULT_FAILED, 1.0E8F);
               });
               this.onFinishRaid(world);
               this.finished = true;
            }
         } else {
            this.runForPlayers(world.func_73046_m(), player -> {
               if (this.ticksLeft + 20 >= this.sTickLeft || player.field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
                  this.spawner.tick(player);
               } else if (player.field_70170_p.func_234923_W_() == World.field_234918_g_) {
                  this.onFinishRaid(world);
               } else {
                  this.ticksLeft = 1;
               }
            });
         }

         this.timer--;
      }
   }

   private void onFinishRaid(ServerWorld world) {
      this.finished = true;
      this.runForAll(world.func_73046_m(), player -> {
         if (!player.field_70128_L && player.field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
            this.teleportToStart(world, player);
         }

         this.finish(world, player.func_110124_au());
         List<UUID> list = this.spectators.stream().map(spectator -> spectator.uuid).collect(Collectors.toList());
         if (!player.field_70128_L && !list.contains(player.func_110124_au())) {
            float range = ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MAX - ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MIN;
            float tnl = ModConfigs.VAULT_GENERAL.VAULT_EXIT_TNL_MIN + world.field_73012_v.nextFloat() * range;
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get(world);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            statsData.addVaultExp(player, (int)(stats.getTnl() * tnl));
         }
      });
      this.finishSpectators(world);
   }

   public void addBoss(LivingEntity entity) {
      this.bosses.add(entity.func_110124_au());
      this.summonedBoss = true;
   }

   private void finishSpectators(ServerWorld world) {
      this.spectators.forEach(spectator -> spectator.finish(world, this));
   }

   public void finish(ServerWorld server, UUID playerId) {
      Scoreboard scoreboard = server.func_96441_U();
      ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.field_96641_b, RenderType.INTEGER);
      scoreboard.func_178822_d(playerId.toString(), objective);
   }

   public static ScoreObjective getOrCreateObjective(Scoreboard scoreboard, String name, ScoreCriteria criteria, RenderType renderType) {
      if (!scoreboard.func_197897_d().contains(name)) {
         scoreboard.func_199868_a(name, criteria, new StringTextComponent(name), renderType);
      }

      return scoreboard.func_96518_b(name);
   }

   public void runForPlayers(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
      for (UUID uuid : this.playerIds) {
         if (server == null) {
            return;
         }

         ServerPlayerEntity player = server.func_184103_al().func_177451_a(uuid);
         if (player == null) {
            return;
         }

         action.accept(player);
      }
   }

   public void runForSpectators(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
      this.spectators.stream().map(spectator -> spectator.uuid).forEach(uuid -> {
         if (server != null) {
            ServerPlayerEntity player = server.func_184103_al().func_177451_a(uuid);
            if (player != null) {
               action.accept(player);
            }
         }
      });
   }

   public void runForAll(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
      this.runForPlayers(server, action);
      this.runForSpectators(server, action);
   }

   public void syncTicksLeft(MinecraftServer server) {
      this.runForAll(
         server,
         player -> ModNetwork.CHANNEL.sendTo(new VaultRaidTickMessage(this.ticksLeft), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      if (this.playerIds.size() == 1) {
         nbt.func_186854_a("PlayerId", this.playerIds.get(0));
      } else {
         ListNBT playerIdsList = new ListNBT();
         this.playerIds.forEach(uuid -> playerIdsList.add(NBTUtil.func_240626_a_(uuid)));
         nbt.func_218657_a("PlayerIds", playerIdsList);
      }

      nbt.func_218657_a("Box", this.box.func_151535_h());
      nbt.func_74768_a("Level", this.level);
      nbt.func_74768_a("Rarity", this.rarity);
      nbt.func_74768_a("StartTicksLeft", this.sTickLeft);
      nbt.func_74768_a("TicksLeft", this.ticksLeft);
      nbt.func_74778_a("PlayerBossName", this.playerBossName);
      nbt.func_74757_a("Won", this.won);
      nbt.func_74757_a("CannotExit", this.cannotExit);
      nbt.func_74757_a("SummonedBoss", this.summonedBoss);
      nbt.func_74768_a("Spawner.MaxMobs", this.spawner.maxMobs);
      nbt.func_218657_a("Modifiers", this.modifiers.serializeNBT());
      if (this.start != null) {
         CompoundNBT startNBT = new CompoundNBT();
         startNBT.func_74768_a("x", this.start.func_177958_n());
         startNBT.func_74768_a("y", this.start.func_177956_o());
         startNBT.func_74768_a("z", this.start.func_177952_p());
         nbt.func_218657_a("Start", startNBT);
      }

      ListNBT spectatorsList = new ListNBT();
      this.spectators.forEach(spectator -> spectatorsList.add(spectator.serializeNBT()));
      nbt.func_218657_a("Spectators", spectatorsList);
      ListNBT bossesList = new ListNBT();
      this.bosses.forEach(boss -> bossesList.add(StringNBT.func_229705_a_(boss.toString())));
      nbt.func_218657_a("Bosses", bossesList);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      if (nbt.func_74764_b("PlayerId")) {
         this.playerIds = Collections.singletonList(nbt.func_186857_a("PlayerId"));
      } else {
         ListNBT playerIdsList = nbt.func_150295_c("PlayerIds", 11);
         this.playerIds = playerIdsList.stream().<UUID>map(NBTUtil::func_186860_b).collect(Collectors.toList());
      }

      this.box = new MutableBoundingBox(nbt.func_74759_k("Box"));
      this.level = nbt.func_74762_e("Level");
      this.rarity = nbt.func_74762_e("Rarity");
      this.sTickLeft = nbt.func_74762_e("StartTicksLeft");
      this.ticksLeft = nbt.func_74762_e("TicksLeft");
      this.playerBossName = nbt.func_74779_i("PlayerBossName");
      this.won = nbt.func_74767_n("Won");
      this.cannotExit = nbt.func_74767_n("CannotExit");
      this.summonedBoss = nbt.func_74767_n("SummonedBoss");
      this.spawner.maxMobs = nbt.func_74762_e("Spawner.MaxMobs");
      this.modifiers.deserializeNBT(nbt.func_74775_l("Modifiers"));
      if (nbt.func_150297_b("Start", 10)) {
         CompoundNBT startNBT = nbt.func_74775_l("Start");
         this.start = new BlockPos(startNBT.func_74762_e("x"), startNBT.func_74762_e("y"), startNBT.func_74762_e("z"));
      }

      this.spectators.clear();
      ListNBT spectatorsList = nbt.func_150295_c("Spectators", 10);
      spectatorsList.stream().map(inbt -> (CompoundNBT)inbt).map(VaultRaid.Spectator::fromNBT).forEach(this.spectators::add);
      this.bosses.clear();
      ListNBT bossesList = nbt.func_150295_c("Bosses", 8);
      bossesList.stream().map(inbt -> ((StringNBT)inbt).func_150285_a_()).map(UUID::fromString).forEach(this.bosses::add);
   }

   public static VaultRaid fromNBT(CompoundNBT nbt) {
      VaultRaid raid = new VaultRaid();
      raid.deserializeNBT(nbt);
      return raid;
   }

   public void teleportToStart(ServerWorld world, ServerPlayerEntity player) {
      if (this.start == null) {
         Vault.LOGGER.warn("No vault start was found.");
         player.func_200619_a(
            world,
            this.box.field_78897_a + this.box.func_78883_b() / 2.0F,
            256.0,
            this.box.field_78896_c + this.box.func_78880_d() / 2.0F,
            player.field_70177_z,
            player.field_70125_A
         );
      } else {
         player.func_200619_a(
            world,
            this.start.func_177958_n() + 0.5,
            this.start.func_177956_o() + 0.2,
            this.start.func_177952_p() + 0.5,
            this.facing == null ? world.func_201674_k().nextFloat() * 360.0F : this.facing.func_176746_e().func_185119_l(),
            0.0F
         );
         player.func_230245_c_(true);
      }
   }

   public void start(ServerWorld world, ChunkPos chunkPos, CrystalData data) {
      this.spawner.init();

      label57:
      for (int x = -48; x < 48; x++) {
         for (int z = -48; z < 48; z++) {
            for (int y = 0; y < 48; y++) {
               BlockPos pos = chunkPos.func_206849_h().func_177982_a(x, 128 + y, z);
               if (world.func_180495_p(pos).func_177230_c() == Blocks.field_235348_mG_) {
                  world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
                  this.start = pos;

                  for (Direction direction : Plane.HORIZONTAL) {
                     int count;
                     for (count = 1; world.func_180495_p(pos.func_177967_a(direction, count)).func_177230_c() == Blocks.field_235349_mH_; count++) {
                        world.func_175656_a(pos.func_177967_a(direction, count), Blocks.field_150350_a.func_176223_P());
                     }

                     if (count != 1) {
                        (this.isFinalVault ? FINAL_PORTAL_PLACER : PORTAL_PLACER).place(world, pos, this.facing = direction, count, count + 1);
                        break label57;
                     }
                  }
               }
            }
         }
      }

      this.spectatorIds.forEach(uuid -> {
         ServerPlayerEntity player = world.func_73046_m().func_184103_al().func_177451_a(uuid);
         if (player != null) {
            this.addSpectator(player);
         }
      });
      this.runForAll(
         world.func_73046_m(),
         player -> {
            this.teleportToStart(world, player);
            player.func_242279_ag();
            Scoreboard scoreboard = player.func_96123_co();
            ScoreObjective objective = getOrCreateObjective(scoreboard, "VaultRarity", ScoreCriteria.field_96641_b, RenderType.INTEGER);
            scoreboard.func_96529_a(player.func_200200_C_().getString(), objective).func_96647_c(this.rarity);
            long seconds = this.ticksLeft / 20 % 60;
            long minutes = this.ticksLeft / 20 / 60 % 60;
            String duration = String.format("%02d:%02d", minutes, seconds);
            StringTextComponent title = new StringTextComponent("The Vault");
            title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
            IFormattableTextComponent subtitle = this.cannotExit
               ? new StringTextComponent("No exit this time, ").func_230529_a_(player.func_200200_C_()).func_230529_a_(new StringTextComponent("!"))
               : new StringTextComponent("Good luck, ").func_230529_a_(player.func_200200_C_()).func_230529_a_(new StringTextComponent("!"));
            subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
            StringTextComponent actionBar = new StringTextComponent("You have " + duration + " minutes to complete the raid.");
            actionBar.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
            STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
            STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, subtitle);
            player.field_71135_a.func_147359_a(titlePacket);
            player.field_71135_a.func_147359_a(subtitlePacket);
            player.func_146105_b(actionBar, true);
            this.modifiers.generate(world.func_201674_k(), this.level, this.playerBossName != null && !this.playerBossName.isEmpty());
            data.apply(this, world.func_201674_k());
            this.modifiers.apply();
            ModNetwork.CHANNEL.sendTo(new VaultInfoMessage(this), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
            StringTextComponent text = new StringTextComponent("");
            AtomicBoolean startsWithVowel = new AtomicBoolean(false);
            this.modifiers
               .forEach(
                  (i, modifier) -> {
                     StringTextComponent s = new StringTextComponent(modifier.getName());
                     s.func_230530_a_(
                        Style.field_240709_b_
                           .func_240718_a_(Color.func_240743_a_(modifier.getColor()))
                           .func_240716_a_(new HoverEvent(Action.field_230550_a_, new StringTextComponent(modifier.getDescription())))
                     );
                     text.func_230529_a_(s);
                     if (i == 0) {
                        char c = modifier.getName().toLowerCase().charAt(0);
                        startsWithVowel.set(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
                     }

                     if (i != this.modifiers.size() - 1) {
                        text.func_230529_a_(new StringTextComponent(", "));
                     }
                  }
               );
            StringTextComponent prefix = new StringTextComponent(startsWithVowel.get() ? " entered an " : " entered a ");
            if (this.modifiers.size() != 0) {
               text.func_230529_a_(new StringTextComponent(" "));
            }

            String rarityName = VaultRarity.values()[this.rarity].name().toLowerCase();
            rarityName = rarityName.substring(0, 1).toUpperCase() + rarityName.substring(1);
            text.func_230529_a_(new StringTextComponent(rarityName).func_240699_a_(VaultRarity.values()[this.rarity].color));
            text.func_230529_a_(new StringTextComponent(" Vault!"));
            prefix.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
            text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
            IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
            playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
            world.func_73046_m()
               .func_184103_al()
               .func_232641_a_(playerName.func_230529_a_(prefix).func_230529_a_(text), ChatType.CHAT, player.func_110124_au());
            Advancement advancement = player.func_184102_h().func_191949_aK().func_192778_a(Vault.id("root"));
            player.func_192039_O().func_192750_a(advancement, "entered_vault");
         }
      );
   }

   public void addSpectator(ServerPlayerEntity player) {
      this.getPlayerIds().remove(player.func_110124_au());
      VaultRaid.Spectator spectator = new VaultRaid.Spectator();
      spectator.uuid = player.func_110124_au();
      spectator.oldGameType = player.field_71134_c.func_73081_b();
      player.func_71033_a(GameType.SPECTATOR);
      if (player.field_70170_p.func_234923_W_() != Vault.VAULT_KEY) {
         this.teleportToStart(player.func_184102_h().func_71218_a(Vault.VAULT_KEY), player);
      }
   }

   public static class Spectator implements INBTSerializable<CompoundNBT> {
      public GameType oldGameType;
      public UUID uuid;

      public void finish(ServerWorld world, VaultRaid raid) {
         NetcodeUtils.runIfPresent(world.func_73046_m(), this.uuid, player -> {
            player.func_71033_a(this.oldGameType);
            raid.teleportToStart(world, player);
         });
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74768_a("GameType", this.oldGameType.ordinal());
         nbt.func_186854_a("PlayerId", this.uuid);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.oldGameType = GameType.values()[nbt.func_74762_e("GameType")];
         this.uuid = nbt.func_186857_a("PlayerId");
      }

      public static VaultRaid.Spectator fromNBT(CompoundNBT nbt) {
         VaultRaid.Spectator spectator = new VaultRaid.Spectator();
         spectator.deserializeNBT(nbt);
         return spectator;
      }
   }
}
