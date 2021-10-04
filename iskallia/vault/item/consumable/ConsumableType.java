package iskallia.vault.item.consumable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ConsumableType {
   BASIC,
   POWERUP;

   private static final Map<String, ConsumableType> STRING_TO_TYPE = Arrays.stream(values())
      .collect(Collectors.toMap(ConsumableType::toString, o -> (ConsumableType)o));

   public static ConsumableType fromString(String name) {
      return STRING_TO_TYPE.get(name);
   }

   @Override
   public String toString() {
      return this.name().toLowerCase();
   }
}
