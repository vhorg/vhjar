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

public class MonsterDamageModifier extends RaidModifier {
   private static final UUID MOB_DAMAGE_INCREASE = UUID.fromString("827b6afb-f01b-498c-9206-970f85da732a");

   public MonsterDamageModifier(String name) {
      super(true, false, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
      AttributeInstance attr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attr != null) {
         attr.addPermanentModifier(new AttributeModifier(MOB_DAMAGE_INCREASE, "Raid Mob Damage Increase", value, Operation.MULTIPLY_BASE));
      }
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public Component getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new TextComponent("+" + percDisplay + "% increased Mob Damage").withStyle(ChatFormatting.RED);
   }
}
