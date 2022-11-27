package iskallia.vault.core.world.generator.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.VaultPieceRegistry;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public abstract class VaultPiece extends DataObject<VaultPiece> {
   public static final VaultPieceRegistry REGISTRY = new VaultPieceRegistry();
   public static final FieldKey<VaultPiece.Children> CHILDREN = FieldKey.of("children", VaultPiece.Children.class)
      .with(Version.v1_0, Adapter.ofCompound(VaultPiece.Children::new), DISK.all().or(CLIENT.all()));

   public VaultPiece(Template template, PlacementSettings settings) {
      template.getTiles(Template.PLACEHOLDERS, settings).forEachRemaining(partialTile -> {});
   }

   public abstract VaultPiece.Key getKey();

   public static class Children extends DataList<VaultPiece.Children, VaultPiece> {
      public Children() {
         super(new ArrayList<>(), Adapter.ofRegistryValue(() -> VaultPiece.REGISTRY, VaultPiece::getKey, Supplier::get));
      }
   }

   public static class Key extends VersionedKey<VaultPiece.Key, Supplier<VaultPiece>> {
      protected Key(ResourceLocation id) {
         super(id);
      }

      public static VaultPiece.Key create(String id) {
         return new VaultPiece.Key(VaultMod.id(id));
      }

      public static VaultPiece.Key create(ResourceLocation id) {
         return new VaultPiece.Key(id);
      }
   }
}
