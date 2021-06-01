package iskallia.vault.research;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class Restrictions {
   @Expose
   protected Map<Restrictions.Type, Boolean> restricts = new HashMap<>();

   private Restrictions() {
   }

   public Restrictions set(Restrictions.Type type, boolean restricts) {
      this.restricts.put(type, restricts);
      return this;
   }

   public boolean restricts(Restrictions.Type type) {
      return this.restricts.getOrDefault(type, false);
   }

   public static Restrictions forMods() {
      Restrictions restrictions = new Restrictions();
      restrictions.restricts.put(Restrictions.Type.USABILITY, false);
      restrictions.restricts.put(Restrictions.Type.CRAFTABILITY, false);
      restrictions.restricts.put(Restrictions.Type.HITTABILITY, false);
      restrictions.restricts.put(Restrictions.Type.BLOCK_INTERACTABILITY, false);
      restrictions.restricts.put(Restrictions.Type.ENTITY_INTERACTABILITY, false);
      return restrictions;
   }

   public static Restrictions forItems(boolean restricted) {
      Restrictions restrictions = new Restrictions();
      restrictions.restricts.put(Restrictions.Type.USABILITY, restricted);
      restrictions.restricts.put(Restrictions.Type.CRAFTABILITY, restricted);
      restrictions.restricts.put(Restrictions.Type.HITTABILITY, restricted);
      return restrictions;
   }

   public static Restrictions forBlocks(boolean restricted) {
      Restrictions restrictions = new Restrictions();
      restrictions.restricts.put(Restrictions.Type.HITTABILITY, restricted);
      restrictions.restricts.put(Restrictions.Type.BLOCK_INTERACTABILITY, restricted);
      return restrictions;
   }

   public static Restrictions forEntities(boolean restricted) {
      Restrictions restrictions = new Restrictions();
      restrictions.restricts.put(Restrictions.Type.HITTABILITY, restricted);
      restrictions.restricts.put(Restrictions.Type.ENTITY_INTERACTABILITY, restricted);
      return restrictions;
   }

   public static enum Type {
      USABILITY,
      CRAFTABILITY,
      HITTABILITY,
      BLOCK_INTERACTABILITY,
      ENTITY_INTERACTABILITY;
   }
}
