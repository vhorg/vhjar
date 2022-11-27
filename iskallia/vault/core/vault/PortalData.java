package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.IdentifierList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class PortalData extends DataObject<PortalData> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<BlockPos> MIN = FieldKey.of("min", BlockPos.class)
      .with(Version.v1_0, Adapter.ofBlockPos(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<BlockPos> MAX = FieldKey.of("max", BlockPos.class)
      .with(Version.v1_0, Adapter.ofBlockPos(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<IdentifierList> TAGS = FieldKey.of("tags", IdentifierList.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), IdentifierList::create)
      .register(FIELDS);

   public PortalData() {
      this.set(TAGS, IdentifierList.create());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean hasTag(ResourceLocation tag) {
      return this.get(TAGS).contains(tag);
   }

   public boolean hasAnyTag(Iterable<ResourceLocation> tags) {
      for (ResourceLocation tag : tags) {
         if (this.hasTag(tag)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(BlockPos pos) {
      return this.get(MIN).getX() <= pos.getX()
         && this.get(MIN).getY() <= pos.getY()
         && this.get(MIN).getZ() <= pos.getZ()
         && this.get(MAX).getX() >= pos.getX()
         && this.get(MAX).getY() >= pos.getY()
         && this.get(MAX).getZ() >= pos.getZ();
   }

   public static class List extends DataList<PortalData.List, PortalData> {
      public List() {
         super(new ArrayList<>(), Adapter.ofCompound(PortalData::new));
      }
   }
}
