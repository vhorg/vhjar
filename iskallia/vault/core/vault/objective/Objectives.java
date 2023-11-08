package iskallia.vault.core.vault.objective;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Objectives extends DataObject<Objectives> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<String> KEY = FieldKey.of("key", String.class).with(Version.v1_3, Adapters.UTF_8, DISK.all()).register(FIELDS);
   public static final FieldKey<Integer> INDEX = FieldKey.of("index", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Objective.ObjList> LIST = FieldKey.of("list", Objective.ObjList.class)
      .with(Version.v1_0, CompoundAdapter.of(Objective.ObjList::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public Objectives() {
      this.set(INDEX, Integer.valueOf(0));
      this.set(LIST, new Objective.ObjList());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      this.get(LIST).forEach(objective -> objective.initServer(world, vault));
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(LIST).forEach(objective -> vault.ifPresent(Vault.LISTENERS, listeners -> {
         for (Listener listener : listeners.getAll()) {
            objective.tickListener(world, vault, listener);
         }
      }));
      this.get(LIST).forEach(objective -> objective.tickServer(world, vault));
   }

   public void releaseServer() {
      this.get(LIST).forEach(Objective::releaseServer);
   }

   public Objectives add(Objective objective) {
      this.get(LIST).add(objective.set(Objective.ID, this.get(INDEX)));
      this.set(INDEX, Integer.valueOf(this.get(INDEX) + 1));
      return this;
   }

   public Optional<Objective> get(int index) {
      for (Objective objective : this.get(LIST)) {
         if (objective.get(Objective.ID) == index) {
            return Optional.of(objective);
         }
      }

      return Optional.empty();
   }

   public Objectives addAll(Collection<Objective> objectives) {
      objectives.forEach(this::add);
      return this;
   }

   public <T extends Objective> boolean forEach(Class<T> type, Predicate<T> consumer) {
      for (Objective objective : this.get(LIST)) {
         boolean result = this.forEachInternal(objective, type, consumer);
         if (result) {
            return true;
         }
      }

      return false;
   }

   protected <T extends Objective> boolean forEachInternal(Objective parent, Class<T> type, Predicate<T> consumer) {
      if (type.isAssignableFrom(parent.getClass()) && consumer.test((T)parent)) {
         return true;
      } else {
         for (Objective child : parent.get(Objective.CHILDREN)) {
            if (this.forEachInternal(child, type, consumer)) {
               return true;
            }
         }

         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient(Vault vault) {
      this.get(LIST).forEach(objective -> objective.initClient(vault));
   }
}
