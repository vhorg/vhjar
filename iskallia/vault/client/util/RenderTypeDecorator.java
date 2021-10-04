package iskallia.vault.client.util;

import java.util.Optional;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderTypeDecorator extends RenderType {
   private final RenderType decorated;
   private final Runnable afterSetup;
   private final Runnable beforeClean;

   private RenderTypeDecorator(RenderType type, Runnable afterSetup, Runnable beforeClean) {
      super(type.toString(), type.func_228663_p_(), type.func_228664_q_(), type.func_228662_o_(), type.func_228665_s_(), false, () -> {}, () -> {});
      this.decorated = type;
      this.afterSetup = afterSetup;
      this.beforeClean = beforeClean;
   }

   public static RenderTypeDecorator decorate(RenderType type) {
      return new RenderTypeDecorator(type, () -> {}, () -> {});
   }

   public static RenderTypeDecorator decorate(RenderType type, Runnable setup, Runnable clean) {
      return new RenderTypeDecorator(type, setup, clean);
   }

   public void func_228547_a_() {
      this.decorated.func_228547_a_();
      this.afterSetup.run();
   }

   public void func_228549_b_() {
      this.beforeClean.run();
      this.decorated.func_228549_b_();
   }

   public void func_228631_a_(BufferBuilder buf, int sortOffsetX, int sortOffsetY, int sortOffsetZ) {
      super.func_228631_a_(buf, sortOffsetX, sortOffsetY, sortOffsetZ);
   }

   public String toString() {
      return this.decorated.toString();
   }

   public int func_228662_o_() {
      return this.decorated.func_228662_o_();
   }

   public VertexFormat func_228663_p_() {
      return this.decorated.func_228663_p_();
   }

   public int func_228664_q_() {
      return this.decorated.func_228664_q_();
   }

   public Optional<RenderType> func_225612_r_() {
      return this.decorated.func_225612_r_().map(type -> decorate(type, this.afterSetup, this.beforeClean));
   }

   public boolean func_230041_s_() {
      return this.decorated.func_230041_s_();
   }

   public boolean func_228665_s_() {
      return this.decorated.func_228665_s_();
   }

   public Optional<RenderType> func_230169_u_() {
      return Optional.of(this);
   }
}
