package iskallia.vault.mixin;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity.UpdateCommand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({CUpdateStructureBlockPacket.class})
public class MixinCUpdateStructureBlockPacket {
   @Shadow
   private BlockPos field_210391_a;
   @Shadow
   private UpdateCommand field_210392_b;
   @Shadow
   private StructureMode field_210393_c;
   @Shadow
   private String field_210394_d;
   @Shadow
   private BlockPos field_210395_e;
   @Shadow
   private BlockPos field_210396_f;
   @Shadow
   private Mirror field_210397_g;
   @Shadow
   private Rotation field_210398_h;
   @Shadow
   private String field_210399_i;
   @Shadow
   private float field_210403_m;
   @Shadow
   private long field_210404_n;
   @Shadow
   private boolean field_210400_j;
   @Shadow
   private boolean field_210401_k;
   @Shadow
   private boolean field_210402_l;

   @Overwrite
   public void func_148837_a(PacketBuffer buf) throws IOException {
      this.field_210391_a = buf.func_179259_c();
      this.field_210392_b = (UpdateCommand)buf.func_179257_a(UpdateCommand.class);
      this.field_210393_c = (StructureMode)buf.func_179257_a(StructureMode.class);
      this.field_210394_d = buf.func_150789_c(32767);
      int i = 48;
      int j = 48;
      this.field_210395_e = new BlockPos(
         MathHelper.func_76125_a(buf.func_150792_a(), -48, 48),
         MathHelper.func_76125_a(buf.func_150792_a(), -48, 48),
         MathHelper.func_76125_a(buf.func_150792_a(), -48, 48)
      );
      this.field_210396_f = new BlockPos(
         MathHelper.func_76125_a(buf.func_150792_a(), 0, 528),
         MathHelper.func_76125_a(buf.func_150792_a(), 0, 528),
         MathHelper.func_76125_a(buf.func_150792_a(), 0, 528)
      );
      this.field_210397_g = (Mirror)buf.func_179257_a(Mirror.class);
      this.field_210398_h = (Rotation)buf.func_179257_a(Rotation.class);
      this.field_210399_i = buf.func_150789_c(12);
      this.field_210403_m = MathHelper.func_76131_a(buf.readFloat(), 0.0F, 1.0F);
      this.field_210404_n = buf.func_179260_f();
      int k = buf.func_150792_a();
      this.field_210400_j = (k & 1) != 0;
      this.field_210401_k = (k & 2) != 0;
      this.field_210402_l = (k & 4) != 0;
   }

   @Redirect(
      method = {"writePacketData"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/PacketBuffer;writeByte(I)Lio/netty/buffer/ByteBuf;"
      )
   )
   private ByteBuf writePacketData(PacketBuffer buf, int value) {
      buf.func_150787_b(value);
      return buf;
   }
}
