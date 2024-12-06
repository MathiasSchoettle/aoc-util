package org.mschoe.aocutil.lib.container;

import org.mschoe.aocutil.Solution;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ClassContainer {

    private final Map<CalendarEntry, Class<?>> classes = new HashMap<>();

    public ClassContainer() {
        fillClasses();
    }

    private void fillClasses() {
        List<Class<?>> classes = new ArrayList<>();

        try {
            Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("");
            Iterator<URL> iterator = resources.asIterator();
            while (iterator.hasNext()) {
                URL url = iterator.next();
                File file = new File(url.getFile());
                classes.addAll(scanDirectory(file, ""));
            }
        } catch (Exception ignored) {
            System.err.println("Error while loading classes");
        }

        classes.forEach(c -> {
            Solution solution = c.getAnnotation(Solution.class);
            this.classes.put(new CalendarEntry(solution.day(), solution.year()), c);
        });
    }

    private List<Class<?>> scanDirectory(File dir, String packageName) throws ClassNotFoundException {
        File[] files = dir.listFiles();

        if (files == null) {
            return new ArrayList<>();
        }

        List<Class<?>> result = new ArrayList<>();

        for (var file : files) {
            if (file.isDirectory()) {
                var classes = scanDirectory(file, packageName + file.getName() + ".");
                result.addAll(classes);
            } else {
                String className = packageName + file.getName().replace(".class", "");
                var clazz = Class.forName(className);

                if (clazz.getAnnotation(Solution.class) != null) {
                    result.add(Class.forName(className));
                }
            }
        }

        return result;
    }

    public Optional<Class<?>> getClassForEntry(int day, int year) {
        Class<?> clazz = classes.get(new CalendarEntry(day, year));
        return Optional.ofNullable(clazz);
    }
}
