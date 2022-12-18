package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CompletionProperties extends TaskProperties {
   private String id;

   public CompletionProperties(String id, List<ResourceLocation> validDimensions, boolean isVaultOnly, double amount) {
      super(TaskRegistry.COMPLETION, validDimensions, isVaultOnly, amount);
      this.id = id;
   }

   public CompletionProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public String getId() {
      return this.id;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("id", this.id);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.id = tag.getString("id");
      String var2 = this.id;

      this.id = switch (var2) {
         case "the_vault:vault" -> "vault";
         case "the_vault:boss" -> "boss";
         case "the_vault:cake" -> "cake";
         case "the_vault:scavenger" -> "scavenger";
         case "the_vault:monolith" -> "monolith";
         default -> this.id;
      };
   }
}
