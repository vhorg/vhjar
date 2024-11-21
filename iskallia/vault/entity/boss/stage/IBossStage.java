package iskallia.vault.entity.boss.stage;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.Optional;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public interface IBossStage {
   void tick();

   boolean isFinished();

   boolean makesBossInvulnerable();

   Set<Flag> getControlFlags();

   void init();

   void start();

   void stop();

   void finish();

   default CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putString("StageType", this.getName());
      return tag;
   }

   String getName();

   Optional<AnimationBuilder> getAnimation();

   Optional<ResourceLocation> getTextureLocation();

   Tuple<Integer, Integer> getBossBarTextureVs();

   default WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return WeightedList.empty();
   }

   default WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks() {
      return WeightedList.empty();
   }

   float getProgress();

   default void onHurt() {
   }
}
