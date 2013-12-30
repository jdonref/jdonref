/*
 * Version 2.4.0 – 2012
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
 *
 * Ce logiciel est un service web servant à valider et géocoder des adresses postales.
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant
 * les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier
 * et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que
 * diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
 * En contrepartie de l'accessibilité au code source et des droits de copie, de
 * modification et de redistribution accordés par cette licence, il n'est offert aux
 * utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits
 * patrimoniaux et les concédants successifs.
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés au
 * chargement,  à l'utilisation,  à la modification et/ou au développement et à la
 * reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel
 * libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des
 * développeurs et des professionnels avertis possédant  des  connaissances
 * informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
 * l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
 * et l'exploiter dans les mêmes conditions de sécurité.
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes.
 */

package ppol.jdonref.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Outils de manipulation de dates
 * @author arochewi
 */
public class DateUtils
{
    // DateFormat de type 'dd/MM/yyyy'
    private final static DateFormat simpleSlachedDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    // DateFormat de type 'dd-MM-yyyy'
    private final static DateFormat simpleDashedDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
    // new SimpleDateFormat("HH:mm",Locale.FRANCE);
    private final static DateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm",Locale.FRANCE);
    // DateFormat de type dateTime MEDIUM / SHORT
    private final static DateFormat dateTimeMediumShortDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
            Locale.FRANCE);
    // DateFormat de type date MEDIUM
    private final static DateFormat dateMediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE);

    public enum DateFormatType
    {
        SimpleSlashed, SimpleDashed, SimpleTime, DateTimeMediumShort, DateMedium
    };

    /**
     * Version synchronisee de new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE).format(date)
     * @param date
     * @return
     */
    public static String formatDateToStringSimpleSlashed(Date date)
    {
        String result;
        synchronized(simpleSlachedDateFormat)
        {
            result = simpleSlachedDateFormat.format(date);
        }
        return result;
    }
    /**
     * Version synchronisee de new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE).format(thing)
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @return
     */
    public static String formatDateToStringSimpleSlashed(Object thing)
    {
        String result;
        synchronized(simpleSlachedDateFormat)
        {
            result = simpleSlachedDateFormat.format(thing);
        }
        return result;
    }

    /**
     * Version synchronisee de new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE).parse(str)
     * @param date
     * @return
     */
    public static Date parseStringToDateSimpleSlashed(String str) throws ParseException
    {
        Date result;
        synchronized(simpleSlachedDateFormat)
        {
            result = simpleSlachedDateFormat.parse(str);
        }
        return result;
    }

    /**
     * Version synchronisee de new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE).format(date)
     * @param date
     * @return
     */
    public static String formatDateToStringSimpleDashed(Date date)
    {
        String result;
        synchronized(simpleDashedDateFormat)
        {
            result = simpleDashedDateFormat.format(date);
        }
        return result;
    }
    /**
     * Version synchronisee de new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE).format(date)
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @return
     */
    public static String formatDateToStringSimpleDashed(Object thing)
    {
        String result;
        synchronized(simpleDashedDateFormat)
        {
            result = simpleDashedDateFormat.format(thing);
        }
        return result;
    }

    /**
     * Version synchronisee de new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE).parse(str)
     * @param date
     * @return
     */
    public static Date parseStringToDateSimpleDashed(String str) throws ParseException
    {
        Date result;
        synchronized(simpleDashedDateFormat)
        {
            result = simpleDashedDateFormat.parse(str);
        }
        return result;
    }

    /**
     * Version synchronisee de new SimpleDateFormat("HH:mm",Locale.FRANCE).format(date)
     * @param date
     * @return
     */
    public static String formatTimeToStringSimple(Date date)
    {
        String result;
        synchronized(simpleTimeFormat)
        {
            result = simpleTimeFormat.format(date);
        }
        return result;
    }
    /**
     * Version synchronisee de new SimpleDateFormat("HH:mm",Locale.FRANCE).format(date)
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @return
     */
    public static String formatTimeToStringSimple(Object thing)
    {
        String result;
        synchronized(simpleTimeFormat)
        {
            result = simpleTimeFormat.format(thing);
        }
        return result;
    }

    /**
     * Version synchronisee de new SimpleDateFormat("HH:mm",Locale.FRANCE).parse(str)
     * @param date
     * @return
     */
    public static Date parseStringToTimeSimple(String str) throws ParseException
    {
        Date result;
        synchronized(simpleTimeFormat)
        {
            result = simpleTimeFormat.parse(str);
        }
        return result;
    }

    /**
     * Version synchronisee de DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE).format(date);
     * @param date
     * @return
     */
    public static String formatDateToStringDateTimeMediumShort(Date date)
    {
        String result;
        synchronized(dateTimeMediumShortDateFormat)
        {
            result = dateTimeMediumShortDateFormat.format(date);
        }
        return result;
    }
    /**
     * Version synchronisee de DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE).format(date);
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @return
     */
    public static String formatDateToStringDateTimeMediumShort(Object thing)
    {
        String result;
        synchronized(dateTimeMediumShortDateFormat)
        {
            result = dateTimeMediumShortDateFormat.format(thing);
        }
        return result;
    }

    /**
     * Version synchronisee de DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE).format(date);
     * @param date
     * @return
     */
    public static Date parseStringToDateDateTimeMediumShort(String str) throws ParseException
    {
        Date result;
        synchronized(dateTimeMediumShortDateFormat)
        {
            result = dateTimeMediumShortDateFormat.parse(str);
        }
        return result;
    }

    /**
     * Version synchronisee de DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date);
     * @param date
     * @return
     */
    public static String formatDateToStringDateMedium(Date date)
    {
        String result;
        synchronized(dateMediumDateFormat)
        {
            result = dateMediumDateFormat.format(date);
        }
        return result;
    }
    /**
     * Version synchronisee de DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date);
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @return
     */
    public static String formatDateToStringDateMedium(Object thing)
    {
        String result;
        synchronized(dateMediumDateFormat)
        {
            result = dateMediumDateFormat.format(thing);
        }
        return result;
    }

    /**
     * Version synchronisee de DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date);
     * @param date
     * @return
     */
    public static Date parseStringToDateDateMedium(String str) throws ParseException
    {
        Date result;
        synchronized(dateMediumDateFormat)
        {
            result = dateMediumDateFormat.parse(str);
        }
        return result;
    }

    /**
     * Formate de facon thread safe une date selon un des 4 formats proposes
     * @param date
     * @param format :  SimpleSlashed : dd/MM/yyyy, SimpleDashed : dd-MM-yyyy,
     *                  DateTimeMediumShort : DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE),
     *                  DateMedium : DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date).
     * @return
     */
    public static String formatDateToString(Date date, DateFormatType format)
    {
        switch (format)
        {
            case SimpleSlashed:
            {
                return formatDateToStringSimpleSlashed(date);
            }
            case SimpleDashed:
            {
                return formatDateToStringSimpleDashed(date);
            }
            case SimpleTime:
            {
                return formatTimeToStringSimple(date);
            }
            case DateTimeMediumShort:
            {
                return formatDateToStringDateTimeMediumShort(date);
            }
            case DateMedium:
            {
                return formatDateToStringDateMedium(date);
            }
        }
        return "";
    }

    /**
     * Formate de facon thread safe une date selon un des 4 formats proposes
     * @param thing : un objet quelconque pour garder la semantique de DateFormat
     * @param format :  SimpleSlashed : dd/MM/yyyy, SimpleDashed : dd-MM-yyyy,
     *                  DateTimeMediumShort : DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE),
     *                  DateMedium : DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date).
     * @return
     */
    public static String formatDateToString(Object thing, DateFormatType format)
    {
        switch (format)
        {
            case SimpleSlashed:
            {
                return formatDateToStringSimpleSlashed(thing);
            }
            case SimpleDashed:
            {
                return formatDateToStringSimpleDashed(thing);
            }
            case SimpleTime:
            {
                return formatTimeToStringSimple(thing);
            }
            case DateTimeMediumShort:
            {
                return formatDateToStringDateTimeMediumShort(thing);
            }
            case DateMedium:
            {
                return formatDateToStringDateMedium(thing);
            }
        }
        return "";
    }

    /**
     * Parse de facon thread safe une string selon un des 4 formats proposes
     * @param date
     * @param format :  SimpleSlashed : dd/MM/yyyy, SimpleDashed : dd-MM-yyyy,
     *                  DateTimeMediumShort : DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.FRANCE),
     *                  DateMedium : DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRANCE).format(date).
     * @return
     */
    public static Date parseStringToDate(String str, DateFormatType format) throws ParseException
    {
        switch (format)
        {
            case SimpleSlashed:
            {
                return parseStringToDateSimpleSlashed(str);
            }
            case SimpleDashed:
            {
                return parseStringToDateSimpleDashed(str);
            }
            case SimpleTime:
            {
                return parseStringToTimeSimple(str);
            }
            case DateTimeMediumShort:
            {
                return parseStringToDateDateTimeMediumShort(str);
            }
            case DateMedium:
            {
                return parseStringToDateDateMedium(str);
            }
        }
        return null;
    }
}
