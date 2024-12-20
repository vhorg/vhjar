package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.generator.layout.VaultGridLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;

public class LayoutTemplateGenerationEvent extends Event<LayoutTemplateGenerationEvent, LayoutTemplateGenerationEvent.Data> {
   public LayoutTemplateGenerationEvent() {
   }

   protected LayoutTemplateGenerationEvent(LayoutTemplateGenerationEvent parent) {
      super(parent);
   }

   public LayoutTemplateGenerationEvent createChild() {
      return new LayoutTemplateGenerationEvent(this);
   }

   public LayoutTemplateGenerationEvent.Data invoke(
      VaultGridLayout layout,
      Vault vault,
      RegionPos region,
      RandomSource random,
      PlacementSettings settings,
      VaultLayout.PieceType pieceType,
      Template template
   ) {
      return this.invoke(new LayoutTemplateGenerationEvent.Data(layout, vault, region, random, settings, pieceType, template));
   }

   public static class Data {
      private final VaultGridLayout layout;
      private final Vault vault;
      private final RegionPos region;
      private final RandomSource random;
      private final PlacementSettings settings;
      private VaultLayout.PieceType pieceType;
      private Template template;

      public Data(
         VaultGridLayout layout,
         Vault vault,
         RegionPos region,
         RandomSource random,
         PlacementSettings settings,
         VaultLayout.PieceType pieceType,
         Template template
      ) {
         this.layout = layout;
         this.vault = vault;
         this.region = region;
         this.random = random;
         this.settings = settings;
         this.pieceType = pieceType;
         this.template = template;
      }

      public VaultGridLayout getLayout() {
         return this.layout;
      }

      public Vault getVault() {
         return this.vault;
      }

      public RegionPos getRegion() {
         return this.region;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public PlacementSettings getSettings() {
         return this.settings;
      }

      public VaultLayout.PieceType getPieceType() {
         return this.pieceType;
      }

      public Template getTemplate() {
         return this.template;
      }

      public void setPieceType(VaultLayout.PieceType pieceType) {
         this.pieceType = pieceType;
      }

      public void setTemplate(Template template) {
         this.template = template;
      }
   }
}
