package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.HashMap;
import java.util.UUID;

public class StatsCollector extends DataObject<StatsCollector> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<StatsCollector.Map> MAP = FieldKey.of("map", StatsCollector.Map.class)
      .with(Version.v1_0, CompoundAdapter.of(StatsCollector.Map::new), DISK.all())
      .register(FIELDS);

   public StatsCollector() {
      this.set(MAP, new StatsCollector.Map());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      this.get(MAP).forEach((uuid, collector) -> collector.initServer(world, vault, uuid));
      CommonEvents.LISTENER_JOIN.register(this, data -> {
         if (data.getVault() == vault) {
            if (data.getListener() instanceof Runner) {
               UUID uuid = data.getListener().get(Listener.ID);
               this.get(MAP).computeIfAbsent(uuid, _uuid -> new StatCollector()).initServer(world, vault, uuid);
            }
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> {
         if (data.getVault() == vault) {
            UUID uuid = data.getListener().get(Listener.ID);
            if (this.get(MAP).containsKey(uuid)) {
               this.get(MAP).get(uuid).releaseServer();
            }
         }
      });
   }

   public void releaseServer() {
      this.get(MAP).values().forEach(StatCollector::releaseServer);
      CommonEvents.release(this);
   }

   public StatCollector get(Listener listener) {
      return this.get(listener.get(Listener.ID));
   }

   public StatCollector get(UUID uuid) {
      return this.get(MAP).get(uuid);
   }

   public StatsCollector.Map getMap() {
      return this.get(MAP);
   }

   public static class Map extends DataMap<StatsCollector.Map, UUID, StatCollector> {
      public Map() {
         super(new HashMap<>(), Adapters.UUID, CompoundAdapter.of(StatCollector::new));
      }
   }
}
