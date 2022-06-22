package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.Vault;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import iskallia.vault.world.vault.gen.layout.SquareRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
public class SummonAndKillAllBossesObjective extends VaultObjective {
   protected int progressCount;
   protected int bossesCount;
   protected int targetCount = 10;
   private ResourceLocation roomPool = Vault.id("raid/rooms");
   private ResourceLocation tunnelPool = Vault.id("vault/tunnels");
   protected VListNBT<UUID, StringNBT> bosses = VListNBT.ofUUID();

   public SummonAndKillAllBossesObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   public boolean allObelisksClicked() {
      return this.progressCount >= this.targetCount;
   }

   public boolean allBossesDefeated() {
      return this.bossesCount >= this.targetCount;
   }

   public void addObelisk() {
      this.progressCount++;
   }

   public void setRoomPool(ResourceLocation roomPool) {
      this.roomPool = roomPool;
   }

   public void setTunnelPool(ResourceLocation tunnelPool) {
      this.tunnelPool = tunnelPool;
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      SquareRoomLayout layout = new SquareRoomLayout();
      layout.setRoomId(this.roomPool);
      layout.setTunnelId(this.tunnelPool);
      return layout;
   }

   public void completeBoss(VaultRaid vault, ServerWorld world, UUID uuid) {
      if (this.bosses.remove(uuid)) {
         this.bossesCount++;
         if (this.bossesCount < this.targetCount) {
            int level = vault.getProperties().getValue(VaultRaid.LEVEL);
            Set<VaultModifier> modifiers = ModConfigs.VAULT_MODIFIERS
               .getRandom(rand, level, VaultModifiersConfig.ModifierPoolType.FINAL_TENOS_ADDS, this.getId());
            List<VaultModifier> modifierList = new ArrayList<>(modifiers);
            Collections.shuffle(modifierList);
            VaultModifier modifier = MiscUtils.getRandomEntry(modifierList, rand);
            if (modifier != null) {
               ITextComponent ct = new StringTextComponent("Added ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(modifier.getNameComponent());
               vault.getModifiers().addPermanentModifier(modifier);
               vault.getPlayers().forEach(vPlayer -> {
                  modifier.apply(vault, vPlayer, world, world.func_201674_k());
                  vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
               });
            }
         }
      }
   }

   public void addBoss(LivingEntity boss) {
      this.bosses.add(boss.func_110124_au());
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.targetCount = amount;
   }

   @Nullable
   @Override
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Find Obelisks: ").func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.GOLD));
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
      return new StringTextComponent("Kill all Bosses").func_240699_a_(TextFormatting.GOLD);
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      if (!this.isCompleted()) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> vPlayer.runIfPresent(
                  world.func_73046_m(),
                  playerEntity -> {
                     VaultGoalMessage pkt = this.allObelisksClicked()
                        ? VaultGoalMessage.killBossGoal()
                        : VaultGoalMessage.obeliskGoal(this.progressCount, this.targetCount);
                     ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
                  }
               )
            );
         if (this.allBossesDefeated()) {
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

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onBossDeath(LivingDeathEvent event) {
      if (!event.getEntity().field_70170_p.func_201670_d()) {
         ServerWorld world = (ServerWorld)event.getEntity().field_70170_p;
         VaultRaid vault = VaultRaidData.get(world).getAt(world, event.getEntity().func_233580_cy_());
         if (VaultUtils.inVault(vault, event.getEntity())) {
            List<SummonAndKillAllBossesObjective> matchingObjectives = vault.getPlayers()
               .stream()
               .map(player -> player.getActiveObjective(SummonAndKillAllBossesObjective.class))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(o -> !o.isCompleted())
               .collect(Collectors.toList());
            if (matchingObjectives.isEmpty()) {
               vault.getActiveObjective(SummonAndKillAllBossesObjective.class)
                  .ifPresent(objective -> objective.completeBoss(vault, world, event.getEntity().func_110124_au()));
            } else {
               matchingObjectives.forEach(objective -> objective.completeBoss(vault, world, event.getEntity().func_110124_au()));
            }
         }
      }
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("ProgressCount", this.progressCount);
      nbt.func_74768_a("TargetCount", this.targetCount);
      nbt.func_74768_a("BossesCount", this.bossesCount);
      nbt.func_218657_a("Bosses", this.bosses.serializeNBT());
      nbt.func_74778_a("roomPool", this.roomPool.toString());
      nbt.func_74778_a("tunnelPool", this.tunnelPool.toString());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.progressCount = nbt.func_74762_e("ProgressCount");
      this.targetCount = nbt.func_74762_e("TargetCount");
      this.bossesCount = nbt.func_74762_e("BossesCount");
      this.bosses.deserializeNBT(nbt.func_150295_c("Bosses", 9));
      if (nbt.func_150297_b("roomPool", 8)) {
         this.roomPool = new ResourceLocation(nbt.func_74779_i("roomPool"));
      }

      if (nbt.func_150297_b("tunnelPool", 8)) {
         this.tunnelPool = new ResourceLocation(nbt.func_74779_i("tunnelPool"));
      }
   }
}
