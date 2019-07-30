// package test;
import org.junit.Test;
import static org.junit.Assert.*;

import subwaycore.Core;

import java.util.Vector;

public class CoreTest {

    @Test
    public void loadMap() {
        Core c = new Core();
        assertEquals(6, c.loadMap("subway.txt"));
    }

    @Test
    public void getStations() {
        Core c = new Core();
        c.loadMap("subway.txt");
        Vector<String> stations = c.getStations("2号线");

        Vector<String> rst = new Vector<>();
        rst.addElement("曹庄");
        rst.addElement("卞兴");
        rst.addElement("芥园西道");
        rst.addElement("咸阳路");
        rst.addElement("长虹公园");
        rst.addElement("广开四马路");
        rst.addElement("西南角");
        rst.addElement("鼓楼");
        rst.addElement("东南角");
        rst.addElement("建国道");
        rst.addElement("天津站");
        rst.addElement("远洋国际中心");
        rst.addElement("顺驰桥");
        rst.addElement("靖江路");
        rst.addElement("翠阜新村");
        rst.addElement("屿东城");
        rst.addElement("登州路");
        rst.addElement("国山路");
        rst.addElement("空港经济区");
        rst.addElement("滨海国际机场");

        assertEquals(stations, rst);
    }

    @Test
    public void getShortPath() {
        Core c = new Core();
        c.loadMap("subway.txt");

        Vector<String> stations = c.getShortPath("西南角", "文化中心");

        Vector<String> rst = new Vector<>();
        rst.addElement("9");
        rst.addElement("西南角");
        rst.addElement("二纬路");
        rst.addElement("海光寺");
        rst.addElement("鞍山道");
        rst.addElement("营口道");
        rst.addElement("小白楼");
        rst.addElement("下瓦房");
        rst.addElement("5号线");
        rst.addElement("西南楼");
        rst.addElement("文化中心");

        assertEquals(stations, rst);
    }
}