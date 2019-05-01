package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AirconDBDAO implements IAirconDAO
{
    private Properties dbSettings;
    
    public AirconDBDAO()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        importDatabaseSettings();
    }
    
    private void importDatabaseSettings()
    {
        try
        {
            dbSettings = new Properties();
            dbSettings.load(new FileInputStream("C:\\Users\\emil\\Documents\\Netbeans\\air-conditioner\\air-conditioner-server\\dbSettings.properties"));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("DB Settings file not found!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Aircon> getAllAircons()
    {
        List<Aircon> aircons = new ArrayList<>();
        return aircons;
    }
    
    @Override
    public Aircon getAirconById(String id)
    {  
        if (airconExists(id))
        {
            Float temperature = null;
            String temperatureUnit = null;
            Integer powerConsumption = null;
            Float electricityPrice = null;
            String electricityPriceUnit = null;
            String lastUpdate = null;
        
            int dateId = getMostRecentDateId(id);
            int timeId = getMostRecentTimeId(id, dateId);

            try (Connection connection = DriverManager.getConnection(
                        dbSettings.getProperty("connectionString"),
                        dbSettings.getProperty("name"),
                        dbSettings.getProperty("password"));)
            {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT temperature, powerConsumption, electricityPrice, "
                      + "temperatureUnit, electricityPriceUnit, "
                      + "year, month, day, hour " 
                      + "FROM fact_readings "
                      + "INNER JOIN dim_aircons on dim_aircons.id = "
                      + "fact_readings.airconId "
                      + "INNER JOIN dim_date on dim_date.id = fact_readings.dateId "
                      + "INNER JOIN dim_time on dim_time.id = fact_readings.timeId "
                      + "WHERE fact_readings.dateId = ? " 
                      + "AND fact_readings.timeId = ? " 
                      + "AND dim_aircons.name = ?;");

                statement.setInt(1, dateId);
                statement.setInt(2, timeId);
                statement.setString(3, id);
                ResultSet result = statement.executeQuery();

                while(result.next())
                {
                    temperature = result.getFloat("temperature");
                    temperatureUnit = result.getString("temperatureUnit");
                    powerConsumption = result.getInt("powerConsumption");
                    electricityPrice = result.getFloat("electricityPrice");
                    electricityPriceUnit = result.getString("electricityPriceUnit");
                    
                    lastUpdate = String.format("%d-%02d-%02d %02d:00",
                            result.getInt("year"),
                            result.getInt("month"),
                            result.getInt("day"),
                            result.getInt("hour"));
                }
                
                return new Aircon(id, temperature, temperatureUnit,
                        powerConsumption, electricityPrice,
                        electricityPriceUnit, lastUpdate);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            } 
        }
 
        return null;  
    }
    
    @Override
    public void updateAircon(Aircon aircon)
    {
        Map<String, Integer> currentDateTime = getCurrentDateTime();
        String sqlQuery;
        
        if (currentHourMeasurementExists(aircon.getId()))
        {
            sqlQuery = "UPDATE fact_readings " 
                     + "INNER JOIN dim_aircons on dim_aircons.id = "
                     + "fact_readings.airconId " 
                     + "INNER JOIN dim_date on dim_date.id = "
                     + "fact_readings.dateId " 
                     + "INNER JOIN dim_time on dim_time.id = fact_readings.timeId "
                     + "SET temperature = ?, powerConsumption = ?, "
                     + "electricityPrice = ? WHERE name = ? " 
                     + "AND year = ? AND month = ? "
                     + "AND day = ? AND hour = ?;";
        }
        else
        {
            sqlQuery = "INSERT INTO fact_readings(airconId, dateId, timeId, "
                     + "temperature, powerConsumption, electricityPrice) "
                     + "SELECT dim_aircons.id, dim_date.id, dim_time.id, ?, "
                     + "?, ? FROM dim_aircons, dim_date, dim_time " 
                     + "WHERE dim_aircons.name = ? " 
                     + "AND dim_date.year = ? "
                     + "AND dim_date.month = ? " 
                     + "AND dim_date.day = ? "
                     + "AND dim_time.hour = ?;";
        }
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            connection.createStatement().execute("SET SQL_SAFE_UPDATES=0;");   
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            
            statement.setObject(1, aircon.getTemperature());
            statement.setObject(2, aircon.getPowerConsumption());
            statement.setObject(3, aircon.getElectricityPrice());
            statement.setObject(4, aircon.getId());
            statement.setInt(5, currentDateTime.get("year"));
            statement.setInt(6, currentDateTime.get("month"));
            statement.setInt(7, currentDateTime.get("day"));
            statement.setInt(8, currentDateTime.get("hour"));
            
            statement.executeUpdate();
            connection.createStatement().execute("SET SQL_SAFE_UPDATES=1;");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public TemperatureSummary getTemperatureSummary(String id)
    {
        return new TemperatureSummary();
    }
    
    @Override
    public ElectricitySummary getElectricitySummary(String id)
    {
        return new ElectricitySummary();
    }
    
    @Override
    public String getHighestPowerConsumptionAircon()
    {
       return "B";
    }
    
    private boolean currentHourMeasurementExists(String id)
    {
        boolean exists = false;
        Map<String, Integer> currentDateTime = getCurrentDateTime();
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM fact_readings " 
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId " 
                  + "INNER JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "INNER JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE year = ? AND month = ? AND day = ? " 
                  + "AND hour = ? AND name = ? LIMIT 1");
            
            statement.setInt(1, currentDateTime.get("year"));
            statement.setInt(2, currentDateTime.get("month"));
            statement.setInt(3, currentDateTime.get("day"));
            statement.setInt(4, currentDateTime.get("hour"));
            statement.setString(5, id);
            ResultSet result = statement.executeQuery();
            
            while(result.next())
            {
                String name = result.getString("name");
                if (name.equals(id))
                {
                    exists = true;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return exists;
    }
    
    private boolean airconExists(String id)
    {
        boolean exists = false;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM fact_readings " 
                  + "RIGHT JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId WHERE name = ? LIMIT 1;");
            
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            
            while(result.next())
            {
                String name = result.getString("name");
                if (name.equals(id))
                {
                    exists = true;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return exists;
    }
    
    private int getMostRecentDateId(String id)
    {
        Integer dateId = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT MAX(dateId) as 'dateId' "
                  + "FROM fact_readings " 
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId WHERE dim_aircons.name = ?;");
            
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            
            while(result.next())
            {
                dateId = result.getInt("dateId");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return dateId;
    }
    
    private int getMostRecentTimeId(String id, int dateId)
    {
        Integer timeId = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT MAX(timeId) as 'timeId' "
                  + "FROM fact_readings " 
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId WHERE dim_aircons.name = ?" 
                  + "AND fact_readings.dateId = ?;");
            
            statement.setString(1, id);
            statement.setInt(2, dateId);
            ResultSet result = statement.executeQuery();
            
            while(result.next())
            {
                timeId = result.getInt("timeId");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return timeId;
    }
    
    private Map<String, Integer> getCurrentDateTime()
    {
        Map<String, Integer> currentDateTime = new HashMap<>();
        
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        DateFormat monthFormat = new SimpleDateFormat("M");
        DateFormat dayFormat = new SimpleDateFormat("d");
        DateFormat hourFormat = new SimpleDateFormat("H");
	Date now = new Date();
        
        currentDateTime.put("year", Integer.parseInt(yearFormat.format(now)));
        currentDateTime.put("month", Integer.parseInt(monthFormat.format(now)));
        currentDateTime.put("day", Integer.parseInt(dayFormat.format(now)));
        currentDateTime.put("hour", Integer.parseInt(hourFormat.format(now)));

        return currentDateTime;
    }
}
