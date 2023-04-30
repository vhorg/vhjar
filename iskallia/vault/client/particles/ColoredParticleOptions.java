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

public record ColoredParticleOptions(ParticleType<ColoredParticleOptions> particleType, Vector3f color) implements ParticleOptions {
   public static final Deserializer<ColoredParticleOptions> DESERIALIZER = new Deserializer<ColoredParticleOptions>() {
      @Nonnull
      @ParametersAreNonnullByDefault
      public ColoredParticleOptions fromCommand(ParticleType<ColoredParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
         return new ColoredParticleOptions(particleType, ColoredParticleOptions.readVector3f(reader));
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ColoredParticleOptions fromNetwork(ParticleType<ColoredParticleOptions> particleType, FriendlyByteBuf buffer) {
         return new ColoredParticleOptions(particleType, new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
      }
   };

   public static Codec<ColoredParticleOptions> codec(ParticleType<ColoredParticleOptions> particleType) {
      return RecordCodecBuilder.create(
         c -> c.group(Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color)).apply(c, color -> new ColoredParticleOptions(particleType, color))
      );
   }

   @Nonnull
   public ParticleType<ColoredParticleOptions> getType() {
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
