package dk.mrspring.toggle.util;

import net.minecraft.util.StatCollector;

/**
 * Created by Konrad on 03-04-2015.
 */
public class Translator
{
    public static String translate(String toTranslate)
    {
        return StatCollector.translateToLocal(toTranslate);
    }

    public static String translate(String toTranslate, Object... format)
    {
        return StatCollector.translateToLocalFormatted(toTranslate, format).replace("§", "\u00a7");
    }
}
