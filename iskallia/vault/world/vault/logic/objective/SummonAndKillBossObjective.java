package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.network.NetworkDirection;

public class SummonAndKillBossObjective extends VaultObjective {
   protected int progressCount;
   protected int targetCount = rand.nextInt(4) + 3;
   protected UUID bossId = null;
   protected Component bossName = null;
   protected Vec3 bossPos = null;
   protected boolean isBossDead = false;

   public SummonAndKillBossObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   public boolean allObelisksClicked() {
      return this.progressCount >= this.targetCount;
   }

   public void addObelisk() {
      this.progressCount++;
   }

   public UUID getBossId() {
      return this.bossId;
   }

   public boolean isBossDead() {
      return this.isBossDead;
   }

   public boolean isBossSpawned() {
      return this.bossId != null;
   }

   public Component getBossName() {
      return this.bossName;
   }

   public Vec3 getBossPos() {
      return this.bossPos;
   }

   public void setBoss(LivingEntity boss) {
      this.bossId = boss.getUUID();
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.targetCount = amount;
   }

   @Nullable
   @Override
   public Component getObjectiveTargetDescription(int amount) {
      return new TextComponent("Required Obelisks: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.GOLD));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return ModBlocks.OBELISK.defaultBlockState();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return null;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Kill the Boss").withStyle(ChatFormatting.GOLD);
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      if (!this.isCompleted()) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> {
                  vPlayer.runIfPresent(
                     world.getServer(),
                     playerEntity -> {
                        VaultGoalMessage pkt = this.allObelisksClicked()
                           ? VaultGoalMessage.killBossGoal()
                           : VaultGoalMessage.obeliskGoal(this.progressCount, this.targetCount);
                        ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                     }
                  );
                  if (this.isBossSpawned()) {
                     vPlayer.sendIfPresent(world.getServer(), new BossMusicMessage(true));
                  }
               }
            );
         if (this.isBossDead) {
            this.setCompleted();
         }
      }
   }

   @Override
   public void complete(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      super.complete(vault, player, world);
      player.sendIfPresent(world.getServer(), new BossMusicMessage(false));
      player.sendIfPresent(world.getServer(), VaultGoalMessage.clear());
   }

   @Override
   public void complete(VaultRaid vault, ServerLevel world) {
      super.complete(vault, world);
      vault.getPlayers().forEach(player -> {
         player.sendIfPresent(world.getServer(), new BossMusicMessage(false));
         player.sendIfPresent(world.getServer(), VaultGoalMessage.clear());
      });
   }

   public void spawnBossLoot(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      player.runIfPresent(
         world.getServer(),
         playerEntity -> {
            Builder builder = new Builder(world)
               .withRandom(world.random)
               .withParameter(LootContextParams.THIS_ENTITY, playerEntity)
               .withParameter(LootContextParams.ORIGIN, this.getBossPos())
               .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.playerAttack(playerEntity))
               .withOptionalParameter(LootContextParams.KILLER_ENTITY, playerEntity)
               .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, playerEntity)
               .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, playerEntity)
               .withLuck(playerEntity.getLuck());
            LootContext ctx = builder.create(LootContextParamSets.ENTITY);
            this.dropBossCrate(world, vault, player, ctx);

            for (int i = 1; i < vault.getPlayers().size(); i++) {
               if (rand.nextFloat() < 0.5F) {
                  this.dropBossCrate(world, vault, player, ctx);
               }
            }

            world.getServer().getPlayerList().broadcastMessage(this.getBossKillMessage(playerEntity), ChatType.CHAT, player.getPlayerId());
         }
      );
   }

   private Component getBossKillMessage(Player player) {
      MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
      MutableComponent playerName = player.getDisplayName().copy();
      playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      return msgContainer.append(playerName).append(" defeated ").append(this.getBossName()).append("!");
   }

   private void dropBossCrate(ServerLevel world, VaultRaid vault, VaultPlayer rewardPlayer, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      vault.getProperties()
         .getBase(VaultRaid.IS_RAFFLE)
         .ifPresent(
            isRaffle -> {
               if (isRaffle) {
                  vault.getPlayers()
                     .stream()
                     .filter(player -> player instanceof VaultRunner)
                     .min(Comparator.comparing(vPlayer -> vPlayer.getTimer().getTimeLeft()))
                     .ifPresent(vPlayer -> {
                        VaultTimer timer = vPlayer.getTimer();
                        PlayerVaultStatsData data = PlayerVaultStatsData.get(world);
                        if (timer.getRunTime() < data.getFastestVaultTime().getTickCount()) {
                           vPlayer.runIfPresent(world.getServer(), sPlayer -> data.updateFastestVaultTime(sPlayer, timer.getRunTime()));
                        }
                     });
               }
            }
         );
      BlockPos dropPos = rewardPlayer.getServerPlayer(world.getServer()).<BlockPos>map(Entity::blockPosition).orElse(new BlockPos(this.getBossPos()));
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOSS, stacks);
      ItemEntity item = new ItemEntity(world, dropPos.getX(), dropPos.getY(), dropPos.getZ(), crate);
      item.setDefaultPickUpDelay();
      world.addFreshEntity(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   protected void onBossDeath(LivingDeathEvent event, VaultRaid vault, ServerLevel world, boolean dropCrate) {
      MinecraftServer srv = world.getServer();
      LivingEntity boss = event.getEntityLiving();
      if (boss.getUUID().equals(this.getBossId())) {
         this.bossName = boss.getCustomName();
         this.bossPos = boss.position();
         this.isBossDead = true;
         if (dropCrate) {
            Optional<UUID> source = Optional.ofNullable(event.getSource().getEntity()).map(Entity::getUUID);
            Optional<VaultPlayer> killer = source.flatMap(vault::getPlayer);
            Optional<VaultPlayer> host = vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer);
            if (killer.isPresent()) {
               this.spawnBossLoot(vault, killer.get(), world);
            } else if (host.isPresent() && host.get() instanceof VaultRunner) {
               this.spawnBossLoot(vault, host.get(), world);
            } else {
               vault.getPlayers()
                  .stream()
                  .filter(player -> player instanceof VaultRunner)
                  .findFirst()
                  .ifPresent(player -> this.spawnBossLoot(vault, player, world));
            }
         }

         boolean isCowVault = vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false);
         if (isCowVault) {
            DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(world);
            vault.getPlayers()
               .forEach(
                  vPlayer -> vPlayer.runIfPresent(
                     srv, serverPlayer -> discoveredModelsData.discoverAllArmorPieceAndBroadcast(serverPlayer, ModDynamicModels.Armor.HELL_COW)
                  )
               );
         }
      }
   }

   public static boolean isBossInVault(VaultRaid vault, LivingEntity entity) {
      List<SummonAndKillBossObjective> matchingObjectives = vault.getPlayers()
         .stream()
         .map(player -> player.getActiveObjective(SummonAndKillBossObjective.class))
         .flatMap(Optional::stream)
         .filter(o -> !o.isCompleted())
         .filter(SummonAndKillBossObjective::allObelisksClicked)
         .filter(o -> o.getBossId().equals(entity.getUUID()))
         .toList();
      vault.getActiveObjective(SummonAndKillBossObjective.class).ifPresent(matchingObjectives::add);
      return matchingObjectives.stream().anyMatch(o -> entity.getUUID().equals(o.getBossId()));
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putInt("ProgressCount", this.progressCount);
      nbt.putInt("TargetCount", this.targetCount);
      if (this.getBossId() != null) {
         nbt.putString("BossId", this.getBossId().toString());
      }

      if (this.getBossName() != null) {
         nbt.putString("BossName", Serializer.toJson(this.getBossName()));
      }

      if (this.getBossPos() != null) {
         nbt.putDouble("BossPosX", this.getBossPos().x());
         nbt.putDouble("BossPosY", this.getBossPos().y());
         nbt.putDouble("BossPosZ", this.getBossPos().z());
      }

      nbt.putBoolean("IsBossDead", this.isBossDead());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.progressCount = nbt.getInt("ProgressCount");
      this.targetCount = nbt.getInt("TargetCount");
      if (nbt.contains("BossId", 8)) {
         this.bossId = UUID.fromString(nbt.getString("BossId"));
      }

      if (nbt.contains("BossName", 8)) {
         this.bossName = Serializer.fromJson(nbt.getString("BossName"));
      }

      this.bossPos = new Vec3(nbt.getDouble("BossPosX"), nbt.getDouble("BossPosY"), nbt.getDouble("BossPosZ"));
      this.isBossDead = nbt.getBoolean("IsBossDead");
   }
}
