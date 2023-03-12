package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public abstract class PortalLogic extends DataObject<PortalLogic> implements ISupplierKey<PortalLogic> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<PortalData.List> DATA = FieldKey.of("data", PortalData.List.class)
      .with(Version.v1_0, CompoundAdapter.of(PortalData.List::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public PortalLogic() {
      this.set(DATA, new PortalData.List());
   }

   public Stream<PortalData> getPortals() {
      return this.get(DATA).stream();
   }

   public Stream<PortalData> getPortals(ResourceLocation tag) {
      return this.get(DATA).stream().filter(data -> data.get(PortalData.TAGS).contains(tag));
   }

   public void addPortal(Template template, PlacementSettings settings, Collection<ResourceLocation> tags) {
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int minZ = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;
      int maxZ = Integer.MIN_VALUE;
      Iterator<PartialTile> it = template.getTiles(Template.VAULT_PORTALS, settings);

      while (it.hasNext()) {
         PartialTile tile = it.next();
         if (tile.getPos().getX() < minX) {
            minX = tile.getPos().getX();
         }

         if (tile.getPos().getY() < minY) {
            minY = tile.getPos().getY();
         }

         if (tile.getPos().getZ() < minZ) {
            minZ = tile.getPos().getZ();
         }

         if (tile.getPos().getX() > maxX) {
            maxX = tile.getPos().getX();
         }

         if (tile.getPos().getY() > maxY) {
            maxY = tile.getPos().getY();
         }

         if (tile.getPos().getZ() > maxZ) {
            maxZ = tile.getPos().getZ();
         }
      }

      PortalData data = new PortalData().set(PortalData.MIN, new BlockPos(minX, minY, minZ)).set(PortalData.MAX, new BlockPos(maxX, maxY, maxZ));
      data.get(PortalData.TAGS).addAll(tags);
      this.get(DATA).add(data);
   }
}
