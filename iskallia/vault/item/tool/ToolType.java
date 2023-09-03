package iskallia.vault.item.tool;

import com.google.common.base.Functions;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ToolType {
   PICK("pick"),
   AXE("axe"),
   SHOVEL("shovel"),
   HAMMER("hammer"),
   SICKLE("sickle"),
   CUTTER("cutter", PICK, AXE),
   MATTOCK("mattock", PICK, SHOVEL),
   EXCAVATOR("excavator", AXE, SHOVEL),
   PAXEL("paxel", PICK, AXE, SHOVEL),
   CLAW_HAMMER("claw_hammer", HAMMER, PICK),
   SPLITTING_MAUL("splitting_maul", HAMMER, AXE),
   MALLET("mallet", HAMMER, SHOVEL),
   CUTTING_HAMMER("cutting_hammer", HAMMER, PICK, AXE),
   BRICK_HAMMER("brick_hammer", HAMMER, PICK, SHOVEL),
   SLEDGEHAMMER("sledgehammer", HAMMER, AXE, SHOVEL),
   SHATTERER("shatterer", HAMMER, PICK, AXE, SHOVEL),
   SCYTHE("scythe", SICKLE, PICK),
   MACHETE("machete", SICKLE, AXE),
   PITCHFORK("pitchfork", SICKLE, SHOVEL),
   BILLHOOK("billhook", SICKLE, PICK, AXE),
   FIELD_SHOVEL("field_shovel", SICKLE, PICK, SHOVEL),
   CLEAVER("cleaver", SICKLE, AXE, SHOVEL),
   REAPER("reaper", SICKLE, PICK, AXE, SHOVEL);

   private static final Map<Integer, ToolType> PACKED_TO_TYPE = Arrays.stream(values()).collect(Collectors.toMap(ToolType::getPacked, Functions.identity()));
   private final String id;
   private final String description;
   private final ToolType[] parents;
   private final int packed;

   private ToolType(String id, ToolType... parents) {
      this.id = id;
      this.description = Util.makeDescriptionId("item", VaultMod.id("tool." + id));
      this.parents = parents;
      this.packed = parents.length == 0 ? 1 << this.ordinal() : Arrays.stream(this.parents).mapToInt(value -> 1 << value.ordinal()).sum();
   }

   public String getId() {
      return this.id;
   }

   public String getDescription() {
      return this.description;
   }

   public ToolType[] getParents() {
      return this.parents;
   }

   public List<VaultGearAttribute<Boolean>> getAttributes() {
      return Arrays.stream(this.parents.length == 0 ? new ToolType[]{this} : this.parents).map(type -> {
         return switch (type) {
            case PICK -> ModGearAttributes.PICKING;
            case AXE -> ModGearAttributes.AXING;
            case SHOVEL -> ModGearAttributes.SHOVELLING;
            case HAMMER -> ModGearAttributes.HAMMERING;
            case SICKLE -> ModGearAttributes.REAPING;
            default -> throw new UnsupportedOperationException("wtf is this parent");
         };
      }).toList();
   }

   public int getPacked() {
      return this.packed;
   }

   @Nullable
   public static ToolType of(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      int packed = (data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue()) ? 1 : 0)
         | (data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue()) ? 2 : 0)
         | (data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue()) ? 4 : 0)
         | (data.get(ModGearAttributes.HAMMERING, VaultGearAttributeTypeMerger.anyTrue()) ? 8 : 0)
         | (data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue()) ? 16 : 0);
      return PACKED_TO_TYPE.get(packed);
   }

   public boolean has(ToolType type) {
      return this == type || Arrays.stream(this.parents).anyMatch(toolType -> toolType == type);
   }
}
