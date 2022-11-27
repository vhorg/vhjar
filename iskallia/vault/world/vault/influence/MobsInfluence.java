package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultSpawner;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class MobsInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("mobs");
   private int mobsAdded;

   MobsInfluence() {
      super(ID);
   }

   public MobsInfluence(int mobsAdded) {
      this();
      this.mobsAdded = mobsAdded;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
      player.getProperties().get(VaultRaid.SPAWNER).ifPresent(spawner -> {
         ((VaultSpawner)spawner.getBaseValue()).addMaxMobs(this.mobsAdded);
         spawner.updateNBT();
      });
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
      player.getProperties().get(VaultRaid.SPAWNER).ifPresent(spawner -> {
         ((VaultSpawner)spawner.getBaseValue()).addMaxMobs(-this.mobsAdded);
         spawner.updateNBT();
      });
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putInt("mobsAdded", this.mobsAdded);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.mobsAdded = tag.getInt("mobsAdded");
   }
}
