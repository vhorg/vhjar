package iskallia.vault.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;

public record AltarParticleOptions(ParticleType<AltarParticleOptions> particleType, Vector3f color) implements ParticleOptions {
   public static final Deserializer<AltarParticleOptions> DESERIALIZER = new Deserializer<AltarParticleOptions>() {
      @Nonnull
      @ParametersAreNonnullByDefault
      public AltarParticleOptions fromCommand(ParticleType<AltarParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
         return new AltarParticleOptions(particleType, AltarParticleOptions.readVector3f(reader));
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public AltarParticleOptions fromNetwork(ParticleType<AltarParticleOptions> particleType, FriendlyByteBuf buffer) {
         return new AltarParticleOptions(particleType, new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
      }
   };

   public static Codec<AltarParticleOptions> codec(ParticleType<AltarParticleOptions> particleType) {
      return RecordCodecBuilder.create(
         c -> c.group(Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color)).apply(c, color -> new AltarParticleOptions(particleType, color))
      );
   }

   @Nonnull
   public ParticleType<AltarParticleOptions> getType() {
      return this.particleType;
   }

   public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
      buffer.writeFloat(this.color.x());
      buffer.writeFloat(this.color.y());
      buffer.writeFloat(this.color.z());
   }

   @Nonnull
   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", this.getType().getRegistryName(), this.color.x(), this.color.y(), this.color.z());
   }

   private static Vector3f readVector3f(StringReader stringReader) throws CommandSyntaxException {
      stringReader.expect(' ');
      float f0 = stringReader.readFloat();
      stringReader.expect(' ');
      float f1 = stringReader.readFloat();
      stringReader.expect(' ');
      float f2 = stringReader.readFloat();
      return new Vector3f(f0, f1, f2);
   }
}
