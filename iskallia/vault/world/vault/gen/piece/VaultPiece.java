package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class VaultPiece implements INBTSerializable<CompoundNBT> {
   public static final Map<ResourceLocation, Supplier<VaultPiece>> REGISTRY = new HashMap<>();
   protected ResourceLocation id;
   protected ResourceLocation template;
   protected MutableBoundingBox boundingBox;
   protected Rotation rotation;
   protected UUID uuid = UUID.randomUUID();

   protected VaultPiece(ResourceLocation id) {
      this.id = id;
   }

   protected VaultPiece(ResourceLocation id, ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
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

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public abstract void tick(ServerWorld var1, VaultRaid var2);

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.id.toString());
      nbt.func_74778_a("Template", this.template.toString());
      nbt.func_218657_a("BoundingBox", this.boundingBox.func_151535_h());
      nbt.func_74768_a("Rotation", this.rotation.ordinal());
      nbt.func_74778_a("UUID", this.uuid.toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
      this.template = new ResourceLocation(nbt.func_74779_i("Template"));
      this.boundingBox = new MutableBoundingBox(nbt.func_74759_k("BoundingBox"));
      this.rotation = Rotation.values()[nbt.func_74762_e("Rotation")];
      if (nbt.func_150297_b("UUID", 8)) {
         this.uuid = UUID.fromString(nbt.func_74779_i("UUID"));
      }
   }

   public boolean isInside(AxisAlignedBB box) {
      return AxisAlignedBB.func_216363_a(this.boundingBox).func_72326_a(box);
   }

   public boolean contains(BlockPos pos) {
      return this.boundingBox.func_175898_b(pos);
   }

   public BlockPos getMin() {
      return new BlockPos(this.boundingBox.field_78897_a, this.boundingBox.field_78895_b, this.boundingBox.field_78896_c);
   }

   public BlockPos getMax() {
      return new BlockPos(this.boundingBox.field_78893_d, this.boundingBox.field_78894_e, this.boundingBox.field_78892_f);
   }

   public Vector3i getCenter() {
      return this.getBoundingBox().func_215126_f();
   }

   public static VaultPiece fromNBT(CompoundNBT nbt) {
      ResourceLocation id = new ResourceLocation(nbt.func_74779_i("Id"));
      VaultPiece piece = REGISTRY.getOrDefault(id, () -> null).get();
      if (piece == null) {
         Vault.LOGGER.error("Piece <" + id + "> is not defined.");
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

   public static boolean shouldIgnoreCollision(JigsawPiece jigsaw) {
      for (VaultPiece piece : of(jigsaw, MutableBoundingBox.func_78887_a(), Rotation.NONE)) {
         if (piece instanceof VaultObelisk) {
            return true;
         }
      }

      return false;
   }

   public static List<VaultPiece> of(StructurePiece raw) {
      return (List<VaultPiece>)(!(raw instanceof AbstractVillagePiece)
         ? new ArrayList<>()
         : of(((AbstractVillagePiece)raw).func_214826_b(), raw.func_74874_b(), raw.func_214809_Y_()));
   }

   public static List<VaultPiece> of(JigsawPiece jigsaw, MutableBoundingBox box, Rotation rotation) {
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
               String path = template.func_110623_a();
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

                     if (path.startsWith("raid/enigma/rooms") || path.startsWith("final_vault/idona/rooms")) {
                        return new VaultRaidRoom(template, box, rotation);
                     }

                     if (path.startsWith("vault/enigma/tunnels") || path.startsWith("final_vault/idona/tunnels")) {
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

                     if (path.startsWith("final_vault/velara/rooms")
                        || path.startsWith("final_vault/tenos/rooms")
                        || path.startsWith("final_vault/wendarr/rooms")) {
                        return new VaultRoom(template, box, rotation);
                     }

                     if (path.startsWith("final_vault/velara/tunnels")
                        || path.startsWith("final_vault/tenos/tunnels")
                        || path.startsWith("final_vault/wendarr/tunnels")) {
                        return new VaultTunnel(template, box, rotation);
                     }

                     if (path.startsWith("final_vault") && path.contains("eye")) {
                        return new VaultGodEye(template, box, rotation);
                     }

                     if (path.startsWith("final_vault") && path.contains("boss")) {
                        return new FinalVaultBoss(template, box, rotation);
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
