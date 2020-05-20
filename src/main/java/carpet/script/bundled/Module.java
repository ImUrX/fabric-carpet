package carpet.script.bundled;

import carpet.CarpetServer;
import net.minecraft.nbt.Tag;

import java.io.File;
import java.nio.file.Files;

import static carpet.script.bundled.FileModule.read;
import static carpet.script.bundled.FileModule.write;

public abstract class Module
{
    public abstract String getName();
    public abstract String getCode();
    public abstract boolean isLibrary();

    private static String getDescriptor(Module module, String file, boolean isShared)
    {
        if (isShared)
        {
            return "shared/"+file;
        }
        else if (module != null && module.getName() != null) // appdata
        {
            return module.getName()+".data"+(file==null?"":"/"+file);
        }
        else
        {
            throw  new RuntimeException("Invalid file descriptor: "+file);
        }
    }

    public static Tag getData(Module module, String file, boolean isShared)
    {
        if (!isShared && (module == null || module.getName() == null)) return null;
        File dataFile = CarpetServer.minecraft_server.getLevelStorage().resolveFile(
                CarpetServer.minecraft_server.getLevelName(), "scripts/"+getDescriptor(module, file, isShared)+".nbt");
        if (!Files.exists(dataFile.toPath()) || !(dataFile.isFile())) return null;
        return read(dataFile);
    }

    public static boolean saveData(Module module, String file, Tag globalState, boolean isShared)
    {
        if (!isShared && (module == null || module.getName() == null)) return false;
        File dataFile =CarpetServer.minecraft_server.getLevelStorage().resolveFile(
                CarpetServer.minecraft_server.getLevelName(), "scripts/"+getDescriptor(module, file, isShared)+".nbt");
        if (!Files.exists(dataFile.toPath().getParent()) && !dataFile.getParentFile().mkdirs()) return false;
        return write(globalState, dataFile);
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }
}
