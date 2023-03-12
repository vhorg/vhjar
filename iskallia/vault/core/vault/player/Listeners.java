package iskallia.vault.core.vault.player;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.RegistryValueAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class Listeners extends DataObject<Listeners> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   protected static FieldKey<Listener.Map> MAP = FieldKey.of("map", Listener.Map.class)
      .with(Version.v1_0, CompoundAdapter.of(Listener.Map::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static FieldKey<ListenersLogic> LOGIC = FieldKey.of("logic", ListenersLogic.class)
      .with(Version.v1_0, RegistryValueAdapter.of(() -> VaultRegistry.LISTENERS_LOGIC, ISupplierKey::getKey, Supplier::get), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public Listeners() {
      this.set(MAP, new Listener.Map());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public Listener get(UUID uuid) {
      return this.get(MAP).get(uuid);
   }

   public boolean contains(UUID uuid) {
      return this.get(MAP).containsKey(uuid);
   }

   public Collection<Listener> getAll() {
      return new ArrayList<>(this.get(MAP).values());
   }

   public <T extends Listener> Collection<T> getAll(Class<T> type) {
      List<T> result = new ArrayList<>();

      for (Listener listener : this.get(MAP).values()) {
         if (type.isAssignableFrom(listener.getClass())) {
            result.add((T)listener);
         }
      }

      return result;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      this.get(MAP).forEach((uuid, listener) -> listener.initServer(world, vault));
      this.ifPresent(LOGIC, logic -> logic.initServer(world, vault));
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      new HashMap<>(this.get(MAP)).forEach((uuid, listener) -> listener.tickServer(world, vault));
      this.ifPresent(LOGIC, logic -> logic.tickServer(world, vault, new HashMap<>(this.get(MAP))));
   }

   public void releaseServer() {
      this.get(MAP).forEach((uuid, listener) -> listener.releaseServer());
      this.ifPresent(LOGIC, ListenersLogic::releaseServer);
   }

   public Listeners add(VirtualWorld world, Vault vault, Listener listener) {
      if (!this.has(LOGIC) || this.get(LOGIC).onJoin(world, vault, listener)) {
         this.get(MAP).put(listener.get(Listener.ID), listener);
         listener.initServer(world, vault);
         CommonEvents.LISTENER_JOIN.invoke(vault, listener);
         listener.onJoin(world, vault);
      }

      return this;
   }

   public Listeners remove(VirtualWorld world, Vault vault, Listener listener) {
      if (!this.has(LOGIC) || this.get(LOGIC).onLeave(world, vault, listener)) {
         this.get(MAP).remove(listener.get(Listener.ID), listener);
         CommonEvents.LISTENER_LEAVE.invoke(vault, listener);
         listener.onLeave(world, vault);
         listener.releaseServer();
      }

      return this;
   }

   public int getObjectivePriority(UUID uuid, Objective objective) {
      Listener listener = this.get(MAP).get(uuid);
      return listener == null ? -1 : listener.get(Listener.OBJECTIVES).indexOf(objective.get(Objective.ID));
   }
}
