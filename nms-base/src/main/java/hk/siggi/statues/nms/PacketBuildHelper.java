package hk.siggi.statues.nms;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketBuildHelper {
	public final ByteArrayOutputStream out = new ByteArrayOutputStream();
	public final DataOutputStream dataOut = new DataOutputStream(out);
	public PacketBuildHelper() {
	}
	public byte[] toByteArray() {
		return out.toByteArray();
	}

	public void writeUUID(UUID uuid) throws IOException {
		dataOut.writeLong(uuid.getMostSignificantBits());
		dataOut.writeLong(uuid.getLeastSignificantBits());
	}

	public void writeVarInt(int i) throws IOException {
		while((i & -128) != 0) {
			dataOut.writeByte(i & 127 | 128);
			i >>>= 7;
		}

		dataOut.writeByte(i);
	}

	public void writeVarLong(long i) throws IOException {
		while((i & -128L) != 0L) {
			dataOut.writeByte((int)(i & 127L) | 128);
			i >>>= 7;
		}

		dataOut.writeByte((int)i);
	}
}
