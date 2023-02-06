package iskallia.vault.util.data;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.net.IBitSerializer;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class BitSerializers {
   public static IBitSerializer<UUID> UUID = IBitSerializer.of(BitBuffer::writeUUID, BitBuffer::readUUID);
   public static IBitSerializer<String> STRING = IBitSerializer.of(BitBuffer::writeString, BitBuffer::readString);
   public static IBitSerializer<ResourceLocation> IDENTIFIER = IBitSerializer.of(BitBuffer::writeIdentifier, BitBuffer::readIdentifier);
   public static IBitSerializer<Integer> INT = IBitSerializer.of(BitBuffer::writeInt, BitBuffer::readInt);
   public static IBitSerializer<Float> FLOAT = IBitSerializer.of(BitBuffer::writeFloat, BitBuffer::readFloat);
   public static IBitSerializer<Double> DOUBLE = IBitSerializer.of(BitBuffer::writeDouble, BitBuffer::readDouble);
}
