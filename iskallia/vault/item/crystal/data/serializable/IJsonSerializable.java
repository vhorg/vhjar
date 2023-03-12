package iskallia.vault.item.crystal.data.serializable;

import com.google.gson.JsonElement;
import java.util.Optional;

public interface IJsonSerializable<J extends JsonElement> {
   Optional<J> writeJson();

   void readJson(J var1);
}
