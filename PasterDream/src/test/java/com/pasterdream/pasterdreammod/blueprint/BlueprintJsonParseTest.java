package com.pasterdream.pasterdreammod.blueprint;

import com.pasterdream.pasterdreammod.data.BlueprintJsonParser;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 蓝图 JSON 解析可自动断言测试（无 Minecraft 运行时，仅依赖 Gson + 主源码纯解析器）。
 * <p>
 * 运行：
 * <pre>
 *   JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline --console=plain
 *   # 然后用 compileJava classpath + 本测试类 main，或直接：
 *   java -cp ... com.pasterdream.pasterdreammod.blueprint.BlueprintJsonParseTest
 * </pre>
 * 也可在 IDE 中运行 {@link #main(String[])}。
 */
public final class BlueprintJsonParseTest {

    private static final Path BLUEPRINTS = Path.of("PasterDream/src/main/resources/data/pasterdream/blueprints");
    private static final Path BLUEPRINTS_ALT = Path.of("src/main/resources/data/pasterdream/blueprints");

    private int passed;
    private int failed;
    private final List<String> messages = new ArrayList<>();

    public static void main(String[] args) {
        BlueprintJsonParseTest test = new BlueprintJsonParseTest();
        test.runAll();
        System.out.println("BlueprintJsonParseTest: passed=" + test.passed + " failed=" + test.failed);
        for (String m : test.messages) {
            System.out.println("  - " + m);
        }
        if (test.failed > 0) {
            System.exit(1);
        }
    }

    void runAll() {
        check("toBlueprintPath strips folder/ext", () -> {
            String p = BlueprintJsonParser.blueprintPathFromResourcePath("blueprints/weapon_workshop.json");
            assertEq("weapon_workshop", p);
            String nested = BlueprintJsonParser.blueprintPathFromResourcePath("blueprints/a/b.json");
            assertEq("a/b", nested);
        });

        check("shadow_blast_furnace 3 layers + core", () -> {
            List<Map<Integer, String>> pages = parseFile("shadow_blast_furnace.json");
            assertEq(3, pages.size());
            assertEq("pasterdream:shadow_blast_furnace_core", pages.get(1).get(12));
            assertSlotsInRange(pages);
        });

        check("weapon_workshop 4 layers + weapon_table", () -> {
            List<Map<Integer, String>> pages = parseFile("weapon_workshop.json");
            assertEq(4, pages.size());
            assertEq("pasterdream:weapon_table", pages.get(1).get(8));
            assertSlotsInRange(pages);
        });
    }

    private void check(String name, ThrowingRunnable body) {
        try {
            body.run();
            passed++;
            messages.add("OK  " + name);
        } catch (Throwable t) {
            failed++;
            messages.add("FAIL " + name + ": " + t.getMessage());
        }
    }

    private static List<Map<Integer, String>> parseFile(String name) throws Exception {
        Path file = resolveBlueprintDir().resolve(name);
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("missing file: " + file.toAbsolutePath());
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return BlueprintJsonParser.parseRawPages(reader);
        }
    }

    private static Path resolveBlueprintDir() {
        if (Files.isDirectory(BLUEPRINTS)) {
            return BLUEPRINTS;
        }
        if (Files.isDirectory(BLUEPRINTS_ALT)) {
            return BLUEPRINTS_ALT;
        }
        // worktree 根相对
        Path wt = Path.of("src/main/resources/data/pasterdream/blueprints");
        if (Files.isDirectory(wt)) {
            return wt;
        }
        throw new IllegalStateException("cannot locate blueprints dir from " + Path.of(".").toAbsolutePath());
    }

    private static void assertSlotsInRange(List<Map<Integer, String>> pages) {
        for (int p = 0; p < pages.size(); p++) {
            for (Integer slot : pages.get(p).keySet()) {
                if (slot < 0 || slot >= BlueprintJsonParser.PAGE_SIZE) {
                    throw new AssertionError("page " + p + " out-of-range slot " + slot);
                }
            }
        }
    }

    private static void assertEq(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("expected=" + expected + " actual=" + actual);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
