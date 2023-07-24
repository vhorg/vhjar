package iskallia.vault.network.message;

import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.HoldAbility;
import iskallia.vault.skill.ability.effect.spi.core.InstantAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityQuickselectMessage {
   public static final int RELEASED = 0;
   public static final int PRESSED = 1;
   private final String abilityName;
   private final int action;

   public AbilityQuickselectMessage(String abilityName, int action) {
      this.abilityName = abilityName;
      this.action = action;
   }

   public static void encode(AbilityQuickselectMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeUtf(pkt.abilityName);
      buffer.writeVarInt(pkt.action);
   }

   public static AbilityQuickselectMessage decode(FriendlyByteBuf buffer) {
      return new AbilityQuickselectMessage(buffer.readUtf(32767), buffer.readVarInt());
   }

   public static void handle(AbilityQuickselectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            SpecializedSkill skill = (SpecializedSkill)abilityTree.getForId(pkt.abilityName).orElse(null);
            if (skill != null && skill.isUnlocked()) {
               SkillContext ctx = SkillContext.of(sender);
               abilityTree.onQuickSelect(pkt.abilityName, ctx);
               Ability ability = (Ability)((TieredSkill)skill.getSpecialization()).getChild();
               if (skill.getId().equals(abilityTree.getSelected().getId())) {
                  if (!(ability instanceof InstantAbility) && !(ability instanceof ToggleAbility)) {
                     if (ability instanceof HoldAbility) {
                        if (pkt.action == 1) {
                           abilityTree.onKeyDown(ctx);
                        } else if (pkt.action == 0) {
                           abilityTree.onKeyUp(ctx);
                        }
                     }
                  } else if (pkt.action == 1) {
                     abilityTree.onKeyUp(ctx);
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
