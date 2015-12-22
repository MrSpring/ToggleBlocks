package dk.mrspring.toggle.client.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.StatCollector;

/**
 * Created by Konrad on 03-04-2015.
 */
public class Translator
{
    public static String translate(String toTranslate, Object... format)
    {
        return I18n.format(toTranslate, format);
    }
}
