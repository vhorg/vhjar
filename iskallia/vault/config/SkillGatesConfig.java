package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.skillgate.ConstantSkillGate;
import iskallia.vault.config.skillgate.EitherSkillGate;
import iskallia.vault.skill.SkillGates;
import java.util.Arrays;

public class SkillGatesConfig extends Config {
   @Expose
   private SkillGates SKILL_GATES;

   @Override
   public String getName() {
      return "skill_gates";
   }

   public SkillGates getGates() {
      return this.SKILL_GATES;
   }

   @Override
   protected void reset() {
      this.SKILL_GATES = new SkillGates();
      SkillGates.Entry gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Storage Noob"));
      this.SKILL_GATES.addEntry("Storage Master", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Storage Master"));
      this.SKILL_GATES.addEntry("Storage Refined", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Storage Refined"));
      this.SKILL_GATES.addEntry("Storage Energistic", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Storage Energistic"));
      this.SKILL_GATES.addEntry("Storage Enthusiast", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Decorator"));
      this.SKILL_GATES.addEntry("Decorator Pro", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(new ConstantSkillGate("Tech Freak"));
      this.SKILL_GATES.addEntry("Nuclear Power", gateEntry);
      gateEntry = new SkillGates.Entry();
      gateEntry.setDependsOn(
         new ConstantSkillGate("Other Test Skill"), new EitherSkillGate(Arrays.asList(new ConstantSkillGate("A"), new ConstantSkillGate("B")))
      );
      this.SKILL_GATES.addEntry("Test Skill", gateEntry);
   }
}
