package iskallia.vault.item.tool;

import iskallia.vault.VaultMod;
import net.minecraft.Util;

public enum ToolMaterial {
   CHROMATIC_IRON_INGOT("chromatic_iron_ingot", 0, 100, 9.0F, 4096, 1),
   CHROMATIC_STEEL_INGOT("chromatic_steel_ingot", 20, 150, 9.0F, 4096, 2),
   VAULTERITE_INGOT("vaulterite_ingot", 50, 200, 9.0F, 4096, 3),
   VAULT_ALLOY("vault_alloy", 50, 200, 9.0F, 4096, 4),
   BLACK_CHROMATIC_STEEL_INGOT("black_chromatic_steel_ingot", 65, 250, 9.0F, 4096, 5),
   ECHOING_INGOT("echoing_ingot", 80, 350, 9.0F, 4096, 5),
   OMEGA_POG("omega_pog", 90, 500, 9.0F, 4096, 7);

   private final String id;
   private final String description;
   private final int level;
   private final int capacity;
   private final float miningSpeed;
   private final int durability;
   private final int repairs;

   private ToolMaterial(String id, int level, int capacity, float miningSpeed, int durability, int repairs) {
      this.id = id;
      this.description = Util.makeDescriptionId("item", VaultMod.id("tool.material." + id));
      this.level = level;
      this.capacity = capacity;
      this.miningSpeed = miningSpeed;
      this.durability = durability;
      this.repairs = repairs;
   }

   public String getId() {
      return this.id;
   }

   public String getDescription() {
      return this.description;
   }

   public int getLevel() {
      return this.level;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public float getMiningSpeed() {
      return this.miningSpeed;
   }

   public int getDurability() {
      return this.durability;
   }

   public int getRepairs() {
      return this.repairs;
   }
}
