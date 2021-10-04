package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultSpawner;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class MobsInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("mobs");
   private int mobsAdded;

   MobsInfluence() {
      super(ID);
   }

   public MobsInfluence(int mobsAdded) {
      this();
      this.mobsAdded = mobsAdded;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      player.getProperties().get(VaultRaid.SPAWNER).ifPresent(spawner -> {
         ((VaultSpawner)spawner.getBaseValue()).addMaxMobs(this.mobsAdded);
         spawner.updateNBT();
      });
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      player.getProperties().get(VaultRaid.SPAWNER).ifPresent(spawner -> {
         ((VaultSpawner)spawner.getBaseValue()).addMaxMobs(-this.mobsAdded);
         spawner.updateNBT();
      });
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74768_a("mobsAdded", this.mobsAdded);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.mobsAdded = tag.func_74762_e("mobsAdded");
   }
}
