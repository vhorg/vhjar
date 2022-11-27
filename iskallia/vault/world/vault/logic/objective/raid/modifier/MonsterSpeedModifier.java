package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MonsterSpeedModifier extends RaidModifier {
   private static final UUID MOB_SPEED_INCREASE = UUID.fromString("83efc139-b73b-4c14-82e6-fb624665fb59");

   public MonsterSpeedModifier(String name) {
      super(true, false, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
      AttributeInstance attr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attr != null) {
         attr.addPermanentModifier(new AttributeModifier(MOB_SPEED_INCREASE, "Raid Mob Speed", value, Operation.MULTIPLY_BASE));
      }
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public Component getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new TextComponent("+" + percDisplay + "% increased Mob Speed").withStyle(ChatFormatting.RED);
   }
}
