package com.mamba.thrift.iface.desc.parser;

import com.mamba.thrift.iface.desc.parser.model.TInterfacesDesc;
import com.mamba.thrift.iface.desc.parser.model.TServiceDesc;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TIDP {

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Input arguments is empty!");
        }
        if (args.length > 1) {
            throw new IllegalArgumentException("Input arguments is error: " + Arrays.toString(args));
        }
        TInterfacesDesc desc = parse(new File(args[0]));
        System.out.println(desc.toString());
    }

    public static TInterfacesDesc parse(File file) throws Exception {
        if (file == null) {
            throw new NullPointerException("input file is null!");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Invalid file path: " + file.getPath());
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader())) {
            if (file.isDirectory()) {
                return parseClassFiles(classLoader, file);
            } else if (file.getName().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    return parseJarFile(classLoader, jarFile);
                }
            } else {
                throw new RuntimeException("Invalid file: " + file.getPath());
            }
        }
    }

    /**
     * 解析Jar文件
     *
     * @param classLoader
     * @param jarFile
     * @return
     * @throws Exception
     */
    public static TInterfacesDesc parseJarFile(URLClassLoader classLoader, JarFile jarFile) throws Exception {
        if (classLoader == null) {
            throw new NullPointerException("ClassLoader is null!");
        }
        if (jarFile == null) {
            throw new NullPointerException("JarFile is null!");
        }
        List<TServiceDesc> services = new ArrayList<>();
        TMetaDataParser parser = new TMetaDataParser(classLoader);
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            if (jarEntry.isDirectory()) {
                continue;
            }
            String name = jarEntry.getName();
            if (!name.endsWith("$Client.class")) {
                continue;
            }
            String className = name.substring(0, name.length() - 6).replace(File.separatorChar, '.');
            services.add(parser.parseServiceMetaData(className));
        }
        return new TInterfacesDesc(services, parser.getStructs(), parser.getEnums());
    }

    /**
     * 解析class文件（xx/*.class）
     *
     * @param classLoader
     * @param classpath
     * @return
     * @throws Exception
     */
    public static TInterfacesDesc parseClassFiles(URLClassLoader classLoader, File classpath) throws Exception {
        if (classLoader == null) {
            throw new NullPointerException("ClassLoader is null!");
        }
        if (classpath == null) {
            throw new NullPointerException("classpath is null!");
        }
        if (!classpath.isDirectory()) {
            throw new FileNotFoundException("Invalid dict path: " + classpath.getPath());
        }
        List<TServiceDesc> services = new ArrayList<>();
        TMetaDataParser parser = new TMetaDataParser(classLoader);
        parseClassFiles(services, parser, classpath.getAbsolutePath(), classpath.listFiles());
        return new TInterfacesDesc(services, parser.getStructs(), parser.getEnums());
    }

    /**
     * 解析class文件（xx/*.class）
     *
     * @param descs
     * @param parser
     * @param root
     * @param files
     * @throws Exception
     */
    private static void parseClassFiles(List<TServiceDesc> descs, TMetaDataParser parser, String root, File[] files) throws Exception {
        for (File file : files) {
            if (file.isDirectory()) {
                parseClassFiles(descs, parser, root, file.listFiles());
            } else {
                String path = file.getAbsolutePath();
                if (!path.endsWith("$Client.class")) {
                    continue;
                }
                String className = path.substring(root.length() + 1, path.length() - 6).replace('/', '.');
                descs.add(parser.parseServiceMetaData(className));
            }
        }
    }

}
