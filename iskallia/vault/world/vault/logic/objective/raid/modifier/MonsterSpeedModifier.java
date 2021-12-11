package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import java.util.UUID;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class MonsterSpeedModifier extends RaidModifier {
   private static final UUID MOB_SPEED_INCREASE = UUID.fromString("83efc139-b73b-4c14-82e6-fb624665fb59");

   public MonsterSpeedModifier(String name) {
      super(true, false, name);
   }

   @Override
   public void affectRaidMob(MobEntity mob, float value) {
      ModifiableAttributeInstance attr = mob.func_110148_a(Attributes.field_233821_d_);
      if (attr != null) {
         attr.func_233769_c_(new AttributeModifier(MOB_SPEED_INCREASE, "Raid Mob Speed", value, Operation.MULTIPLY_BASE));
      }
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public ITextComponent getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new StringTextComponent("+" + percDisplay + "% increased Mob Speed").func_240699_a_(TextFormatting.RED);
   }
}
