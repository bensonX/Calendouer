package cn.sealiu.calendouer.until;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by liuyang
 * on 2017/1/25.
 */

public class SolarTermCalendar {
    private final static String[] solarTerm = {
            "小\n寒", "大\n寒", "立\n春", "雨\n水", "惊\n蛰", "春\n分",
            "清\n明", "谷\n雨", "立\n夏", "小\n满", "芒\n种", "夏\n至",
            "小\n暑", "大\n暑", "立\n秋", "处\n暑", "白\n露", "秋\n分",
            "寒\n露", "霜\n降", "立\n冬", "小\n雪", "大\n雪", "冬\n至"
    };

    private final static int[] solarTermInfo = {
            0, 21208, 42467, 63836, 85337, 107014, 128867, 150921,
            173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033,
            353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758
    };

    /**
     * 返回公历年节气的日期
     *
     * @param solarYear 指定公历年份(数字)
     * @param index     指定节气序号(数字,0从小寒算起)
     * @return 日期(数字, 所在月份的第几天)
     */
    private static Date getSolarTermDate(int solarYear, int index) {
        long l = (long) 31556925974.7 * (solarYear - 1900)
                + solarTermInfo[index] * 60000L;

        Calendar cal = Calendar.getInstance();
        cal.set(1900, 0, 6, 2, 5, 0);
        l = l + cal.getTime().getTime();
        return new Date(l);
    }

    private static List<Date> getSolarTermDateList(int solarYear) {
        List<Date> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list.add(getSolarTermDate(solarYear, i));
        }
        return list;
    }

    public static String getSolarTermStr(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        List<Date> list = SolarTermCalendar.getSolarTermDateList(year);

        for (int i = 0; i < 24; i++) {
            if (isSameDay(list.get(i), date)) {
                return solarTerm[i];
            }
        }

        return null;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(date1).equals(df.format(date2));
    }
}
