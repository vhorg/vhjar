package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.config.VaultModifierPoolsConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.network.NetworkDirection;

public class SummonAndKillAllBossesObjective extends VaultObjective {
   protected int progressCount;
   protected int bossesCount;
   protected int targetCount = 10;
   protected VListNBT<UUID, StringTag> bosses = VListNBT.ofUUID();

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

   public void completeBoss(VaultRaid vault, ServerLevel world, UUID uuid) {
      if (this.bosses.remove(uuid)) {
         this.bossesCount++;
         if (this.bossesCount < this.targetCount) {
            int level = vault.getProperties().getValue(VaultRaid.LEVEL);
            Set<VaultModifier<?>> modifiers = ModConfigs.VAULT_MODIFIER_POOLS
               .getRandom(rand, level, VaultModifierPoolsConfig.ModifierPoolType.FINAL_TENOS_ADDS, this.getId());
            List<VaultModifier<?>> modifierList = new ArrayList<>(modifiers);
            Collections.shuffle(modifierList);
            VaultModifier<?> modifier = MiscUtils.getRandomEntry(modifierList, rand);
            if (modifier != null) {
               Component ct = new TextComponent("Added ").withStyle(ChatFormatting.GRAY).append(modifier.getNameComponent());
               vault.getModifiers().addPermanentModifier(modifier, 1);
               vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.getServer(), sPlayer -> sPlayer.sendMessage(ct, Util.NIL_UUID)));
            }
         }
      }
   }

   public void addBoss(LivingEntity boss) {
      this.bosses.add(boss.getUUID());
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.targetCount = amount;
   }

   @Nullable
   @Override
   public Component getObjectiveTargetDescription(int amount) {
      return new TextComponent("Find Obelisks: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.GOLD));
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
      return new TextComponent("Kill all Bosses").withStyle(ChatFormatting.GOLD);
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      if (!this.isCompleted()) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> vPlayer.runIfPresent(
                  world.getServer(),
                  playerEntity -> {
                     VaultGoalMessage pkt = this.allObelisksClicked()
                        ? VaultGoalMessage.killBossGoal()
                        : VaultGoalMessage.obeliskGoal(this.progressCount, this.targetCount);
                     ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                  }
               )
            );
         if (this.allBossesDefeated()) {
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

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putInt("ProgressCount", this.progressCount);
      nbt.putInt("TargetCount", this.targetCount);
      nbt.putInt("BossesCount", this.bossesCount);
      nbt.put("Bosses", this.bosses.serializeNBT());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.progressCount = nbt.getInt("ProgressCount");
      this.targetCount = nbt.getInt("TargetCount");
      this.bossesCount = nbt.getInt("BossesCount");
      this.bosses.deserializeNBT(nbt.getList("Bosses", 9));
   }
}
