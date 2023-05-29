package iskallia.vault.config.skillgate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.skill.SkillGates;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EitherSkillGate extends SkillGateType {
   protected List<SkillGateType> gates;

   public List<SkillGateType> getGates() {
      return this.gates;
   }

   public EitherSkillGate() {
   }

   public EitherSkillGate(List<SkillGateType> gates) {
      this.gates = gates;
   }

   @Override
   public boolean allows(String skillId) {
      return this.gates.stream().anyMatch(c -> c.allows(skillId));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray gatesJson = new JsonArray();
      this.gates.forEach(gate -> gate.writeJson().ifPresent(g -> {
         String typeKey = SkillGates.GATE_TYPE.getKey();
         String type = SkillGates.GATE_TYPE.getType(gate);
         g.addProperty(typeKey, type);
         gatesJson.add(g);
      }));
      json.add("gates", gatesJson);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.gates = new ArrayList<>();

      for (JsonElement gateJson : json.getAsJsonArray("gates")) {
         SkillGates.GATE_TYPE.readJson(gateJson).ifPresent(gate -> this.gates.add(gate));
      }
   }
}
