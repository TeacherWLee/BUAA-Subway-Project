import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import subwaycore.*;

public class subway {
    public static void main(String[] args) {

        // -------------------------------------
        // 解析参数
        String strSubwayFileName = null;
        String strLineNum = null;
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

                    strLineNum = args[n];
//                    if (strLineNum.length() >= 1) {
//                        String strNumber = strLineNum.substring(0, 1);
//                        nLineNum = Integer.parseInt(strNumber);
//                    }
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
        Core mapSubway = new Core();

        if (strSubwayFileName != null) {
            int n = mapSubway.loadMap(strSubwayFileName);
            if (n != -1) {
                System.out.printf("OK: Load \"%s\" successful, load %d subway items.\r\n", strSubwayFileName, n);
            }
        } else {
            mapSubway.loadMap();
        }

        if (strLineNum != null) {
            if (strOutFileName == null) {
                System.out.println("Fail: No parameter of \"-o\"");
            }
            Vector<String> vcStations = mapSubway.getStations(strLineNum);
            if (vcStations != null) {
                System.out.printf("OK: Get stations of line %s successful.", strLineNum);
            }
            FileProcess fp = new FileProcess();
            fp.printFile(vcStations, strOutFileName);

        } else if (strStartStationName != null) {
            if (strEndStationName == null || strOutFileName == null) {
                System.out.println("-b 或 -o 参数错误");
            }

            Vector<String> vcStations = mapSubway.getShortPath(strStartStationName, strEndStationName);
            FileProcess fp = new FileProcess();
            fp.printFile(vcStations, strOutFileName);
        }
    }


}


