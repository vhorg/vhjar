package iskallia.vault.entity.boss;

import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;

public class MeleeAttacks {
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes HAMMERSMASH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 30, 17, ArtifactBossEntity.AttackMove.HAMMERSMASH, 1.0F, 0.0F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes UPPERCUT_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 25, 15, ArtifactBossEntity.AttackMove.UPPERCUT, 1.5F, 0.3F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes GROUNDSLAM_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 45, 24, ArtifactBossEntity.AttackMove.GROUNDSLAM, 2.5F, 0.1F
   );
   public static final Map<String, BiFunction<ArtifactBossEntity, Double, IMeleeAttack>> MELEE_ATTACK_FACTORIES = Map.of(
      "hammersmash",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, HAMMERSMASH_ATTACK),
      "uppercut",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, UPPERCUT_ATTACK),
      "groundslam",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, GROUNDSLAM_ATTACK),
      "throw",
      ThrowAttack::new,
      "aoeclose",
      AoeCloseAttack::new
   );

   public record AttackData(String name, double multiplier) {
      public static MeleeAttacks.AttackData from(CompoundTag tag) {
         return new MeleeAttacks.AttackData(tag.getString("Name"), tag.getDouble("Multiplier"));
      }

      public void serializeTo(CompoundTag tag) {
         tag.putString("Name", this.name);
         tag.putDouble("Multiplier", this.multiplier);
      }
   }
}
