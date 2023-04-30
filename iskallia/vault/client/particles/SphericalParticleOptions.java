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

public record SphericalParticleOptions(ParticleType<SphericalParticleOptions> particleType, float range, Vector3f color) implements ParticleOptions {
   public static final Deserializer<SphericalParticleOptions> DESERIALIZER = new Deserializer<SphericalParticleOptions>() {
      @Nonnull
      @ParametersAreNonnullByDefault
      public SphericalParticleOptions fromCommand(ParticleType<SphericalParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
         return new SphericalParticleOptions(particleType, reader.readFloat(), SphericalParticleOptions.readVector3f(reader));
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public SphericalParticleOptions fromNetwork(ParticleType<SphericalParticleOptions> particleType, FriendlyByteBuf buffer) {
         return new SphericalParticleOptions(particleType, buffer.readFloat(), new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
      }
   };

   public static Codec<SphericalParticleOptions> codec(ParticleType<SphericalParticleOptions> particleType) {
      return RecordCodecBuilder.create(
         c -> c.group(Codec.FLOAT.fieldOf("range").forGetter(data -> data.range), Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color))
            .apply(c, (range, color) -> new SphericalParticleOptions(particleType, range, color))
      );
   }

   @Nonnull
   public ParticleType<SphericalParticleOptions> getType() {
      return this.particleType;
   }

   public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
      buffer.writeFloat(this.range);
      buffer.writeFloat(this.color.x());
      buffer.writeFloat(this.color.y());
      buffer.writeFloat(this.color.z());
   }

   @Nonnull
   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", this.getType().getRegistryName(), this.range, this.color.x(), this.color.y(), this.color.z());
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
