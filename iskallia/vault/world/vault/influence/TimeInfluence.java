package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.time.extension.ModifierExtension;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class TimeInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("time");
   private int timeChange;

   TimeInfluence() {
      super(ID);
   }

   public TimeInfluence(int timeChange) {
      this();
      this.timeChange = timeChange;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
      player.getTimer().addTime(new ModifierExtension(this.timeChange), 0);
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
      player.getTimer().addTime(new ModifierExtension(-this.timeChange), 0);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putInt("timeChange", this.timeChange);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.timeChange = tag.getInt("timeChange");
   }
}
