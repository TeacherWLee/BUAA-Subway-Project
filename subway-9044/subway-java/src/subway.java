// import com.sun.org.apache.xpath.internal.operations.String;

import java.io.*;
import java.util.*;


// 站点，保存站点名称和ID
class Station {
    String stationName;
    Set<Integer> setStationId;

    Station() {
        stationName = "";
        setStationId = new TreeSet<>();
    }
}


// 路径，距离和经过的中转站列表
class Path {
    int nFDistance;
    Station stationLastStationInPath;
    Vector<Station> listStationsInPath;

    Path() {
        nFDistance = 0;
        stationLastStationInPath = new Station();
        listStationsInPath = new Vector<>();
    }
}

// 地图，负责站点解析和路径计算
class Map {
    private Vector<String> listSubwayInfo = new Vector<>();
    private String[] arrSubwayStationsData = {
            "1：刘园，西横堤，果酒厂，本溪路，勤俭道，洪湖里，西站，西北角，西南角，二纬路，海光寺，鞍山道，营口道，小白楼，下瓦房，南楼，土城，陈塘庄，复兴门，华山里，财经大学，双林，李楼，梨双路北，洪泥河东，高庄子，上郭庄，北洋村，国展路，东沽路，咸水沽北，双桥河",
            "2：曹庄，卞兴，芥园西道，咸阳路，长虹公园，广开四马路，西南角，鼓楼，东南角，建国道，天津站，远洋国际中心，顺驰桥，靖江路，翠阜新村，屿东城，登州路，国山路，空港经济区，滨海国际机场",
            "3：小淀，丰产河，华北集团，天士力，宜兴埠，张兴庄，铁东路，北站，中山路，金狮桥，天津站，津湾广场，和平路，营口道，西康路，吴家窑，天塔，周邓纪念馆，红旗南路，王顶堤，华苑，大学城，高新区，学府工业区，杨伍庄，南站",
            "5：北辰科技园北，丹河北道，北辰道，职业大学，淮河道，辽河北道，宜兴埠北，张兴庄，志成路，思源路，建昌道，金钟河大街，月牙河，幸福公园，靖江路，成林道，津塘路，直沽，下瓦房，西南楼，文化中心，天津宾馆，肿瘤医院，体育中心，凌宾路，昌凌路，中医一附院，李七庄南",
            "6：南孙庄，南何庄，大毕庄，金钟街，徐庄子，金钟河大街，民权门，北宁公园，北站，新开河，外院附中，天泰路，北运河，北竹林，西站，复兴路，人民医院，长虹公园，宜宾道，鞍山西道，天拖，一中心医院，红旗南路，迎风道，南翠屏，水上公园东路，肿瘤医院，天津宾馆，文化中心，乐园道，尖山路，黑牛城道，梅江道，左江道，梅江公园，梅江会展中心，解放南路，洞庭路，梅林路",
            "9：天津站，大王庄，十一经路，直沽，东兴路，中山门，一号桥，二号桥，张贵庄，新立，东丽开发区，小东庄，军粮城，钢管公司，胡家园，塘沽，泰达，市民广场，太湖路，会展中心，东海路"
    };

    private int nMaxDistance = 999999;

    private static HashMap<String, Station> mapNametoStation = new HashMap<>();
    private static HashMap<Integer, Station> mapStationIdtoStation = new HashMap<>();
    private static HashMap<Integer, Set<Station>> mapLinetoTransferStation = new HashMap<>();
    private static HashMap<String, Integer> mapTransferStationNametoDistance = new HashMap<>();


    // ------------------------------------------------------------------------------------------------
    // 加载地铁线路数据
    public void loadSubwayFile(String strSubwayFileName) {
        File fSubway = new File(strSubwayFileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fSubway));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                if (tempString.startsWith("\uFEFF")) {
                    tempString = tempString.substring(1, tempString.length());
                }
                listSubwayInfo.addElement(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // unit test
//        for (String strSubwayLine: listSubwayInfo) {
//            System.out.println(strSubwayLine);
//        }


        parseSubwayStationsData();
    }

    // 从数组加载数据
    public void loadsubwayArray() {
        for (String strLineInfo: arrSubwayStationsData) {
            listSubwayInfo.addElement(strLineInfo);
        }

        // unit test
//        for (String strSubwayLine: listSubwayInfo) {
//            System.out.println(strSubwayLine);
//        }

        parseSubwayStationsData();
    }


    // ----------------------------------------------
    // 地铁数据处理
    void parseSubwayStationsData() {
        for (String strSubwayLine: listSubwayInfo) {
            parseSubwayLineData(strSubwayLine);
        }
    }

    void parseSubwayLineData(String strSubwayLine) {

        String[] arrLineAndStations = strSubwayLine.split("：");     // 划分地铁线路和该线路所有站点
        if (arrLineAndStations.length != 2) {
            System.out.println("地铁数据错误" + strSubwayLine);
            return;
        }

        int nLine = getLineNumber(arrLineAndStations[0]);
        if (nLine == -1) {
            System.out.println("地铁线路号数据错误" + strSubwayLine);
        }


        String[] arrStrStationNames = arrLineAndStations[1].split("，");
        for (int i=0; i < arrStrStationNames.length; i++) {
            String strStationName = arrStrStationNames[i];
            int nStationId = nLine*1000 + i+1;

            Station station = new Station();
            station.stationName = strStationName;
            station.setStationId.add(nStationId);


            mapStationIdtoStation.put(nStationId, station);


            if (!mapNametoStation.containsKey(strStationName)) {
                mapNametoStation.put(strStationName, station);
            } else {
                // 如果站点名字存在，证明是中转站
                Station stationExistedTransferStation = mapNametoStation.get(strStationName);
                stationExistedTransferStation.setStationId.add(nStationId);

                updateTransferStation(stationExistedTransferStation);
            }
        }
    }

    void updateTransferStation(Station stationTransferStation) {
        mapTransferStationNametoDistance.put(stationTransferStation.stationName, nMaxDistance);

        for (int nTStationId: stationTransferStation.setStationId) {
            int nLine = getLineNumber(nTStationId);

            if (!mapLinetoTransferStation.containsKey(nLine)) {
                Set<Station> setTStations = new HashSet<>();
                setTStations.add(stationTransferStation);

                mapLinetoTransferStation.put(nLine, setTStations);
            } else {
                Set<Station> setTStations = mapLinetoTransferStation.get(nLine);
                setTStations.add(stationTransferStation);
            }
        }
    }



    // -----------------------------------------------------------------------------------
    // 最优路径规划
    Path shortedPath(String strStartStationName, String strEndStationName) {
        // todo: 进行一些合法性检查

        Station stationStart = mapNametoStation.get(strStartStationName);
        Station stationEnd = mapNametoStation.get(strEndStationName);

        mapTransferStationNametoDistance.put(strEndStationName, nMaxDistance);

        Path pathStart = new Path();
        pathStart.nFDistance = 0;
        pathStart.stationLastStationInPath = stationStart;
        pathStart.listStationsInPath.addElement(stationStart);

        Path shortedPath = new Path();
        shortedPath.nFDistance = nMaxDistance;

        Stack<Path> stackAllPaths = new Stack<>();
        stackAllPaths.push(pathStart);


        while (!stackAllPaths.empty()) {
            Path pathCurrent = stackAllPaths.pop();

            if (pathCurrent.nFDistance > shortedPath.nFDistance) {
                continue;
            }


            int nBDistance = getStationsDistance(pathCurrent.stationLastStationInPath, stationEnd);

            if (nBDistance == 0) {      // 到达终止节点
                if (pathCurrent.nFDistance < shortedPath.nFDistance) {
                    shortedPath = pathCurrent;
                }
                continue;
            }


            for (String strTStationName: mapTransferStationNametoDistance.keySet()) {
                Station stationTransfer = mapNametoStation.get(strTStationName);
                int nDistanceDelta = getStationsDistance(pathCurrent.stationLastStationInPath, stationTransfer);
                int nTStationDistance = pathCurrent.nFDistance + nDistanceDelta;

                if (nTStationDistance >= mapTransferStationNametoDistance.get(strTStationName)) {
                    continue;
                }

                mapTransferStationNametoDistance.put(strTStationName, nTStationDistance);

                if (nTStationDistance<1000 && nTStationDistance<shortedPath.nFDistance) {
                    Path pathNew = new Path();
                    pathNew.nFDistance = nTStationDistance;
                    pathNew.stationLastStationInPath = stationTransfer;
                    pathNew.listStationsInPath = new Vector<>(pathCurrent.listStationsInPath);
                    pathNew.listStationsInPath.addElement(stationTransfer);
                    stackAllPaths.push(pathNew);
                }
            }
        }

        return shortedPath;
    }


    // -----------------------------------------------------------------------------------
    // 打印一个路径
    void printPath(Path path) {

        String strFormatedPath = formatPath(path);
        System.out.println(strFormatedPath);
    }

    void printPath(Path path, String strOutFileName) {
        String strFormatedPath = formatPath(path);

        printFile(strFormatedPath, strOutFileName);
    }

    String formatPath(Path path) {
        StringBuffer strRst = new StringBuffer();

        int nCurrentLine = -1;

        if (path.listStationsInPath.size() == 0) {
            return "";
        }

        if (path.listStationsInPath.size() == 1) {
            return path.stationLastStationInPath.stationName;
        }

        Vector<Station> listStations = path.listStationsInPath;

        for (int n=1; n<listStations.size(); n++) {
            Station stationStart = listStations.get(n-1);
            Station stationEnd = listStations.get(n);

            int nLineNum = getLineNumber(stationStart, stationEnd);

            if (nLineNum != nCurrentLine) {
                nCurrentLine = nLineNum;
                strRst.append(String.format("%d号线\r\n", nCurrentLine));
            }

            if (n == 1) {
                strRst.append(String.format("%s\r\n", stationStart.stationName));
            }

            for (String strStationName: listStationsInArea(stationStart, stationEnd)) {
                strRst.append(String.format("%s\r\n", strStationName));
            }
        }

        return strRst.toString();
    }

    Vector<String> listStationsInArea(Station stationStart, Station stationEnd) {
        Vector<String> listStations = new Vector<String>();
        int nLineNumber = getLineNumber(stationStart, stationEnd);

        int nStartId = 0;
        int nEndId = 0;

        for (int nId: stationStart.setStationId) {
            if (Math.abs(nId-(nLineNumber*1000))<1000) {
                nStartId = nId;
            }
        }

        for (int nId: stationEnd.setStationId) {
            if (Math.abs(nId-(nLineNumber*1000))<1000) {
                nEndId = nId;
            }
        }

        if (nStartId == nEndId) {
            return listStations;
        }

        int nStep = 1;

        if (nEndId < nStartId) {
            nStep = -1;
        }

        int nIndexId = nStartId + nStep;
        while (nIndexId != nEndId) {
            String strSName = mapStationIdtoStation.get(nIndexId).stationName;
            listStations.addElement(strSName);
            nIndexId += nStep;
        }

        String strName = mapStationIdtoStation.get(nEndId).stationName;
        listStations.addElement(strName);

        return listStations;
    }

    // ------------------------------------------------------------------------------------
    // 获取特定地铁线路数据
    void printStationsOfLine(int nLineNum, String strOutFile) {
        StringBuffer strRst = new StringBuffer();
        strRst.append(String.format("%d号线\r\n", nLineNum));

        for (int i = 1; i < 90; i++) {
            int nStationId = nLineNum * 1000 + i;

            if (mapStationIdtoStation.containsKey(nStationId)) {
                strRst.append(mapStationIdtoStation.get(nStationId).stationName + "\r\n");
            } else {
                break;
            }
        }

        printFile(strRst.toString(), strOutFile);
    }


    // ------------------------------------------------------------------------------------
    // 工具函数
    int getLineNumber(int nStationId) {
        return nStationId / 1000;
    }

    int getLineNumber(String strLine) {
        String s = "1";

        if (strLine.length() >= 1) {
            String strNumber = strLine.substring(0, 1);
            return Integer.parseInt(strNumber);
        }
        return -1;
    }


    int getStationsDistance(Station S1, Station S2) {
        int nMinDistance = nMaxDistance;
        Set<Integer> S1Ids = S1.setStationId;
        Set<Integer> S2Ids = S2.setStationId;
        for (int id1: S1Ids) {
            for (int id2: S2Ids) {
                int nDistance = Math.abs(id1-id2);

                if (nDistance < nMinDistance) {
                    nMinDistance = nDistance;
                }
            }
        }

        return nMinDistance;
    }

    int getLineNumber(Station S1, Station S2) {
        for (int nS1Id: S1.setStationId) {
            int nS1LineNum = getLineNumber(nS1Id);

            for (int nS2Id: S2.setStationId) {
                int nS2LineNumber = getLineNumber(nS2Id);

                if (nS1LineNum == nS2LineNumber) {
                    return nS1LineNum;
                }
            }
        }
        return -1;
    }

    void printFile(String strContent, String strOutFile) {
        try {
            File file = new File(strOutFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), false);

            fileWriter.write(strContent.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };



    // ---------------------------------------------------------------------------------------------------
    // unitest 打印一些信息

    void test() {
        for (int n=1; n < 10; n++) {
            for (int i=1; i < 30; i++) {
                int nStationId = n*1000 + i;

                if (mapStationIdtoStation.containsKey(nStationId)) {
                    System.out.printf("%d-%s", nStationId, mapStationIdtoStation.get(nStationId).stationName);
                } else {
                    break;
                }
            }
            System.out.printf("\n");
        }


        System.out.println("打印所有站点名称从mapNametoStation：");
        for (String strSName: mapNametoStation.keySet()) {
            Station station = mapNametoStation.get(strSName);
            System.out.printf("%s, ", station.stationName);
        }


        System.out.println("\n打印所有线路中转站点从mapLinetoTransferStation：");
        for (int n=1; n < 10; n++) {
            if (!mapLinetoTransferStation.containsKey(n)) {
                continue;
            }

            System.out.printf("%d号线：", n);
            Set<Station> setTStations = mapLinetoTransferStation.get(n);
            for (Station tStation: setTStations) {
                System.out.printf("%s - ", tStation.stationName);
            }
            System.out.printf("\n");
        }


        System.out.println("打印所有终端站距离");
        for (String strStationName: mapTransferStationNametoDistance.keySet()) {
            System.out.printf("%s-%d", strStationName, mapTransferStationNametoDistance.get(strStationName));
        }

    }
}


public class subway {
    public static void main(String[] args) {

        // -------------------------------------
        // 解析参数
        String strSubwayFileName = null;
        int nLineNum = -1;
        String strOutFileName = null;
        String strStartStationName = null;
        String strEndStationName = null;

        for (int n=0; n<args.length; n++) {
            String strArg = args[n];

            if (strArg.equals("-map")) {
                n += 1;
                if (n < args.length) {
                    strSubwayFileName = args[n];
                } else {
                    System.out.println("-map 参数后无地铁路线信息文件，程序退出。");
                    return;
                }
            } else if (strArg.equals("-a")) {
                n += 1;
                if (n < args.length) {

                    String strLineNum = args[n];
                    if (strLineNum.length() >= 1) {
                        String strNumber = strLineNum.substring(0, 1);
                        nLineNum = Integer.parseInt(strNumber);
                    }
                }
            } else if (strArg.equals("-o")) {
                n += 1;
                if (n < args.length) {
                    strOutFileName = args[n];
                } else {
                    System.out.println("-o 参数后无信息输出文件，程序退出。");
                    return;
                }
            } else if (strArg.equals("-b")) {
                if (n+2 > args.length) {
                    System.out.println("-o 参数后无信息输出文件，程序退出。");
                    return;
                }

                strStartStationName = args[n+1];
                strEndStationName = args[n+2];
                n += 2;
            } else {
                System.out.println("参数不正确，程序退出。");
                return;
            }
        }

        // ----------------------------------------------------------------
        // 处理地铁地图。
        Map mapSubway = new Map();

        if (strSubwayFileName != null) {
            mapSubway.loadSubwayFile(strSubwayFileName);
        } else {
            mapSubway.loadsubwayArray();
        }

        if (nLineNum != -1) {
            if (strOutFileName == null) {
                System.out.println("-o 参数错误");
            }
            mapSubway.printStationsOfLine(nLineNum, strOutFileName);
        } else if (strStartStationName != null) {
            if (strEndStationName == null || strOutFileName == null) {
                System.out.println("-b 或 -o 参数错误");
            }

            Path shortedPath = mapSubway.shortedPath(strStartStationName, strEndStationName);
            mapSubway.printPath(shortedPath, strOutFileName);
        }
    }
}


