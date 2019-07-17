import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

class Station {
    String stationName;
    Set<Integer> setStationId;

    Station() {
        stationName = "";
        setStationId = new TreeSet<>();
    }
}

class Map {

    private String[] arrSubwayStationsData = {
            "1：园，西横堤，果酒厂，本溪路，勤俭道，洪湖里，西站，西北角，西南角，二纬路，海光寺，鞍山道，营口道，小白楼，下瓦房，南楼，土城，陈塘庄，复兴门，华山里，财经大学，双林，李楼，梨双路北，洪泥河东，高庄子，上郭庄，北洋村，国展路，东沽路，咸水沽北，双桥河",
            "2：曹庄，卞兴，芥园西道，咸阳路，长虹公园，广开四马路，西南角，鼓楼，东南角，建国道，天津站，远洋国际中心，顺驰桥，靖江路，翠阜新村，屿东城，登州路，国山路，空港经济区，滨海国际机场",
            "3：小淀，丰产河，华北集团，天士力，宜兴埠，张兴庄，铁东路，北站，中山路，金狮桥，天津站，津湾广场，和平路，营口道，西康路，吴家窑，天塔，周邓纪念馆，红旗南路，王顶堤，华苑，大学城，高新区，学府工业区，杨伍庄，南站",
            "5：北辰科技园北，丹河北道，北辰道，职业大学，淮河道，辽河北道，宜兴埠北，张兴庄，志成路，思源路，建昌道，金钟河大街，月牙河，幸福公园，靖江路，成林道，津塘路，直沽，下瓦房，西南楼，文化中心，天津宾馆，肿瘤医院，体育中心，凌宾路，昌凌路，中医一附院，李七庄南",
            "6：南孙庄，南何庄，大毕庄，金钟街，徐庄子，金钟河大街，民权门，北宁公园，北站，新开河，外院附中，天泰路，北运河，北竹林，西站，复兴路，人民医院，长虹公园，宜宾道，鞍山西道，天拖，一中心医院，红旗南路，迎风道，南翠屏，水上公园东路，肿瘤医院，天津宾馆，文化中心，乐园道，尖山路，黑牛城道，梅江道，左江道，梅江公园，梅江会展中心，解放南路，洞庭路，梅林路",
            "9：天津站，大王庄，十一经路，直沽，东兴路，中山门，一号桥，二号桥，张贵庄，新立，东丽开发区，小东庄，军粮城，钢管公司，胡家园，塘沽，泰达，市民广场，太湖路，会展中心，东海路"
    };

    private int nMaxDistance = 999;

    private static HashMap<String, Station> mapNametoStation = new HashMap<>();
    private static HashMap<Integer, Station> mapIdtoStation = new HashMap<>();
    private static HashMap<Integer, Station> mapLinetoTransferStation = new HashMap<>();
    private static HashMap<Station, Integer> mapTransferStationtoDistance = new HashMap<>();




    void go() {
        parseSubwayStationsData();
    }

    void test() {

    }


    // ----------------------------------------------
    // 地铁数据处理
    void parseSubwayStationsData() {
        for (String strSubwayLine: arrSubwayStationsData) {
            parseSubwayLineData(strSubwayLine);
        }
    }

    void parseSubwayLineData(String strSubwayLine) {

        String[] arrLineAndStations = strSubwayLine.split("：");     // 划分地铁线路和该线路所有站点
        if (arrLineAndStations.length != 2) {
            System.out.println("地铁数据错误" + strSubwayLine);
            return;
        }


        int nLine = Integer.parseInt(arrLineAndStations[0]);        // 解析地铁线路号
        // todo: 解析地铁线路错误处理


        String[] arrStrStationNames = arrLineAndStations[1].split("，");
        for (int i=0; i < arrStrStationNames.length; i++) {
            String strStationName = arrStrStationNames[i];
            int nId = nLine*100 + i+1;

            Station station = new Station();
            station.stationName = strStationName;
            station.setStationId.add(nId);

            if (!mapNametoStation.containsKey(strStationName)) {
                mapNametoStation.put(strStationName, station);
            } else {

            }

            mapIdtoStation.put(nId, station);

        }

//        for (String s1: arrStationName) {
//            System.out.println(s1);
//        }
    }
}


public class subway {
    public static void main(String[] args) {
        Map m = new Map();
        m.go();
        m.test();

    }
}


