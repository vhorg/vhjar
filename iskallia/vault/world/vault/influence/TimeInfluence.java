package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.time.extension.ModifierExtension;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class TimeInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("time");
   private int timeChange;

   TimeInfluence() {
      super(ID);
   }

   public TimeInfluence(int timeChange) {
      this();
      this.timeChange = timeChange;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      player.getTimer().addTime(new ModifierExtension(this.timeChange), 0);
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      player.getTimer().addTime(new ModifierExtension(-this.timeChange), 0);
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74768_a("timeChange", this.timeChange);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.timeChange = tag.func_74762_e("timeChange");
   }
}
