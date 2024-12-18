import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class InspectedVehicles {
    public static Properties prop = new Properties();

    public static void main(String[] args) throws IOException, SQLException {


        getVehicles();
    }


    public static void getVehicles() throws SQLException, IOException {
        DBConnection dbc = new DBConnection();
        ResultSet chassislist = dbc.getdatafromdb();

            while (chassislist.next()) {

                String chassisnumber = chassislist.getString("CHASSIS_NO");
                String plateinfo = chassislist.getString("PLATE_DETAILS");
                String custominfo = chassislist.getString("CUSTOMS_CERTIFICATE_NO");
                System.out.println(chassisnumber +","+ plateinfo +","+ custominfo);

            }

    }
}