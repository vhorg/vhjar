package iskallia.vault.antique.condition;

public enum DropConditionType {
   CHEST,
   ENTITY,
   REWARD_CRATE;

   public static DropConditionType byName(String name) {
      for (DropConditionType type : values()) {
         if (type.name().equalsIgnoreCase(name)) {
            return type;
         }
      }

      return null;
   }
}
