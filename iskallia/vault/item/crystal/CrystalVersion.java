package iskallia.vault.item.crystal;

import iskallia.vault.core.IVersion;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;

public enum CrystalVersion implements IVersion<CrystalVersion> {
   LEGACY(CrystalVersion::convertLegacyTo1),
   v1(CrystalVersion::convert1to2),
   v2(CrystalVersion::convert2to3);

   private final Consumer<CompoundTag> fixer;

   private CrystalVersion(Consumer<CompoundTag> fixer) {
      this.fixer = fixer;
   }

   public CrystalVersion getThis() {
      return this;
   }

   public CrystalVersion next() {
      return this == latest() ? this : values()[this.ordinal() + 1];
   }

   public static CrystalVersion latest() {
      return values()[values().length - 1];
   }

   public static CrystalVersion oldest() {
      return values()[0];
   }

   static CompoundTag upgrade(CompoundTag nbt) {
      for (CrystalVersion version = CrystalData.VERSION.readNbt(nbt.get("Version")).orElse(LEGACY); version != latest(); version = version.next()) {
         version.fixer.accept(nbt);
      }

      return nbt;
   }

   static void convertLegacyTo1(CompoundTag nbt) {
      nbt.putInt("Version", v1.ordinal());
      int instabilityCounter = nbt.getInt("instabilityCounter");
      float instability = instabilityCounter <= 3 ? 0.0F : Math.min((instabilityCounter - 3) * 0.05F, 0.9F);
      nbt.putFloat("Instability", nbt.getFloat("Instability") + instability);
      nbt.putBoolean("Exhausted", !nbt.getBoolean("canBeModified"));
      CompoundTag modifiers = new CompoundTag();
      modifiers.put("List", nbt.getList("Modifiers", 10));
      modifiers.putBoolean("RandomModifiers", !nbt.getBoolean("preventsRandomModifiers"));
      modifiers.putBoolean("Clarity", nbt.getBoolean("clarity"));
      nbt.put("Modifiers", modifiers);
   }

   static void convert1to2(CompoundTag nbt) {
      nbt.putInt("Version", v2.ordinal());
      if (nbt.contains("Modifiers", 10)) {
         CompoundTag modifiers = nbt.getCompound("Modifiers");
         if (!modifiers.contains("type", 8)) {
            modifiers.putString("type", "default");
         }
      }
   }

   private static void convert2to3(CompoundTag tag) {
   }
}
