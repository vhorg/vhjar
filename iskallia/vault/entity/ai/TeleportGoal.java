package iskallia.vault.entity.ai;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class TeleportGoal<T extends LivingEntity> extends GoalTask<T> {
   private final Predicate<T> startCondition;
   private final Function<T, Vector3d> targetSupplier;
   private final Consumer<T> postTeleport;

   protected TeleportGoal(T entity, Predicate<T> startCondition, Function<T, Vector3d> targetSupplier, Consumer<T> postTeleport) {
      super(entity);
      this.startCondition = startCondition;
      this.targetSupplier = targetSupplier;
      this.postTeleport = postTeleport;
   }

   public static <T extends LivingEntity> TeleportGoal.Builder<T> builder(T entity) {
      return new TeleportGoal.Builder<>(entity);
   }

   public boolean func_75250_a() {
      return this.startCondition.test(this.getEntity());
   }

   public void func_75249_e() {
      Vector3d target = this.targetSupplier.apply(this.getEntity());
      if (target != null) {
         boolean teleported = this.getEntity().func_213373_a(target.func_82615_a(), target.func_82617_b(), target.func_82616_c(), true);
         if (teleported) {
            this.postTeleport.accept(this.getEntity());
         }
      }
   }

   public static class Builder<T extends LivingEntity> {
      private final T entity;
      private Predicate<T> startCondition = entityx -> false;
      private Function<T, Vector3d> targetSupplier = entityx -> null;
      private Consumer<T> postTeleport = entityx -> {};

      private Builder(T entity) {
         this.entity = entity;
      }

      public TeleportGoal.Builder<T> start(Predicate<T> startCondition) {
         this.startCondition = startCondition;
         return this;
      }

      public TeleportGoal.Builder<T> to(Function<T, Vector3d> targetSupplier) {
         this.targetSupplier = targetSupplier;
         return this;
      }

      public TeleportGoal.Builder<T> then(Consumer<T> postTeleport) {
         this.postTeleport = postTeleport;
         return this;
      }

      public TeleportGoal<T> build() {
         return new TeleportGoal<>(this.entity, this.startCondition, this.targetSupplier, this.postTeleport);
      }
   }
}
