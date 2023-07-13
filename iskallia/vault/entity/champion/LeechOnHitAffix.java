package iskallia.vault.entity.champion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class LeechOnHitAffix extends ChampionAffixBase implements IChampionOnHitAffix {
   public static final String TYPE = "leech_on_hit";
   private final float leechPercentage;

   public LeechOnHitAffix(String name, float leechPercentage) {
      super("leech_on_hit", name);
      this.leechPercentage = leechPercentage;
   }

   public static LeechOnHitAffix deserialize(CompoundTag tag) {
      return new LeechOnHitAffix(deserializeName(tag), tag.getFloat("leech_percentage"));
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = super.serialize();
      tag.putFloat("leech_percentage", this.leechPercentage);
      return tag;
   }

   @Override
   public void onChampionHitPlayer(LivingEntity champion, Player player, float amount) {
      champion.heal(amount * this.leechPercentage);
   }
}
