package iskallia.vault.skill.source;

import iskallia.vault.mana.ManaPlayer;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SkillSource {
   protected Vec3 pos;
   protected ManaPlayer mana;

   protected SkillSource() {
   }

   public static SkillSource empty() {
      return new SkillSource();
   }

   public static SkillSource of(Entity entity) {
      return new EntitySkillSource(entity);
   }

   public Optional<Vec3> getPos() {
      return Optional.ofNullable(this.pos);
   }

   public Optional<ManaPlayer> getMana() {
      return Optional.ofNullable(this.mana);
   }

   public SkillSource setPos(Vec3 pos) {
      this.pos = pos;
      return this;
   }

   public SkillSource setMana(ManaPlayer mana) {
      this.mana = mana;
      return this;
   }

   public SkillSource copy() {
      return new SkillSource()
         .setPos(this.pos != null ? new Vec3(this.pos.x(), this.pos.y(), this.pos.z()) : null)
         .setMana(this.mana != null ? this.mana : null);
   }

   public <E> Optional<E> as(Class<E> type) {
      return Optional.empty();
   }
}
