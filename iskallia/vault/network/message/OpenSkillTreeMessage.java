package iskallia.vault.network.message;

import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class OpenSkillTreeMessage {
   public static void encode(OpenSkillTreeMessage message, PacketBuffer buffer) {
   }

   public static OpenSkillTreeMessage decode(PacketBuffer buffer) {
      return new OpenSkillTreeMessage();
   }

   public static void handle(OpenSkillTreeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity sender = context.getSender();
         if (sender != null) {
            PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get((ServerWorld)sender.field_70170_p);
            final AbilityTree abilityTree = playerAbilitiesData.getAbilities(sender);
            PlayerTalentsData playerTalentsData = PlayerTalentsData.get((ServerWorld)sender.field_70170_p);
            final TalentTree talentTree = playerTalentsData.getTalents(sender);
            PlayerResearchesData playerResearchesData = PlayerResearchesData.get((ServerWorld)sender.field_70170_p);
            final ResearchTree researchTree = playerResearchesData.getResearches(sender);
            NetworkHooks.openGui(sender, new INamedContainerProvider() {
               public ITextComponent func_145748_c_() {
                  return new TranslationTextComponent("container.vault.ability_tree");
               }

               @Nullable
               public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  return new SkillTreeContainer(i, abilityTree, talentTree, researchTree);
               }
            }, buffer -> {
               buffer.func_150786_a(abilityTree.serializeNBT());
               buffer.func_150786_a(talentTree.serializeNBT());
               buffer.func_150786_a(researchTree.serializeNBT());
            });
         }
      });
      context.setPacketHandled(true);
   }
}
