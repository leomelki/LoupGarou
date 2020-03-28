package fr.leomelki.loupgarou.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.beans.ConstructorProperties;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilsConfig {


    private static final Logger LOGGER = Logger.getLogger("CustomConfig");

    private final Plugin plugin;
    private final File file;
    private final String templatePath;

    private YamlConfiguration configuration;

    /**
     * Initialize a new custom configuration.
     *
     * @param plugin       - {@link org.bukkit.plugin.java.JavaPlugin}, Instance of the plugin who use the configuration
     * @param file         - Configuration file out the jar
     * @param templatePath - Path in the jar to the template of the configuration. It's was copied when the file not exist.
     */
    @ConstructorProperties({"plugin", "file", "templatePath"})
    public UtilsConfig(Plugin plugin, File file, String templatePath)
    {
        this.plugin = plugin;
        this.file = file;
        this.templatePath = templatePath;
        LOGGER.info("Create new custom configuration: " + templatePath);
    }

    public void init() throws IOException
    {
        LOGGER.info("Initilize configuration ...");
        if (!this.file.getParentFile().exists())
        {
            LOGGER.info("Folder of the config not exist ! Create it ...");
            if (!this.file.getParentFile().mkdirs())
                throw new IOException("Can't create the folder: " + this.file.getParentFile().getAbsolutePath());
            LOGGER.info("Folder created !");
        }
        if (!this.file.exists())
        {
            LOGGER.info("Configuration file not exist ! Created it ...");
            try
            {
                if (!this.file.createNewFile())
                    throw new IOException("Can't create the file: " + this.file.getAbsolutePath());
                LOGGER.info("Configuration File created with success !");
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, "Error while the creation of the configuration file", e);
            }
            LOGGER.info("Copying file from template ..");

            /*try
            {
                InputStream in  = this.plugin.getResource(templatePath);
                OutputStream out = new FileOutputStream(file);

                byte[] buf = new byte[1024 * 4];
                if(in == null) return;
                int    len = in.read(buf);

                while (len != -1)
                {
                    out.write(buf, 0, len);
                    len = in.read(buf);
                }

                out.close();
                in.close();
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, "Error while copying the template !", e);
            }*/
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Saves this {@link YamlConfiguration} to the file. If the file does not exist, it will be created. If already exists, it will be overwritten. If
     * it cannot be overwritten or created, an exception will be thrown.
     * <p>
     * This method will save using the system default encoding, or possibly using UTF8.
     */
    public void save()
    {
        try
        {
            this.configuration.save(this.file);
            LOGGER.info("Save configuration ...");
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Can't save the configuration !", e);
        }
    }

    /**
     * Reload the {@link YamlConfiguration} from file to ram.
     */
    public void reload()
    {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        LOGGER.info("Reload configuration ...");
    }

    /**
     * Get the configuration file
     *
     * @return file object of the configuration
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Get the path to the template of the configuration
     *
     * @return path of the template
     */
    public String getTemplatePath()
    {
        return templatePath;
    }

    /**
     * Get Config instance.
     *
     * @return {@link YamlConfiguration} instance for the file.
     */
    public YamlConfiguration getConfig()
    {
        return configuration;
    }

}
