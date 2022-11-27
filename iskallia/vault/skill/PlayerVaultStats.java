package iskallia.vault.skill;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.world.data.EternalsData;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class PlayerVaultStats implements INBTSerializable<CompoundTag> {
   private final UUID uuid;
   private int vaultLevel;
   private int exp;
   private int unspentSkillPoints;
   private int unspentKnowledgePoints;
   private int unspentArchetypePoints;
   private int unspentRegretPoints;
   private int totalSpentSkillPoints;
   private int totalSpentKnowledgePoints;
   private int totalSpentArchetypePoints;
   private int totalSpentRegretPoints;

   public PlayerVaultStats(UUID uuid) {
      this.uuid = uuid;
   }

   public int getVaultLevel() {
      return this.vaultLevel;
   }

   public int getExp() {
      return this.exp;
   }

   public int getUnspentSkillPoints() {
      return this.unspentSkillPoints;
   }

   public int getUnspentKnowledgePoints() {
      return this.unspentKnowledgePoints;
   }

   public int getUnspentArchetypePoints() {
      return this.unspentArchetypePoints;
   }

   public int getUnspentRegretPoints() {
      return this.unspentRegretPoints;
   }

   public int getTotalSpentSkillPoints() {
      return this.totalSpentSkillPoints;
   }

   public int getTotalSpentKnowledgePoints() {
      return this.totalSpentKnowledgePoints;
   }

   public int getTotalSpentArchetypePoints() {
      return this.totalSpentArchetypePoints;
   }

   public int getTotalSpentRegretPoints() {
      return this.totalSpentRegretPoints;
   }

   public int getExpNeededToNextLevel() {
      return this.getVaultLevel() >= ModConfigs.LEVELS_META.getMaxLevel() ? 0 : ModConfigs.LEVELS_META.getLevelMeta(this.vaultLevel).tnl;
   }

   public void setVaultLevel(MinecraftServer server, int level) {
      this.vaultLevel = Mth.clamp(level, 0, ModConfigs.LEVELS_META.getMaxLevel());
      this.exp = 0;
      this.sync(server);
   }

   public void addVaultExp(MinecraftServer server, int exp) {
      int maxLevel = ModConfigs.LEVELS_META.getMaxLevel();
      if (this.getVaultLevel() < maxLevel) {
         this.exp = Math.max(this.exp, 0);
         this.exp += exp;
         int initialLevel = this.vaultLevel;

         int neededExp;
         while (this.exp >= (neededExp = this.getExpNeededToNextLevel())) {
            this.vaultLevel++;
            this.unspentSkillPoints++;
            this.exp -= neededExp;
            if (this.getVaultLevel() >= maxLevel) {
               this.vaultLevel = maxLevel;
               this.exp = 0;
               break;
            }
         }

         if (this.vaultLevel > initialLevel) {
            NetcodeUtils.runIfPresent(server, this.uuid, this::fancyLevelUpEffects);
         }

         this.sync(server);
      }
   }

   protected void fancyLevelUpEffects(ServerPlayer player) {
      ServerLevel world = player.getLevel();
      Vec3 pos = player.position();

      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian();
         double d1 = world.random.nextGaussian();
         double d2 = world.random.nextGaussian();
         world.sendParticles(
            ParticleTypes.TOTEM_OF_UNDYING,
            pos.x() + world.random.nextDouble() - 0.5,
            pos.y() + world.random.nextDouble() - 0.5 + 3.0,
            pos.z() + world.random.nextDouble() - 0.5,
            10,
            d0,
            d1,
            d2,
            0.25
         );
      }

      world.playSound(null, player.blockPosition(), ModSounds.VAULT_LEVEL_UP_SFX, SoundSource.PLAYERS, 1.0F, 2.0F);
   }

   public void spendSkillPoints(MinecraftServer server, int amount) {
      this.unspentSkillPoints -= amount;
      this.totalSpentSkillPoints += amount;
      this.sync(server);
   }

   public void spendKnowledgePoints(MinecraftServer server, int amount) {
      this.unspentKnowledgePoints -= amount;
      this.totalSpentKnowledgePoints += amount;
      this.sync(server);
   }

   public void spendArchetypePoints(MinecraftServer server, int amount) {
      this.unspentArchetypePoints -= amount;
      this.totalSpentArchetypePoints += amount;
      this.sync(server);
   }

   public void spendRegretPoints(MinecraftServer server, int amount) {
      this.unspentRegretPoints -= amount;
      this.totalSpentRegretPoints += amount;
      this.sync(server);
   }

   public PlayerVaultStats reset(MinecraftServer server) {
      this.vaultLevel = 0;
      this.exp = 0;
      this.unspentSkillPoints = 0;
      this.unspentKnowledgePoints = 0;
      this.unspentArchetypePoints = 0;
      this.unspentRegretPoints = 0;
      this.sync(server);
      return this;
   }

   public PlayerVaultStats addSkillPoints(int amount) {
      this.unspentSkillPoints += amount;
      return this;
   }

   public PlayerVaultStats addKnowledgePoints(int amount) {
      this.unspentKnowledgePoints += amount;
      return this;
   }

   public PlayerVaultStats addArchetypePoints(int amount) {
      this.unspentArchetypePoints += amount;
      return this;
   }

   public PlayerVaultStats addRegretPoints(int amount) {
      this.unspentRegretPoints += amount;
      return this;
   }

   public void sync(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> {
            ModNetwork.CHANNEL
               .sendTo(
                  new VaultLevelMessage(
                     this.vaultLevel,
                     this.exp,
                     this.getExpNeededToNextLevel(),
                     this.unspentSkillPoints,
                     this.unspentKnowledgePoints,
                     this.unspentArchetypePoints,
                     this.unspentRegretPoints
                  ),
                  player.connection.connection,
                  NetworkDirection.PLAY_TO_CLIENT
               );
            EternalsData.get(server).setDirty();
         }
      );
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("vaultLevel", this.vaultLevel);
      nbt.putInt("exp", this.exp);
      nbt.putInt("unspentSkillPts", this.unspentSkillPoints);
      nbt.putInt("unspentKnowledgePts", this.unspentKnowledgePoints);
      nbt.putInt("unspentArchetypePoints", this.unspentArchetypePoints);
      nbt.putInt("unspentRegretPoints", this.unspentRegretPoints);
      nbt.putInt("totalSpentSkillPoints", this.totalSpentSkillPoints);
      nbt.putInt("totalSpentKnowledgePoints", this.totalSpentKnowledgePoints);
      nbt.putInt("totalSpentArchetypePoints", this.totalSpentArchetypePoints);
      nbt.putInt("totalSpentRegretPoints", this.totalSpentRegretPoints);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.vaultLevel = nbt.getInt("vaultLevel");
      this.exp = nbt.getInt("exp");
      this.unspentSkillPoints = nbt.getInt("unspentSkillPts");
      this.unspentKnowledgePoints = nbt.getInt("unspentKnowledgePts");
      this.unspentArchetypePoints = nbt.getInt("unspentArchetypePoints");
      this.unspentRegretPoints = nbt.getInt("unspentRegretPoints");
      this.totalSpentSkillPoints = nbt.getInt("totalSpentSkillPoints");
      this.totalSpentKnowledgePoints = nbt.getInt("totalSpentKnowledgePoints");
      this.totalSpentArchetypePoints = nbt.getInt("totalSpentArchetypePoints");
      this.totalSpentRegretPoints = nbt.getInt("totalSpentRegretPoints");
   }
}
