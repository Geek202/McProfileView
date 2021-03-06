package me.geek.tom.mcprofileview.profile;

import me.geek.tom.mcprofileview.profile.tree.TreeBranch;
import me.geek.tom.mcprofileview.profile.tree.TreePart;
import me.geek.tom.mcprofileview.profile.tree.TreeRoot;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProfileFile {

    private final TreeRoot root = new TreeRoot();
    private int ticks, time;

    private static final Pattern DEPTH = Pattern.compile("\\[([0-9]{2})] ");
    //private static final Pattern PERCENTS = Pattern.compile("(.*) - ([0-9]*.[0-9]*)%/([0-9]*.[0-9]*)%");

    private ProfileFile() { }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TreeRoot getRoot() {
        return root;
    }

    @Deprecated // Use the new load function below
    public static ProfileFile load(File file) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset()).stream().map(String::trim).collect(Collectors.toList());
        ProfileFile ret = new ProfileFile();

        TreePart currentParent = ret.getRoot();
        TreePart previous = currentParent;
        int currentDepth = 0;
        boolean isReadingProfileDump = false;
        int tickCount = 0;
        int time = 0;

        if (lines.size() == 0 || !lines.get(0).equals("---- Minecraft Profiler Results ----"))
            throw new NotAProfileException();

        for (String line : lines) {
            Matcher m = DEPTH.matcher(line);
            if (line.startsWith("//")) // Comment
                continue;

            if (line.startsWith("Time span: ")) { // Time
                time = Integer.parseInt(line.substring(11).replace(" ms", ""));
            } else if (line.startsWith("Tick span: ")) { // Ticks
                tickCount = Integer.parseInt(line.substring(11).replace(" ticks", ""));
            } else if (line.startsWith("--- BEGIN PROFILE DUMP ---")) { // Profile dump start
                isReadingProfileDump = true;
            } else if (line.startsWith("--- END PROFILE DUMP ---")) { // Profile dump end
                break;
            } else if (m.find() && isReadingProfileDump) {
                int depth = Integer.parseInt(m.group(1));
                String name = line.substring((depth * 4) + 5);
                TreeBranch newPart = new TreeBranch(name, "", 0, 0);
                if (depth < currentDepth) {
                    System.out.printf("Step out from %d to %d\n", currentDepth, depth);
                    currentParent = currentParent.getParent();
                    currentDepth = depth;
                } else if (depth > currentDepth) {
                    System.out.printf("Step in from %d to %d\n", currentDepth, depth);
                    currentParent = previous;
                    currentDepth = depth;
                } else {
                    System.out.printf("Staying at depth: %d\n", depth);
                }
                if (name.contains("unspecified(4255/37) - 31.25%/0.58%"))
                    System.out.println("josdfgv");
                previous = newPart;
                newPart.setParent(currentParent);
                assert currentParent != null; // This should never fail in a valid file!
                currentParent.addBranch(newPart);
            }
        }

        ret.setTicks(tickCount);
        ret.setTime(time);

        return ret;
    }

    public static ProfileFile loadNew(File file) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset()).stream().map(String::trim).collect(Collectors.toList());
        ProfileFile ret = new ProfileFile();

        if (lines.size() == 0 || !lines.get(0).equals("---- Minecraft Profiler Results ----"))
            throw new NotAProfileException();

        int time = 0, tickCount = 0;

        while (lines.size() != 0) {
            String line = lines.remove(0);

            if (line.startsWith("//")) // Comment
                continue;

            if (line.startsWith("Time span: ")) { // Time
                time = Integer.parseInt(line.substring(11).replace(" ms", ""));
            } else if (line.startsWith("Tick span: ")) { // Ticks
                tickCount = Integer.parseInt(line.substring(11).replace(" ticks", ""));
            } else if (line.startsWith("--- BEGIN PROFILE DUMP ---")) { // Start reading the tree
                lines.remove(0); // Get rid of the blank line
                String nextLine = lines.get(0);
                while (!nextLine.equals("--- END PROFILE DUMP ---")) { // Read until end
                    TreePart part = findChildren(lines, ret.getRoot());
                    if (part != null)
                        ret.getRoot().addBranch(part);
                    else
                        break;
                    nextLine = lines.get(0);
                }
                break;
            }
        }

        ret.setTicks(tickCount);
        ret.setTime(time);

        return ret;
    }

    private static TreePart findChildren(List<String> lines, TreePart parent) {
        String startLine = lines.remove(0);

        if (startLine.equals("--- END PROFILE DUMP ---"))
            return null;

        int depth = getDepth(startLine);

        String name = startLine.substring((depth * 4) + 5);

        float percentOfParent, percentOfTotal;
        if (name.contains("%")) {
            String percents = name.split(" - ")[1];
            String[] parts = percents.split("/");

            String parentVal = parts[0];
            String totalVal = parts[1];

            percentOfParent = Float.parseFloat(parentVal.replace("%", ""));
            percentOfTotal = Float.parseFloat(totalVal.replace("%", ""));
        } else {
            percentOfParent = percentOfTotal = 0.0f;
        }

        TreeBranch ret = new TreeBranch(name, "", percentOfParent, percentOfTotal);

        ret.setParent(parent);

        String nextLine = lines.get(0);
        if (nextLine.equals("--- END PROFILE DUMP ---"))
            return null;

        int nextDepth = getDepth(nextLine);
        List<TreePart> children = new ArrayList<>();
        while (nextDepth > depth && lines.size() != 0) {
            if (nextLine.equals("")) continue; // Skip blank line
            TreePart child = findChildren(lines, ret);
            if (child != null) {
                children.add(child);
                nextLine = lines.get(0);
                nextDepth = getDepth(nextLine);
            }
            else break;
        }
        children.forEach(ret::addBranch);
        return ret;
    }

    private static int getDepth(String line) {
        Matcher m = DEPTH.matcher(line);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new RuntimeException(new IllegalArgumentException("No depth found in line: " + line));
    }

    public float getAverageTps() {
        float tps = this.ticks / ((float) this.time / 1000);
        DecimalFormat format = new DecimalFormat("##.##");
        return Float.parseFloat(format.format(tps));
    }

    public static class NotAProfileException extends IOException { }
}
