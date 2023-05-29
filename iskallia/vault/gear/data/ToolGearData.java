package iskallia.vault.gear.data;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.tool.ToolMaterial;

public class ToolGearData extends VaultGearData {
   public ToolGearData() {
   }

   public ToolGearData(BitBuffer buf) {
      this.read(buf);
   }

   @Override
   protected void read(BitBuffer buf) {
      super.read(buf);
      if (GearDataVersion.V0_4.isLaterThan(this.version)) {
         if (!this.has(ModGearAttributes.TOOL_MATERIAL) || !this.has(ModGearAttributes.TOOL_CAPACITY)) {
            return;
         }

         ToolMaterial toolMaterial = this.getFirstValue(ModGearAttributes.TOOL_MATERIAL).orElse(null);
         int capacity = this.getFirstValue(ModGearAttributes.TOOL_CAPACITY).orElse(0);
         int additional = 0;
         if (toolMaterial == ToolMaterial.ECHOING_INGOT) {
            additional = 50;
         } else if (toolMaterial == ToolMaterial.OMEGA_POG) {
            additional = 200;
         }

         this.updateAttribute(ModGearAttributes.TOOL_CAPACITY, Integer.valueOf(capacity + additional));
      }
   }
}
