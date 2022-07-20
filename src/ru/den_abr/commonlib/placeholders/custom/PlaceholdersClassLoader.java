package ru.den_abr.commonlib.placeholders.custom;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import ru.den_abr.commonlib.CommonLib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceholdersClassLoader extends URLClassLoader
{
    private File placeholderFile;
    private final CommonLib plugin;
    
    public PlaceholdersClassLoader(final CommonLib plugin, final File file) {
        super(new URL[0], plugin.getClass().getClassLoader());
        this.plugin = plugin;
        try {
            this.placeholderFile = file;
            this.addURL(this.placeholderFile.toURI().toURL());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public LoadablePlaceholder lookupPlaceholder() {
        Class<? extends LoadablePlaceholder> mainClass = null;
        if (this.getPlaceholdersFile().isDirectory()) {
            for (final File file : this.getPlaceholdersFile().listFiles((f, name) -> name.endsWith(".class"))) {
                final Class<?> loadedClass = this.loadClass(file);
                if (this.isMainClass(loadedClass) && this.hasAnnotation(loadedClass)) {
                    if (mainClass != null) {
                        this.plugin.getLogger().warning(this.getPlaceholdersFile() + " contains multiple placeholder class files. Skipping " + file);
                    }
                    else {
                        mainClass = (Class<? extends LoadablePlaceholder>)loadedClass;
                    }
                }
            }
        }
        else {
            if (!this.getPlaceholdersFile().getName().endsWith(".class")) {
                this.plugin.getLogger().warning(this.getPlaceholdersFile() + " is not supported");
                return null;
            }
            final Class<?> loadedClass2 = this.loadClass(this.getPlaceholdersFile());
            if (this.isMainClass(loadedClass2) && this.hasAnnotation(loadedClass2)) {
                mainClass = (Class<? extends LoadablePlaceholder>)loadedClass2;
            }
            else {
                this.plugin.getLogger().warning(this.getPlaceholdersFile() + " is not LoadablePlaceholder file");
            }
        }
        return this.loadPlaceholderFromClass(mainClass);
    }
    
    public LoadablePlaceholder loadPlaceholderFromClass(final Class<? extends LoadablePlaceholder> clazz) {
        final CPlaceholder annotation = this.extractAnnotation(clazz);
        final List<String> missing = Arrays.stream(annotation.depend()).filter(p -> Bukkit.getPluginManager().getPlugin(p) == null).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            CommonLib.INSTANCE.getLogger().warning(clazz.getName() + " missing depending plugins: " + Joiner.on(", ").join(missing));
            return null;
        }
        try {
            final LoadablePlaceholder placeholder = clazz.newInstance();
            placeholder.init(this, annotation.id(), annotation.version(), annotation.description(), annotation.author(), Arrays.asList(annotation.depend()), annotation.requiresPlayer(), annotation.canRunAsync());
            placeholder.setFile(this.getPlaceholdersFile());
            return placeholder;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (this.getPlaceholdersFile().isDirectory()) {
            return super.findClass(name);
        }
        try (final FileInputStream fis = new FileInputStream(this.getPlaceholdersFile())) {
            final byte[] stream = ByteStreams.toByteArray(fis);
            return this.defineClass(name, stream, 0, stream.length);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Class<?> loadClass(final File classFile) {
        try {
            return Class.forName(FilenameUtils.getBaseName(classFile.getName()), true, this);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean isMainClass(final Class<?> clazz) {
        return clazz != null && clazz.getSuperclass().equals(LoadablePlaceholder.class);
    }
    
    public boolean hasAnnotation(final Class<?> clazz) {
        return this.extractAnnotation(clazz) != null;
    }
    
    public CPlaceholder extractAnnotation(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        final CPlaceholder annotation = Arrays.stream(clazz.getAnnotations()).filter(CPlaceholder.class::isInstance).map(CPlaceholder.class::cast).findFirst().orElse(null);
        return annotation;
    }
    
    public File getPlaceholdersFile() {
        return this.placeholderFile;
    }
    
    public CommonLib getPlugin() {
        return this.plugin;
    }
    
    static {
        ClassLoader.registerAsParallelCapable();
    }
}
