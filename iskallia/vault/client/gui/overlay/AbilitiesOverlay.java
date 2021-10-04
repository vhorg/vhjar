package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AbilitiesOverlay {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/abilities.png");

   @SubscribeEvent
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         List<AbilityNode<?, ?>> abilities = ClientAbilityData.getLearnedAbilityNodes();
         if (!abilities.isEmpty()) {
            AbilityGroup<?, ?> selectedAbilityGroup = ClientAbilityData.getSelectedAbility();
            if (selectedAbilityGroup != null) {
               AbilityNode<?, ?> selectAbilityNode = ClientAbilityData.getLearnedAbilityNode(selectedAbilityGroup);
               if (selectAbilityNode != null) {
                  int selectedAbilityIndex = ClientAbilityData.getIndexOf(selectedAbilityGroup);
                  if (selectedAbilityIndex != -1) {
                     int previousIndex = selectedAbilityIndex - 1;
                     if (previousIndex < 0) {
                        previousIndex += abilities.size();
                     }

                     AbilityNode<?, ?> previousAbility = abilities.get(previousIndex);
                     int nextIndex = selectedAbilityIndex + 1;
                     if (nextIndex >= abilities.size()) {
                        nextIndex -= abilities.size();
                     }

                     AbilityNode<?, ?> nextAbility = abilities.get(nextIndex);
                     MatrixStack matrixStack = event.getMatrixStack();
                     Minecraft minecraft = Minecraft.func_71410_x();
                     int bottom = minecraft.func_228018_at_().func_198087_p();
                     int barWidth = 62;
                     int barHeight = 22;
                     minecraft.func_213239_aq().func_76320_a("abilityBar");
                     matrixStack.func_227860_a_();
                     RenderSystem.enableBlend();
                     matrixStack.func_227861_a_(10.0, bottom - barHeight, 0.0);
                     minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
                     minecraft.field_71456_v.func_238474_b_(matrixStack, 0, 0, 1, 13, barWidth, barHeight);
                     minecraft.func_110434_K().func_110577_a(ABILITIES_RESOURCE);
                     int selectedCooldown = ClientAbilityData.getCooldown(selectedAbilityGroup);
                     int selectedMaxCooldown = ClientAbilityData.getMaxCooldown(selectedAbilityGroup);
                     String styleKey = selectAbilityNode.getSpecialization() != null
                        ? selectAbilityNode.getSpecialization()
                        : selectAbilityNode.getGroup().getParentName();
                     SkillStyle focusedStyle = ModConfigs.ABILITIES_GUI.getStyles().get(styleKey);
                     RenderSystem.color4f(1.0F, 1.0F, 1.0F, selectedCooldown > 0 ? 0.4F : 1.0F);
                     minecraft.field_71456_v.func_238474_b_(matrixStack, 23, 3, focusedStyle.u, focusedStyle.v, 16, 16);
                     if (selectedCooldown > 0) {
                        float cooldownPercent = (float)selectedCooldown / Math.max(1, selectedMaxCooldown);
                        int cooldownHeight = (int)(16.0F * cooldownPercent);
                        AbstractGui.func_238467_a_(matrixStack, 23, 3 + (16 - cooldownHeight), 39, 19, -1711276033);
                        RenderSystem.enableBlend();
                     }

                     int previousCooldown = ClientAbilityData.getCooldown(previousAbility.getGroup());
                     int previousMaxCooldown = ClientAbilityData.getMaxCooldown(previousAbility.getGroup());
                     RenderSystem.color4f(0.7F, 0.7F, 0.7F, 0.5F);
                     if (previousCooldown > 0) {
                        float cooldownPercent = (float)previousCooldown / Math.max(1, previousMaxCooldown);
                        int cooldownHeight = (int)(16.0F * cooldownPercent);
                        AbstractGui.func_238467_a_(matrixStack, 43, 3 + (16 - cooldownHeight), 59, 19, -1711276033);
                        RenderSystem.enableBlend();
                     }

                     String prevStyleKey = previousAbility.getSpecialization() != null
                        ? previousAbility.getSpecialization()
                        : previousAbility.getGroup().getParentName();
                     SkillStyle previousStyle = ModConfigs.ABILITIES_GUI.getStyles().get(prevStyleKey);
                     minecraft.field_71456_v.func_238474_b_(matrixStack, 43, 3, previousStyle.u, previousStyle.v, 16, 16);
                     int nextCooldown = ClientAbilityData.getCooldown(nextAbility.getGroup());
                     int nextMaxCooldown = ClientAbilityData.getMaxCooldown(nextAbility.getGroup());
                     if (nextCooldown > 0) {
                        float cooldownPercent = (float)nextCooldown / Math.max(1, nextMaxCooldown);
                        int cooldownHeight = (int)(16.0F * cooldownPercent);
                        AbstractGui.func_238467_a_(matrixStack, 3, 3 + (16 - cooldownHeight), 19, 19, -1711276033);
                        RenderSystem.enableBlend();
                     }

                     String nextStyleKey = nextAbility.getSpecialization() != null ? nextAbility.getSpecialization() : nextAbility.getGroup().getParentName();
                     SkillStyle nextStyle = ModConfigs.ABILITIES_GUI.getStyles().get(nextStyleKey);
                     minecraft.field_71456_v.func_238474_b_(matrixStack, 3, 3, nextStyle.u, nextStyle.v, 16, 16);
                     minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
                     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                     minecraft.field_71456_v
                        .func_238474_b_(matrixStack, 19, -1, 64 + (selectedCooldown > 0 ? 50 : (ClientAbilityData.isActive() ? 25 : 0)), 13, 24, 24);
                     matrixStack.func_227865_b_();
                     minecraft.func_213239_aq().func_76319_b();
                  }
               }
            }
         }
      }
   }
}
