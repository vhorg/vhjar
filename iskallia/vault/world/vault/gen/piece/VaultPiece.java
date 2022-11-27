package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import iskallia.vault.world.vault.VaultRaid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultPiece implements INBTSerializable<CompoundTag> {
   public static final Map<ResourceLocation, Supplier<VaultPiece>> REGISTRY = new HashMap<>();
   protected ResourceLocation id;
   protected ResourceLocation template;
   protected BoundingBox boundingBox;
   protected Rotation rotation;
   protected UUID uuid = UUID.randomUUID();

   protected VaultPiece(ResourceLocation id) {
      this.id = id;
   }

   protected VaultPiece(ResourceLocation id, ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      this.id = id;
      this.template = template;
      this.boundingBox = boundingBox;
      this.rotation = rotation;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public ResourceLocation getTemplate() {
      return this.template;
   }

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public abstract void tick(ServerLevel var1, VaultRaid var2);

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.id.toString());
      nbt.putString("Template", this.template.toString());
      nbt.put("BoundingBox", NBTHelper.serializeBoundingBox(this.boundingBox));
      nbt.putInt("Rotation", this.rotation.ordinal());
      nbt.putString("UUID", this.uuid.toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
      this.template = new ResourceLocation(nbt.getString("Template"));
      this.boundingBox = NBTHelper.deserializeBoundingBox(nbt.getIntArray("BoundingBox"));
      this.rotation = Rotation.values()[nbt.getInt("Rotation")];
      if (nbt.contains("UUID", 8)) {
         this.uuid = UUID.fromString(nbt.getString("UUID"));
      }
   }

   public boolean isInside(AABB box) {
      return AABB.of(this.boundingBox).intersects(box);
   }

   public boolean contains(BlockPos pos) {
      return this.boundingBox.isInside(pos);
   }

   public static VaultPiece fromNBT(CompoundTag nbt) {
      ResourceLocation id = new ResourceLocation(nbt.getString("Id"));
      VaultPiece piece = REGISTRY.getOrDefault(id, () -> null).get();
      if (piece == null) {
         VaultMod.LOGGER.error("Piece <" + id + "> is not defined.");
         return null;
      } else {
         try {
            piece.deserializeNBT(nbt);
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         return piece;
      }
   }

   public static boolean shouldIgnoreCollision(StructurePoolElement jigsaw) {
      for (VaultPiece piece : of(jigsaw, BoundingBox.infinite(), Rotation.NONE)) {
         if (piece instanceof VaultObelisk) {
            return true;
         }
      }

      return false;
   }

   public static List<VaultPiece> of(StructurePiece raw) {
      return (List<VaultPiece>)(!(raw instanceof PoolElementStructurePiece)
         ? new ArrayList<>()
         : of(((PoolElementStructurePiece)raw).getElement(), raw.getBoundingBox(), raw.getRotation()));
   }

   public static List<VaultPiece> of(StructurePoolElement jigsaw, BoundingBox box, Rotation rotation) {
      List<PalettedSinglePoolElement> elements = new ArrayList<>();
      if (jigsaw instanceof PalettedSinglePoolElement) {
         elements.add((PalettedSinglePoolElement)jigsaw);
      } else if (jigsaw instanceof PalettedListPoolElement) {
         ((PalettedListPoolElement)jigsaw).getElements().forEach(jigsawPiece -> {
            if (jigsawPiece instanceof PalettedSinglePoolElement) {
               elements.add((PalettedSinglePoolElement)jigsawPiece);
            }
         });
      }

      return elements.stream()
         .map(
            element -> {
               ResourceLocation template = (ResourceLocation)element.getTemplate().left().get();
               String path = template.getPath();
               if (path.startsWith("vault/prefab/decor/generic/obelisk")) {
                  return new VaultObelisk(template, box, rotation);
               } else {
                  if (path.startsWith("vault/enigma/rooms")) {
                     if (path.contains("layer0")) {
                        return new VaultRoom(template, box, rotation);
                     }
                  } else {
                     if (path.startsWith("architect_event/enigma/rooms")) {
                        return new VaultRoom(template, box, rotation);
                     }

                     if (path.startsWith("raid/enigma/rooms")) {
                        return new VaultRaidRoom(template, box, rotation);
                     }

                     if (path.startsWith("vault/enigma/tunnels")) {
                        return new VaultTunnel(template, box, rotation);
                     }

                     if (path.startsWith("vault/enigma/starts")
                        || path.startsWith("architect_event/enigma/starts")
                        || path.startsWith("raid/enigma/starts")
                        || path.startsWith("trove/enigma/starts")) {
                        return new VaultStart(template, box, rotation);
                     }

                     if (path.startsWith("vault/enigma/treasure")) {
                        return new VaultTreasure(template, box, rotation);
                     }

                     if (path.startsWith("final_vault/starts")) {
                        return new FinalVaultLobby(template, box, rotation);
                     }

                     if (path.startsWith("final_vault/portals")) {
                        return new VaultPortal(template, box, rotation);
                     }
                  }

                  return null;
               }
            }
         )
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
   }
}
