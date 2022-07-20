package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Animation
{
    private static String glidingPattern;
    private static String slidesPattern;
    private final List<String> frames;
    private final Iterator<String> iterator;
    private String current;
    
    public Animation(final List<String> frames) {
        this.frames = (List<String>)ImmutableList.copyOf((Collection)frames);
        this.iterator = (Iterator<String>)Iterators.cycle((Iterable)this.frames);
    }
    
    public static Optional<Animation> parseDynamicAnimation(final String input) {
        if (isGliding(input)) {
            return Optional.of(parseGliding(input));
        }
        if (isSlides(input)) {
            return Optional.of(parseSlides(input));
        }
        return Optional.empty();
    }
    
    public static Animation parseSlides(final String input) {
        Preconditions.checkArgument(isSlides(input), "Provided string is not an animation: " + input);
        final List<String> slides = ((JsonObject)GsonUtil.GSON.fromJson(input, JsonObject.class)).getAsJsonObject("slides").entrySet().stream().flatMap(e -> Stream.generate(() -> e.getKey()).limit(e.getValue().getAsInt())).collect(Collectors.toList());
        return new Animation(slides);
    }
    
    public static Animation parseGliding(final String input) {
        Preconditions.checkArgument(isGliding(input), "Provided string is not an animation: " + input);
        final String leftFiller = input.replaceAll(Animation.glidingPattern, "$1");
        final int leftSize = Integer.parseInt(input.replaceAll(Animation.glidingPattern, "$2"));
        final String middleFiller = input.replaceAll(Animation.glidingPattern, "$3");
        final int middleSize = Integer.parseInt(input.replaceAll(Animation.glidingPattern, "$4"));
        final String rightFiller = input.replaceAll(Animation.glidingPattern, "$5");
        final int rightSize = Integer.parseInt(input.replaceAll(Animation.glidingPattern, "$6"));
        final int repAmount = Integer.parseInt(input.replaceAll(Animation.glidingPattern, "$7"));
        final String text = UtilityMethods.color(input.replaceAll(Animation.glidingPattern, "$8"));
        final String generalColor = ChatColor.getLastColors(text);
        final String colorless = UtilityMethods.stripColor(text);
        final List<String> result = new ArrayList<>(colorless.length());
        final int insertionsLength = leftSize + middleSize + rightSize;
        for (int index = -insertionsLength; index < text.length(); ++index) {
            final StringBuilder sb = new StringBuilder(colorless);
            final int leftOffset = index;
            final int midOffset = leftOffset + leftSize;
            final int rightOffset = midOffset + middleSize;
            final int endOffset = rightOffset + rightSize;
            final List<Map.Entry<Integer, String>> list = new ArrayList<Map.Entry<Integer, String>>();
            if (leftOffset > 0) {
                list.add(new AbstractMap.SimpleEntry<Integer, String>(0, generalColor));
            }
            list.add(new AbstractMap.SimpleEntry<Integer, String>(leftOffset, leftFiller));
            list.add(new AbstractMap.SimpleEntry<>(midOffset, middleFiller));
            list.add(new AbstractMap.SimpleEntry<Integer, String>(rightOffset, rightFiller));
            list.add(new AbstractMap.SimpleEntry<Integer, String>(endOffset, generalColor));
            boolean beginning = false;
            for (int i = list.size(); i > 0; --i) {
                final Map.Entry<Integer, String> entry = list.get(i - 1);
                int offset = entry.getKey();
                final String insertion = entry.getValue();
                if (!beginning && offset < 0 && i <= list.size() - 1) {
                    offset = 0;
                    beginning = true;
                }
                if (offset >= 0 && sb.length() >= offset) {
                    sb.insert(offset, insertion);
                }
            }
            result.add(sb.toString());
        }
        Stream.generate(() -> text).limit(repAmount).forEach(result::add);
        return new Animation(result);
    }
    
    public static boolean isGliding(final String string) {
        return string.matches(Animation.glidingPattern);
    }
    
    public static boolean isSlides(final String input) {
        return input.matches(Animation.slidesPattern);
    }
    
    public List<String> getFrames() {
        return this.frames;
    }
    
    public String next() {
        return this.current = this.iterator.next();
    }
    
    public String current() {
        return (this.current == null) ? this.next() : this.current;
    }
    
    static {
        Animation.glidingPattern = "\\{gliding:\"(.+)\":([0-9]+);\"(.*)\":([0-9]+);\"(.*)\":([0-9]+);([0-9]+);\"(.+)\"}";
        Animation.slidesPattern = "\\{slides:\\{(.+)\\}}";
    }
}
