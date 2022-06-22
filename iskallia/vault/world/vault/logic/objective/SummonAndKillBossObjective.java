package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import iskallia.vault.world.vault.time.VaultTimer;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class SummonAndKillBossObjective extends VaultObjective {
   protected int progressCount;
   protected int targetCount = rand.nextInt(4) + 3;
   protected UUID bossId = null;
   protected ITextComponent bossName = null;
   protected Vector3d bossPos = null;
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

   public ITextComponent getBossName() {
      return this.bossName;
   }

   public Vector3d getBossPos() {
      return this.bossPos;
   }

   public void setBoss(LivingEntity boss) {
      this.bossId = boss.func_110124_au();
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.targetCount = amount;
   }

   @Nullable
   @Override
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Required Obelisks: ").func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.GOLD));
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return ModBlocks.OBELISK.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getBossCrate()) : LootTable.field_186464_a;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Kill the Boss").func_240699_a_(TextFormatting.GOLD);
   }

   @Override
   public int modifyMinimumObjectiveCount(int objectives, int requiredAmount) {
      return Math.max(objectives, requiredAmount);
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      if (!this.isCompleted()) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> {
                  vPlayer.runIfPresent(
                     world.func_73046_m(),
                     playerEntity -> {
                        VaultGoalMessage pkt = this.allObelisksClicked()
                           ? VaultGoalMessage.killBossGoal()
                           : VaultGoalMessage.obeliskGoal(this.progressCount, this.targetCount);
                        ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
                     }
                  );
                  if (this.isBossSpawned()) {
                     vPlayer.sendIfPresent(world.func_73046_m(), new BossMusicMessage(true));
                  }
               }
            );
         if (this.isBossDead) {
            this.setCompleted();
         }
      }
   }

   @Override
   public void complete(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      super.complete(vault, player, world);
      player.sendIfPresent(world.func_73046_m(), new BossMusicMessage(false));
      player.sendIfPresent(world.func_73046_m(), VaultGoalMessage.clear());
   }

   @Override
   public void complete(VaultRaid vault, ServerWorld world) {
      super.complete(vault, world);
      vault.getPlayers().forEach(player -> {
         player.sendIfPresent(world.func_73046_m(), new BossMusicMessage(false));
         player.sendIfPresent(world.func_73046_m(), VaultGoalMessage.clear());
      });
   }

   public void spawnBossLoot(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            Builder builder = new Builder(world)
               .func_216023_a(world.field_73012_v)
               .func_216015_a(LootParameters.field_216281_a, playerEntity)
               .func_216015_a(LootParameters.field_237457_g_, this.getBossPos())
               .func_216015_a(LootParameters.field_216283_c, DamageSource.func_76365_a(playerEntity))
               .func_216021_b(LootParameters.field_216284_d, playerEntity)
               .func_216021_b(LootParameters.field_216285_e, playerEntity)
               .func_216015_a(LootParameters.field_216282_b, playerEntity)
               .func_186469_a(playerEntity.func_184817_da());
            LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
            this.dropBossCrate(world, vault, player, ctx);

            for (int i = 1; i < vault.getPlayers().size(); i++) {
               if (rand.nextFloat() < 0.5F) {
                  this.dropBossCrate(world, vault, player, ctx);
               }
            }

            world.func_73046_m().func_184103_al().func_232641_a_(this.getBossKillMessage(playerEntity), ChatType.CHAT, player.getPlayerId());
         }
      );
   }

   private ITextComponent getBossKillMessage(PlayerEntity player) {
      IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
      IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
      playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
      return msgContainer.func_230529_a_(playerName).func_240702_b_(" defeated ").func_230529_a_(this.getBossName()).func_240702_b_("!");
   }

   private void dropBossCrate(ServerWorld world, VaultRaid vault, VaultPlayer rewardPlayer, LootContext context) {
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
                           vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> data.updateFastestVaultTime(sPlayer, timer.getRunTime()));
                        }
                     });
               }
            }
         );
      BlockPos dropPos = rewardPlayer.getServerPlayer(world.func_73046_m()).<BlockPos>map(Entity::func_233580_cy_).orElse(new BlockPos(this.getBossPos()));
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
      ItemEntity item = new ItemEntity(world, dropPos.func_177958_n(), dropPos.func_177956_o(), dropPos.func_177952_p(), crate);
      item.func_174869_p();
      world.func_217376_c(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   @Override
   protected void addSpecialLoot(ServerWorld world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
      boolean isCowVault = vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false);
      if (isCowVault) {
         stacks.add(new ItemStack(ModItems.ARMOR_CRATE_HELLCOW));
      }
   }

   protected void onBossDeath(LivingDeathEvent event, VaultRaid vault, ServerWorld world, boolean dropCrate) {
      LivingEntity boss = event.getEntityLiving();
      if (boss.func_110124_au().equals(this.getBossId())) {
         this.bossName = boss.func_200201_e();
         this.bossPos = boss.func_213303_ch();
         this.isBossDead = true;
         if (dropCrate) {
            Optional<UUID> source = Optional.ofNullable(event.getSource().func_76346_g()).map(Entity::func_110124_au);
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
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onBossDeath(LivingDeathEvent event) {
      if (!event.getEntity().field_70170_p.func_201670_d()) {
         ServerWorld world = (ServerWorld)event.getEntity().field_70170_p;
         VaultRaid vault = VaultRaidData.get(world).getAt(world, event.getEntity().func_233580_cy_());
         if (VaultUtils.inVault(vault, event.getEntity())) {
            List<SummonAndKillBossObjective> matchingObjectives = vault.getPlayers()
               .stream()
               .map(player -> player.getActiveObjective(SummonAndKillBossObjective.class))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(o -> !o.isCompleted())
               .filter(SummonAndKillBossObjective::allObelisksClicked)
               .filter(o -> o.getBossId().equals(event.getEntity().func_110124_au()))
               .collect(Collectors.toList());
            if (matchingObjectives.isEmpty()) {
               vault.getActiveObjective(SummonAndKillBossObjective.class).ifPresent(objective -> objective.onBossDeath(event, vault, world, true));
            } else {
               matchingObjectives.forEach(objective -> objective.onBossDeath(event, vault, world, false));
            }
         }
      }
   }

   public static boolean isBossInVault(VaultRaid vault, LivingEntity entity) {
      List<SummonAndKillBossObjective> matchingObjectives = vault.getPlayers()
         .stream()
         .map(player -> player.getActiveObjective(SummonAndKillBossObjective.class))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .filter(o -> !o.isCompleted())
         .filter(SummonAndKillBossObjective::allObelisksClicked)
         .filter(o -> o.getBossId().equals(entity.func_110124_au()))
         .collect(Collectors.toList());
      vault.getActiveObjective(SummonAndKillBossObjective.class).ifPresent(matchingObjectives::add);
      return matchingObjectives.stream().anyMatch(o -> entity.func_110124_au().equals(o.getBossId()));
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("ProgressCount", this.progressCount);
      nbt.func_74768_a("TargetCount", this.targetCount);
      if (this.getBossId() != null) {
         nbt.func_74778_a("BossId", this.getBossId().toString());
      }

      if (this.getBossName() != null) {
         nbt.func_74778_a("BossName", Serializer.func_150696_a(this.getBossName()));
      }

      if (this.getBossPos() != null) {
         nbt.func_74780_a("BossPosX", this.getBossPos().func_82615_a());
         nbt.func_74780_a("BossPosY", this.getBossPos().func_82617_b());
         nbt.func_74780_a("BossPosZ", this.getBossPos().func_82616_c());
      }

      nbt.func_74757_a("IsBossDead", this.isBossDead());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.progressCount = nbt.func_74762_e("ProgressCount");
      this.targetCount = nbt.func_74762_e("TargetCount");
      if (nbt.func_150297_b("BossId", 8)) {
         this.bossId = UUID.fromString(nbt.func_74779_i("BossId"));
      }

      if (nbt.func_150297_b("BossName", 8)) {
         this.bossName = Serializer.func_240643_a_(nbt.func_74779_i("BossName"));
      }

      this.bossPos = new Vector3d(nbt.func_74769_h("BossPosX"), nbt.func_74769_h("BossPosY"), nbt.func_74769_h("BossPosZ"));
      this.isBossDead = nbt.func_74767_n("IsBossDead");
   }
}
