package iskallia.vault.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceBoundary {
   ResourceLocation resource;
   int u;
   int v;
   int w;
   int h;

   public ResourceBoundary(ResourceLocation resource, int u, int v, int w, int h) {
      this.resource = resource;
      this.u = u;
      this.v = v;
      this.w = w;
      this.h = h;
   }

   public ResourceLocation getResource() {
      return this.resource;
   }

   public int getU() {
      return this.u;
   }

   public int getV() {
      return this.v;
   }

   public int getWidth() {
      return this.w;
   }

   public int getHeight() {
      return this.h;
   }
}
