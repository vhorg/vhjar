package iskallia.vault.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

public class VaultDataDump {
   private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

   public static void onStart(FMLServerStartedEvent event) {
      dumpModData();
   }

   public static void dumpModData() {
      File dir = new File("data/the_vault/");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File dataFile = new File(dir, "data.json");
      if (dataFile.exists()) {
         dataFile.delete();
      }

      try {
         dataFile.createNewFile();
         FileWriter writer = new FileWriter(dataFile);
         GSON.toJson(getData(), writer);
         writer.flush();
         writer.close();
      } catch (IOException var3) {
         var3.printStackTrace();
      }
   }

   public static Map<String, String> getData() {
      Map<String, String> data = new HashMap<>();
      String version = ModList.get()
         .getModContainerById("the_vault")
         .map(ModContainer::getModInfo)
         .<ArtifactVersion>map(IModInfo::getVersion)
         .map(Objects::toString)
         .orElse("");
      data.put("version", version);
      return data;
   }
}
