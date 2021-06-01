package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;

public class AbilitiesGUIConfig extends Config {
   @Expose
   private HashMap<String, SkillStyle> styles;

   @Override
   public String getName() {
      return "abilities_gui_styles";
   }

   public HashMap<String, SkillStyle> getStyles() {
      return this.styles;
   }

   @Override
   protected void reset() {
      this.styles = new HashMap<>();
      SkillStyle style = new SkillStyle(0, 0, 64, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.ABILITIES.NIGHT_VISION.getParentName(), style);
      style = new SkillStyle(50, 0, 96, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.ABILITIES.INVISIBILITY.getParentName(), style);
      style = new SkillStyle(100, 0, 16, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.VEIN_MINER.getParentName(), style);
      style = new SkillStyle(150, 0, 80, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.SELF_SUSTAIN.getParentName(), style);
      style = new SkillStyle(0, 50, 0, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.DASH.getParentName(), style);
      style = new SkillStyle(50, 50, 48, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.MEGA_JUMP.getParentName(), style);
      style = new SkillStyle(100, 50, 112, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.GHOST_WALK.getParentName(), style);
      style = new SkillStyle(150, 50, 128, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.RAMPAGE.getParentName(), style);
      style = new SkillStyle(0, 100, 144, 0);
      style.frameType = SkillFrame.STAR;
      this.styles.put(ModConfigs.ABILITIES.CLEANSE.getParentName(), style);
      style = new SkillStyle(50, 100, 160, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.ABILITIES.TANK.getParentName(), style);
      style = new SkillStyle(100, 100, 176, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.ABILITIES.EXECUTE.getParentName(), style);
      style = new SkillStyle(150, 100, 192, 0);
      style.frameType = SkillFrame.RECTANGULAR;
      this.styles.put(ModConfigs.ABILITIES.SUMMON_ETERNAL.getParentName(), style);
   }
}
