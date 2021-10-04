package iskallia.vault.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class JsonDump {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public abstract String fileName();

   public abstract JsonObject dumpToJSON();

   public void dumpToFile(String parentDir) throws IOException {
      File configFile = new File(parentDir + File.separator + this.fileName());
      if (!configFile.exists()) {
         configFile.getParentFile().mkdirs();
         configFile.createNewFile();
      }

      JsonObject jsonObject = this.dumpToJSON();
      FileWriter writer = new FileWriter(configFile);
      GSON.toJson(jsonObject, writer);
      writer.close();
   }
}
