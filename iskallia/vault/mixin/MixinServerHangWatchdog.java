package iskallia.vault.mixin;

import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.StringJoiner;
import net.minecraft.server.dedicated.ServerHangWatchdog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerHangWatchdog.class})
public class MixinServerHangWatchdog {
   @Redirect(
      method = {"run"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/StringBuilder;append(Ljava/lang/Object;)Ljava/lang/StringBuilder;"
      )
   )
   public StringBuilder appendThreadInfo(StringBuilder sb, Object obj) {
      if (obj instanceof ThreadInfo) {
         StackTraceElement[] trace = ((ThreadInfo)obj).getStackTrace();
         StringJoiner joiner = new StringJoiner("\n");
         Arrays.stream(trace).map(StackTraceElement::toString).forEach(joiner::add);
         sb.append(obj).append("\n");
         sb.append("Full Trace:\n");
         sb.append(joiner.toString());
         return sb;
      } else {
         return sb.append(obj);
      }
   }
}
