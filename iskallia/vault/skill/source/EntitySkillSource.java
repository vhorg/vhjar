package iskallia.vault.skill.source;

import iskallia.vault.mana.ManaPlayer;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntitySkillSource extends SkillSource {
   protected Entity entity;

   public EntitySkillSource(Entity entity) {
      this.entity = entity;
   }

   @Override
   public Optional<Vec3> getPos() {
      return Optional.of(super.getPos().orElse(this.entity.position()));
   }

   @Override
   public Optional<ManaPlayer> getMana() {
      return Optional.ofNullable(super.getMana().orElse(this.entity instanceof ManaPlayer mana ? mana : null));
   }

   @Override
   public <E> Optional<E> as(Class<E> type) {
      return this.entity != null && type.isAssignableFrom(this.entity.getClass()) ? Optional.of((E)this.entity) : Optional.empty();
   }
}
