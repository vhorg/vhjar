package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.SingularVaultRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.piece.FinalVaultBoss;
import iskallia.vault.world.vault.logic.task.VaultTask;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class KillTheBossObjective extends VaultObjective {
   private UUID bossId;
   private boolean bossDead;

   public KillTheBossObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   public void setBossId(UUID bossId) {
      this.bossId = bossId;
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      if (this.bossId == null) {
         EyesoreEntity boss = (EyesoreEntity)ModEntities.EYESORE.func_200721_a(world);
         Optional<FinalVaultBoss> room = vault.getGenerator().getPieces(FinalVaultBoss.class).stream().findFirst();
         if (room.isPresent()) {
            Vector3i pos = room.get().getCenter();
            boss.func_70012_b(pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.2, pos.func_177952_p() + 0.5, 0.0F, 0.0F);
            world.func_217470_d(boss);
            this.bossId = boss.func_110124_au();
         } else {
            this.bossDead = true;
         }
      } else {
         Entity boss = world.func_217461_a(this.bossId);
         if (boss == null || !boss.func_70089_S()) {
            this.bossDead = true;
         }
      }

      if (this.bossDead) {
         this.setCompleted();
      }
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return Blocks.field_150350_a.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      return null;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Kill the Boss").func_240699_a_(TextFormatting.OBFUSCATED);
   }

   @Nullable
   @Override
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Kill the Boss").func_240699_a_(TextFormatting.OBFUSCATED);
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      return new SingularVaultRoomLayout();
   }

   @Override
   public boolean shouldPauseTimer(MinecraftServer srv, VaultRaid vault) {
      return true;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      if (this.bossId != null) {
         tag.func_74778_a("BossId", this.bossId.toString());
      }

      tag.func_74757_a("BossDead", this.bossDead);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      if (nbt.func_150297_b("BossId", 8)) {
         this.bossId = UUID.fromString(nbt.func_74779_i("BossId"));
      }

      this.bossDead = nbt.func_74767_n("BossDead");
   }
}
