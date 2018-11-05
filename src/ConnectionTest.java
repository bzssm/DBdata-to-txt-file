/**
 * Created by Administrator on 2016/4/28.
 * Used for big size export from database.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ConnectionTest {
    public String returnQueryLine(ResultSet result) {
        String line = "";
        try {
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++)
                line = line + result.getMetaData().getColumnName(i + 1) + ":" + result.getString(i + 1) + "|||";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    public String returnTextLine(ResultSet result) {
        String line = "";
        try {
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++)
                line += result.getString(i + 1) + "|||";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    public String totalRowSQL(String sql) {
        return "select count(*) from (" + sql + ")";
    }

    public void testOracle(int type, String sql) {
        Connection con = null;// 创建一个数据库连接
        PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
        ResultSet result = null;// 创建一个结果集对象
        String url = null;
        String user = null;
        String password = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
            System.out.println("开始尝试连接数据库！");
            switch (type) {
                case 1:
                    url = "jdbc:oracle:" + "thin:@(DESCRIPTION =" + "    (FAILOVER=ON)" + "    (ADDRESS_LIST =" + "      (LOAD_BALANCE=on)" + "      (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.201.152)(PORT = 1521))" + "      (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.201.153)(PORT = 1521))" + "      (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.201.153)(PORT = 1521))" + "    )" + "    (CONNECT_DATA =" + "      (SERVICE_NAME = DAAS)" + "    )" + "  )";
                    user = "username";// 用户名,系统默认的账户名
                    password = "password";// 你安装时选设置的密码
                    break;
                case 2:
                    url = "jdbc:oracle:" + "thin:@192.168.202.11:1521:ORCL";// 测试
                    user = "username";// 用户名,系统默认的账户名
                    password = "password";// 你安装时选设置的密码
                    break;
                case 3:
                    url = "jdbc:oracle:" + "thin:@192.168.201.211:1521:ORCL";// etl
                    user = "username";
                    password = "password";
                    break;
            }
            //设置日期格式，写入日期
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            //文件写入初始化
            FileOutputStream fs = new FileOutputStream(new File("D:\\javasqlExport\\Export" + df.format(new Date()) + ".txt"));
            PrintStream p = new PrintStream(fs);
            //数据库连接初始化
            con = DriverManager.getConnection(url, user, password);// 获取连接
            System.out.println("连接成功！");
            //查询总行数
            String totalRowSQL = totalRowSQL(sql);
            String totalRowNumber=null;
            pre = con.prepareStatement(totalRowSQL);
            result = pre.executeQuery();
            while (result.next()) {
                totalRowNumber = result.getString(1);
            }

            //输入sql查询
            pre = con.prepareStatement(sql);// 实例化预编译语句
            result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
            long startTime = System.currentTimeMillis();
            while (result.next()) {
                System.out.println("当前行:" + result.getRow() + "||" + "总行数:" + totalRowNumber);
                System.out.println(returnQueryLine(result));
                p.println(returnTextLine(result));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("用时:" + (endTime - startTime));
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
                // 注意关闭的顺序，最后使用的最先关闭
                if (result != null)
                    result.close();
                if (pre != null)
                    pre.close();
                if (con != null)
                    con.close();
                System.out.println("数据库连接已关闭！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String args[]) {
        while (true) {
            ConnectionTest test = new ConnectionTest();
            System.out.println("1:生产   2:测试   3:etl   -1:退出");
            Scanner sc1 = new Scanner(System.in);
            int type = Integer.parseInt(sc1.nextLine());
            if (type == -1)
                System.exit(0);
            System.out.println("输入sql语句：");
            Scanner sc2 = new Scanner(System.in);
            String sql = sc2.nextLine();
            test.testOracle(type, sql);
        }
    }
}
