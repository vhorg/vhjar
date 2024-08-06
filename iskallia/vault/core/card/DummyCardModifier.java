package iskallia.vault.core.card;

import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import java.util.ArrayList;
import java.util.List;

public class DummyCardModifier extends CardModifier<CardModifier.Config> {
   public DummyCardModifier() {
      super(new CardModifier.Config());
   }

   public DummyCardModifier(CardModifier.Config config) {
      super(config);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getSnapshotAttributes(int tier) {
      return new ArrayList<>();
   }

   @Override
   public int getHighlightColor() {
      return 0;
   }
}
