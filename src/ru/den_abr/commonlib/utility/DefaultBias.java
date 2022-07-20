package ru.den_abr.commonlib.utility;

import ru.den_abr.commonlib.messages.MessageKey;
import ru.den_abr.commonlib.messages.SingleKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultBias
{
    public static String biasPrefix(final String keyPrefix) {
        return keyPrefix + ".timebias";
    }

    public static Collection<MessageKey> withTimeBias(final String keyPrefix, final MessageKey... mainKeys) {
        return Stream.concat(Arrays.stream(mainKeys), createTimeBias(keyPrefix).stream()).collect(Collectors.toList());
    }

    public static Collection<MessageKey> createTimeBias(final String keyPrefix) {
        final Stream.Builder<MessageKey> builder = Stream.builder();
        builder.add(new SingleKey(keyPrefix + ".timebias.days.plural", "дня"));
        builder.add(new SingleKey(keyPrefix + ".timebias.days.plural2", "дней"));
        builder.add(new SingleKey(keyPrefix + ".timebias.days.singular", "день"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.plural", "часа"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.plural2", "часов"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.singular", "час"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.plural", "часа"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.plural2", "часов"));
        builder.add(new SingleKey(keyPrefix + ".timebias.hours.singular", "час"));
        builder.add(new SingleKey(keyPrefix + ".timebias.minutes.plural", "минуты"));
        builder.add(new SingleKey(keyPrefix + ".timebias.minutes.plural2", "минут"));
        builder.add(new SingleKey(keyPrefix + ".timebias.minutes.singular", "минуту"));
        builder.add(new SingleKey(keyPrefix + ".timebias.seconds.plural", "секунды"));
        builder.add(new SingleKey(keyPrefix + ".timebias.seconds.plural2", "секунд"));
        builder.add(new SingleKey(keyPrefix + ".timebias.seconds.singular", "секунду"));
        return builder.build().collect(Collectors.toList());
    }
}
