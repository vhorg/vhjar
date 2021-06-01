package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;

public class TalentsGUIConfig extends Config {
   @Expose
   private HashMap<String, SkillStyle> styles;

   @Override
   public String getName() {
      return "talents_gui_styles";
   }

   public HashMap<String, SkillStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      SkillStyle style = new SkillStyle(0, 0, 96, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.HASTE.getParentName(), style);
      style = new SkillStyle(70, 0, 48, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.REGENERATION.getParentName(), style);
      style = new SkillStyle(140, 0, 16, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.VAMPIRISM.getParentName(), style);
      style = new SkillStyle(210, 0, 112, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.RESISTANCE.getParentName(), style);
      style = new SkillStyle(280, 0, 128, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.STRENGTH.getParentName(), style);
      style = new SkillStyle(350, 0, 64, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.FIRE_RESISTANCE.getParentName(), style);
      style = new SkillStyle(0, 70, 144, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.SPEED.getParentName(), style);
      style = new SkillStyle(70, 70, 0, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.WATER_BREATHING.getParentName(), style);
      style = new SkillStyle(140, 70, 32, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.WELL_FIT.getParentName(), style);
      style = new SkillStyle(210, 70, 208, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.TWERKER.getParentName(), style);
      style = new SkillStyle(280, 70, 176, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.ELVISH.getParentName(), style);
      style = new SkillStyle(350, 70, 224, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.ANGEL.getParentName(), style);
      style = new SkillStyle(0, 140, 160, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.REACH.getParentName(), style);
      style = new SkillStyle(70, 140, 0, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.EXPERIENCED.getParentName(), style);
      style = new SkillStyle(140, 140, 240, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.PARRY.getParentName(), style);
      style = new SkillStyle(210, 140, 16, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.STONE_SKIN.getParentName(), style);
      style = new SkillStyle(280, 140, 32, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.UNBREAKABLE.getParentName(), style);
      style = new SkillStyle(350, 140, 48, 16);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.CRITICAL_STRIKE.getParentName(), style);
      style = new SkillStyle(0, 210, 64, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.LOOTER.getParentName(), style);
      style = new SkillStyle(70, 210, 80, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.CARAPACE.getParentName(), style);
      style = new SkillStyle(140, 210, 96, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.CHUNKY.getParentName(), style);
      style = new SkillStyle(210, 210, 112, 16);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.FRENZY.getParentName(), style);
      style = new SkillStyle(280, 210, 128, 16);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.TALENTS.STEP.getParentName(), style);
      style = new SkillStyle(350, 210, 144, 16);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.TALENTS.NINJA.getParentName(), style);
   }
}
