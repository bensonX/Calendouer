package cn.sealiu.calendouer;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import cn.sealiu.calendouer.until.LunarCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void calendar() throws Exception {
        List<String> list = LunarCalendar.getLunarCalendarStr(new Date());
        System.out.println(list.toString());
    }
}