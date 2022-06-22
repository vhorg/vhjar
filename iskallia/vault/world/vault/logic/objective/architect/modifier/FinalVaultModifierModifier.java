package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.modifier.VaultModifier;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class FinalVaultModifierModifier extends VoteModifier {
   @Expose
   private final String addedModifier;

   public FinalVaultModifierModifier(String name, String description, String addedModifier) {
      super(name, description, 0);
      this.addedModifier = addedModifier;
   }

   @Override
   public void onApply(ArchitectObjective objective, VaultRaid vault, ServerWorld world) {
      super.onApply(objective, vault, world);
      VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(this.addedModifier);
      if (modifier != null) {
         ITextComponent ct = new StringTextComponent("Added ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(modifier.getNameComponent());
         vault.getModifiers().addPermanentModifier(modifier);
         vault.getPlayers().forEach(vPlayer -> {
            modifier.apply(vault, vPlayer, world, world.func_201674_k());
            vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
         });
      }
   }
}
