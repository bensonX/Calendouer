package cn.sealiu.calendouer.until;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liuyang
 * on 2017/1/25.
 */

public class FestivalCalendar {
    private static Map<String, String> solarFestMap;

    static {
        solarFestMap = new HashMap<>();
        solarFestMap.put("01-01", "元旦");
        solarFestMap.put("02-14", "情人节");
        solarFestMap.put("03-08", "国际劳动妇女节");
        solarFestMap.put("03-12", "中国植树节");
        solarFestMap.put("03-15", "国际消费者权益日");
        solarFestMap.put("04-01", "国际愚人节");
        solarFestMap.put("04-02", "世界自闭症日");
        solarFestMap.put("04-07", "世界卫生日");
        solarFestMap.put("04-22", "世界地球日");
        solarFestMap.put("04-26", "世界知识产权日");
        solarFestMap.put("05-01", "国际劳动节");
        solarFestMap.put("05-03", "世界新闻自由日");
        solarFestMap.put("05-04", "五四运动纪念日|中国青年节");
        solarFestMap.put("05-08", "世界微笑日:-)");
        solarFestMap.put("05-11", "世界肥胖日");
        solarFestMap.put("05-12", "国际护士节");
        solarFestMap.put("05-31", "世界无烟日");
        solarFestMap.put("06-01", "国际儿童节");
        solarFestMap.put("06-05", "世界环境日");
        solarFestMap.put("06-14", "世界献血日");
        solarFestMap.put("06-23", "国际奥林匹克日");
        solarFestMap.put("06-26", "国际宪章日（联合国宪章日）");
        solarFestMap.put("07-01", "香港回归纪念日|中国共产党成立");
        solarFestMap.put("08-01", "中国人民解放军建军节");
        solarFestMap.put("08-12", "国际青年日");
        solarFestMap.put("08-13", "国际左撇子日");
        solarFestMap.put("09-01", "全国中小学开学");
        solarFestMap.put("09-03", "中国抗日战争胜利纪念日");
        solarFestMap.put("09-08", "国际新闻工作者日|世界扫盲日");
        solarFestMap.put("09-10", "中国教师节");
        solarFestMap.put("09-18", "中国国耻日|\"九•一八\"事变纪念日");
        solarFestMap.put("09-21", "国际和平日");
        solarFestMap.put("10-01", "中国国庆节");
        solarFestMap.put("10-10", "辛亥革命纪念日");
        solarFestMap.put("10-16", "世界粮食日");
        solarFestMap.put("10-24", "联合国日");
        solarFestMap.put("10-31", "万圣节前夕");
        solarFestMap.put("11-08", "中国记者节");
        solarFestMap.put("11-25", "国际素食日");
        solarFestMap.put("12-01", "世界艾滋病日");
        solarFestMap.put("12-03", "国际残疾人日");
        solarFestMap.put("12-04", "全国法制宣传日");
        solarFestMap.put("12-09", "\"一二•九\"运动纪念日");
        solarFestMap.put("12-10", "世界人权日");
        solarFestMap.put("12-12", "西安事变纪念日");
        solarFestMap.put("12-13", "南京大屠杀纪念日");
        solarFestMap.put("12-20", "澳门回归纪念日");
        solarFestMap.put("12-24", "平安夜");
        solarFestMap.put("12-25", "圣诞节");
    }

    /**
     * 获取阳历节日名称
     *
     * @param calendar 当前的阳历日期
     * @return 返回当前的阳历日期对应的阳历节日名称，没有则返回null
     */
    public static String getSolarFest(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
        String key = df.format(date);

        return solarFestMap.get(key);
    }

    /**
     * 获取阴历节日名称
     *
     * @param calendar 当前的阳历日期
     * @return 放回当前的阳历日期对应的阴历节日名称，没有则返回null
     */
    public static String getLunarFest(Calendar calendar) {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
        if (lunar[1] == 1 && lunar[2] == 1) {
            return "春节";
        } else if (lunar[1] == 1 && lunar[2] == 15) {
            return "元宵节";
        } else if (lunar[1] == 2 && lunar[2] == 2) {
            return "春龙节(龙抬头)";
        } else if (lunar[1] == 3 && lunar[2] == 3) {
            return "上巳节";
        } else if (lunar[1] == 5 && lunar[2] == 5) {
            return "端午节";
        } else if (lunar[1] == 7 && lunar[2] == 7) {
            return "七夕";
        } else if (lunar[1] == 7 && lunar[2] == 15) {
            return "中元节";
        } else if (lunar[1] == 8 && lunar[2] == 15) {
            return "中秋节";
        } else if (lunar[1] == 9 && lunar[2] == 9) {
            return "重阳节";
        } else if (lunar[1] == 12 && lunar[2] == 8) {
            return "腊八节";
        } else if (isNewYearEve(calendar)) {
            return "除夕夜";
        } else {
            return null;
        }
    }

    /**
     * 判断一个日期是否为除夕
     *
     * @param calendar 当前的日期（Calendar）
     * @return boolean 是/否
     */
    private static boolean isNewYearEve(Calendar calendar) {
        calendar.add(Calendar.DATE, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
        return lunar[1] == 1 && lunar[2] == 1;
    }


    /**
     * 获取按星期安排的节日名称
     *
     * @param calendar 当前的阳历日期
     * @return 返回当前阳历日期对应的星期节日名称，没有则返回null
     */
    public static String getWeekFest(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1) { //星期日
            if (month == 5) { //五月
                return day > 7 && day <= 14 ? "母亲节" : null;
            } else if (month == 6) { //六月
                return day > 7 && day <= 14 ? "父亲节" : null;
            } else {
                return null;
            }
        } else if (dayOfWeek == 5 && month == 11) { //星期四，十一月
            return day > 21 && day <= 28 ? "感恩节" : null;
        } else {
            return null;
        }
    }
}
