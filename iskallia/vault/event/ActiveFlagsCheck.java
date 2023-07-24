package iskallia.vault.event;

public class ActiveFlagsCheck {
   public static boolean isAnyFlagActiveLuckyHit() {
      if (ActiveFlags.IS_CHAINING_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_AOE_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_TOTEM_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_CHARMED_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_DOT_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_REFLECT_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_EFFECT_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_JAVELIN_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_FIRESHOT_ATTACKING.isSet()) {
         return true;
      } else if (ActiveFlags.IS_SMITE_ATTACKING.isSet()) {
         return true;
      } else {
         return ActiveFlags.IS_SMITE_BASE_ATTACKING.isSet() ? true : ActiveFlags.IS_GLACIAL_SHATTER_ATTACKING.isSet();
      }
   }

   public static boolean checkIfFullSwingAttack() {
      return !ActiveFlags.IS_SMITE_BASE_ATTACKING.isSet() && !ActiveFlags.IS_FIRESHOT_ATTACKING.isSet() && !ActiveFlags.IS_TOTEM_ATTACKING.isSet();
   }
}
