package iskallia.vault.skill;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.util.NetcodeUtils;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class PlayerVaultStats implements INBTSerializable<CompoundNBT> {
   private final UUID uuid;
   private int vaultLevel;
   private int exp;
   private int unspentSkillPts = 5;
   private int unspentKnowledgePts;
   private int totalSpentSkillPoints;
   private int totalSpentKnowledgePoints;

   public PlayerVaultStats(UUID uuid) {
      this.uuid = uuid;
   }

   public int getVaultLevel() {
      return this.vaultLevel;
   }

   public int getExp() {
      return this.exp;
   }

   public int getUnspentSkillPts() {
      return this.unspentSkillPts;
   }

   public int getUnspentKnowledgePts() {
      return this.unspentKnowledgePts;
   }

   public int getTotalSpentSkillPoints() {
      return this.totalSpentSkillPoints;
   }

   public int getTotalSpentKnowledgePoints() {
      return this.totalSpentKnowledgePoints;
   }

   public int getTnl() {
      return ModConfigs.LEVELS_META.getLevelMeta(this.vaultLevel).tnl;
   }

   public PlayerVaultStats setVaultLevel(MinecraftServer server, int level) {
      this.vaultLevel = level;
      this.exp = 0;
      this.sync(server);
      return this;
   }

   public PlayerVaultStats addVaultExp(MinecraftServer server, int exp) {
      this.exp += exp;
      int initialLevel = this.vaultLevel;

      int tnl;
      while (this.exp >= (tnl = this.getTnl())) {
         this.vaultLevel++;
         if (this.vaultLevel <= 200) {
            this.unspentSkillPts++;
         }

         this.exp -= tnl;
      }

      if (this.vaultLevel > initialLevel) {
         NetcodeUtils.runIfPresent(server, this.uuid, this::fancyLevelUpEffects);
      }

      this.sync(server);
      return this;
   }

   protected void fancyLevelUpEffects(ServerPlayerEntity player) {
      World world = player.field_70170_p;
      Vector3d pos = player.func_213303_ch();

      for (int i = 0; i < 20; i++) {
         double d0 = world.field_73012_v.nextGaussian() * 1.0;
         double d1 = world.field_73012_v.nextGaussian() * 1.0;
         double d2 = world.field_73012_v.nextGaussian() * 1.0;
         ((ServerWorld)world)
            .func_195598_a(
               ParticleTypes.field_197604_O,
               pos.func_82615_a() + world.field_73012_v.nextDouble() - 0.5,
               pos.func_82617_b() + world.field_73012_v.nextDouble() - 0.5 + 3.0,
               pos.func_82616_c() + world.field_73012_v.nextDouble() - 0.5,
               10,
               d0,
               d1,
               d2,
               0.25
            );
      }

      world.func_184133_a(null, player.func_233580_cy_(), ModSounds.VAULT_LEVEL_UP_SFX, SoundCategory.PLAYERS, 1.0F, 2.0F);
   }

   public PlayerVaultStats spendSkillPoints(MinecraftServer server, int amount) {
      this.unspentSkillPts -= amount;
      this.totalSpentSkillPoints += amount;
      this.sync(server);
      return this;
   }

   public PlayerVaultStats spendKnowledgePoints(MinecraftServer server, int amount) {
      this.unspentKnowledgePts -= amount;
      this.totalSpentKnowledgePoints += amount;
      this.sync(server);
      return this;
   }

   public PlayerVaultStats reset(MinecraftServer server) {
      this.vaultLevel = 0;
      this.exp = 0;
      this.unspentSkillPts = 0;
      this.unspentKnowledgePts = 0;
      this.sync(server);
      return this;
   }

   public PlayerVaultStats addSkillPoints(int amount) {
      this.unspentSkillPts += amount;
      return this;
   }

   public PlayerVaultStats addKnowledgePoints(int amount) {
      this.unspentKnowledgePts += amount;
      return this;
   }

   public void sync(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL
            .sendTo(
               new VaultLevelMessage(this.vaultLevel, this.exp, this.getTnl(), this.unspentSkillPts, this.unspentKnowledgePts),
               player.field_71135_a.field_147371_a,
               NetworkDirection.PLAY_TO_CLIENT
            )
      );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74768_a("vaultLevel", this.vaultLevel);
      nbt.func_74768_a("exp", this.exp);
      nbt.func_74768_a("unspentSkillPts", this.unspentSkillPts);
      nbt.func_74768_a("unspentKnowledgePts", this.unspentKnowledgePts);
      nbt.func_74768_a("totalSpentSkillPoints", this.totalSpentSkillPoints);
      nbt.func_74768_a("totalSpentKnowledgePoints", this.totalSpentKnowledgePoints);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.vaultLevel = nbt.func_74762_e("vaultLevel");
      this.exp = nbt.func_74762_e("exp");
      this.unspentSkillPts = nbt.func_74762_e("unspentSkillPts");
      this.unspentKnowledgePts = nbt.func_74762_e("unspentKnowledgePts");
      this.totalSpentSkillPoints = nbt.func_74762_e("totalSpentSkillPoints");
      this.totalSpentKnowledgePoints = nbt.func_74762_e("totalSpentKnowledgePoints");
      this.vaultLevel = nbt.func_74762_e("vaultLevel");
   }
}
