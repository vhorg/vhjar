package iskallia.vault.config.skillgate;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import java.util.Optional;

public class ConstantSkillGate extends SkillGateType {
   @Expose
   protected String id;

   public String getId() {
      return this.id;
   }

   public ConstantSkillGate() {
   }

   public ConstantSkillGate(String skillId) {
      this.id = skillId;
   }

   @Override
   public boolean allows(String skillId) {
      return skillId.equals(this.id);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      json.addProperty("id", this.id);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.id = json.get("id").getAsString();
   }
}
