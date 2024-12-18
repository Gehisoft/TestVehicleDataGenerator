import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    public ResultSet getdatafromdb() throws SQLException, IOException {

        String plateflag = "IS NULL";
        String customflag = "IS NULL";


        FileReader fl = new FileReader("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(fl);
        String envvalue = prop.getProperty("env");
        System.out.println("The testing environment is " + envvalue);
        String connectionstring = "jdbc:oracle:thin:@" + prop.getProperty(envvalue + ".ip") + ":" + prop.getProperty(envvalue + ".port") + ":" + prop.getProperty(envvalue + ".dbname");
        String dbusername = prop.getProperty(envvalue + ".bdusername");
        String dbupassword = prop.getProperty(envvalue + ".dbpassword");
        String platedvehicle = prop.getProperty("plated");
        String customvehicle = prop.getProperty("customno");

        if (platedvehicle.equalsIgnoreCase("yes")) {

            plateflag = "IS NOT NULL";

        }
        if (customvehicle.equalsIgnoreCase("yes")) {

            customflag = "IS NOT NULL";
        }

        /////// Retreiving values from configuration file /////////////////////////////////////////////////

        int attempts = Integer.parseInt(prop.getProperty("previousattempt"));
        int result = Integer.parseInt(prop.getProperty("inspectionresult"));
        int days = Integer.parseInt(prop.getProperty("numberofpreviousdays"));
        Connection con = DriverManager.getConnection(connectionstring, dbusername, dbupassword);

        PreparedStatement stat = null;

        String query = "SELECT v.CHASSIS_NO , v.PLATE_DETAILS ,  v.CUSTOMS_CERTIFICATE_NO \n" +
                "FROM CI_TRS_TRANSACTIONS t, CI_TRS_VEHICLES v , CI_TRS_SERVICES s\n" +
                "WHERE t.ID = v.TRS_ID \n" +
                "AND t.ID = s.TRS_ID \n" +
                "AND s.CURRENT_ATTEMPT_NUMBER = ?\n" +
                "AND s.\"RESULT\" = ? \n" +
                "AND t.CONFIRMATION_DATE >= SYSDATE-?\n" +
                "AND s.SRV_ID = 110 \n" +
                "AND t.STATUS IN (14,15)\n" +
                "AND (v.PLATE_DETAILS " + plateflag + " AND v.CUSTOMS_CERTIFICATE_NO " + customflag + " )\n" +
                "ORDER BY t.CONFIRMATION_DATE DESC ";


        stat = con.prepareStatement(query);

        stat.setInt(1, attempts);
        stat.setInt(2, result);
        stat.setInt(3, days);

        if (result == 1) {
            System.out.println("These vehicles were previously passed");
        } else {
            System.out.println("These vehicles were previously failed");
        }

        ResultSet queryresult = stat.executeQuery();
        return queryresult;

    }


}
