package me.geek.tom.mcprofileview.profile;

import me.geek.tom.mcprofileview.profile.tree.TreeBranch;
import me.geek.tom.mcprofileview.profile.tree.TreePart;
import me.geek.tom.mcprofileview.profile.tree.TreeRoot;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProfileFile {

    private TreeRoot root = new TreeRoot();
    private int ticks, time;

    private static final Pattern DEPTH = Pattern.compile("\\[([0-9]{2})] ");

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

    public static ProfileFile load(File file) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset()).stream().map(String::trim).collect(Collectors.toList());
        ProfileFile ret = new ProfileFile();

        TreePart currentParent = ret.getRoot();
        TreePart previous = currentParent;
        int currentDepth = -1;
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
                TreeBranch newPart = new TreeBranch(name, "");
                if (depth < currentDepth) {
                    currentParent = currentParent.getParent();
                    currentDepth = depth;
                } else if (depth > currentDepth) {
                    currentParent = previous;
                    currentDepth = depth;
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

    public static class NotAProfileException extends IOException { }
}
