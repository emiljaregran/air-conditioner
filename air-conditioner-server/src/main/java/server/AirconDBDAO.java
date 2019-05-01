package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        List<String> airconIds = getAllAirconIds();
        
        for (String airconId : airconIds)
        {
            aircons.add(getAirconById(airconId));
        }
        
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
        Map<String, Integer> currentDateTime = getDateTime(new Date());
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
        if (!airconExists(id))
        {
            return null;
        }
        
        Map<String, Integer> current = getDateTime(new Date());
        Map<String, Integer> oneDayBefore = getDateTime(getOneDayBeforeDate());
        
        int currentDateId = getDateId(current.get("year"), current.get("month"),
                                      current.get("day"));
        
        int oneDayBeforeDateId = getDateId(oneDayBefore.get("year"),
                            oneDayBefore.get("month"), oneDayBefore.get("day")); 
        
        LinkedHashMap<Integer, Float> temperatures = getTemperatures24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        float averateTemperature = getAverageTemperature24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        float maxTemperature = getMaxTemperature24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        float minTemperature = getMinTemperature24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        return new TemperatureSummary(temperatures, averateTemperature,
                maxTemperature, minTemperature);
    }
    
    @Override
    public ElectricitySummary getElectricitySummary(String id)
    {
        if (!airconExists(id))
        {
            return null;
        }
        
        Map<String, Integer> current = getDateTime(new Date());
        Map<String, Integer> oneDayBefore = getDateTime(getOneDayBeforeDate());
        
        int currentDateId = getDateId(current.get("year"), current.get("month"),
                                      current.get("day"));
        
        int oneDayBeforeDateId = getDateId(oneDayBefore.get("year"),
                            oneDayBefore.get("month"), oneDayBefore.get("day"));   
        
        float totalConsumedElectricity24h = getTotalConsumedElectricity24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        float electricityCost24h = getElectricityCost24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        int averagePowerConsumption24h = getAveragePowerConsumption24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        int maxPowerConsumption24h = getMaxPowerConsumption24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        int minPowerConsumption24h = getMinPowerConsumption24h(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        int highestElectricityPriceHour = getHighestElectricityPriceHour(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        int lowestElectricityPriceHour = getLowestElectricityPriceHour(id,
                currentDateId, oneDayBeforeDateId, current.get("hour"),
                oneDayBefore.get("hour"));
        
        String electricityPriceUnit = getElectricityPriceUnit(id);
        
        return new ElectricitySummary(totalConsumedElectricity24h,
                    electricityCost24h, electricityPriceUnit,
                    averagePowerConsumption24h, maxPowerConsumption24h,
                    minPowerConsumption24h, highestElectricityPriceHour,
                    lowestElectricityPriceHour);
    }
    
    @Override
    public String getHighestPowerConsumptionAircon()
    {
       return "B";
    }
    
    private float getMinTemperature24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Float minTemperature = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT temperature "
                  + "FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY temperature ASC LIMIT 1;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                minTemperature = result.getFloat("temperature");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return minTemperature;
    }
    
    private float getMaxTemperature24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Float maxTemperature = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT temperature "
                  + "FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY temperature DESC LIMIT 1;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                maxTemperature = result.getFloat("temperature");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return maxTemperature;
    }
    
    private float getAverageTemperature24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Float averageTemperature = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT AVG(temperature) AS averageTemperature "
                  + "FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId " 
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?));");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                averageTemperature = result.getFloat("averageTemperature");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return averageTemperature;
    }
    
    private LinkedHashMap<Integer, Float> getTemperatures24h(String id, 
            int currentDateId, int oneDayBeforeDateId, int currentHour,
            int oneDayBeforeHour)
    {
        LinkedHashMap<Integer, Float> temperatures = new LinkedHashMap<>();
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT temperature, hour " 
                  + "FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId " 
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? " 
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?))"
                  + "ORDER BY hour;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                float temperature = result.getFloat("temperature");
                int hour = result.getInt("hour");
                temperatures.put(hour, temperature);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return temperatures;
    }
    
    private float getElectricityCost24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Float electricityCost = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM((powerConsumption / 1000) * electricityPrice) "
                  + "as electricityCost " 
                  + "FROM fact_readings " 
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? " 
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?));");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                electricityCost = result.getFloat("electricityCost");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return electricityCost;
    }
    
    private String getElectricityPriceUnit(String id)
    {
        String electricityPriceUnit = null;
        
        try (Connection connection = DriverManager.getConnection(
                        dbSettings.getProperty("connectionString"),
                        dbSettings.getProperty("name"),
                        dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT electricityPriceUnit "
                  + "FROM dim_aircons WHERE name = ?;");
            
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                electricityPriceUnit = result.getString("electricityPriceUnit");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return electricityPriceUnit; 
    }
    
    private int getLowestElectricityPriceHour(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer lowestElectricityPriceHour = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT hour FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId " 
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? " 
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY electricityPrice ASC LIMIT 1;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                lowestElectricityPriceHour = result.getInt("hour");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return lowestElectricityPriceHour;
    }
    
    private int getHighestElectricityPriceHour(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer highestElectricityPriceHour = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT hour FROM fact_readings "
                  + "INNER JOIN dim_aircons on dim_aircons.id = "
                  + "fact_readings.airconId " 
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? " 
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY electricityPrice DESC LIMIT 1;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                highestElectricityPriceHour = result.getInt("hour");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return highestElectricityPriceHour;
    }
    
    private int getMinPowerConsumption24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer minConsumption = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT MIN(powerConsumption) AS minConsumption " 
                  + "FROM fact_readings INNER JOIN dim_aircons on " 
                  + "dim_aircons.id = fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY dim_date.id DESC, dim_time.id DESC;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                minConsumption = result.getInt("minConsumption");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return minConsumption;
    }
    
    private int getMaxPowerConsumption24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer maxConsumption = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT MAX(powerConsumption) AS maxConsumption " 
                  + "FROM fact_readings INNER JOIN dim_aircons on " 
                  + "dim_aircons.id = fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY dim_date.id DESC, dim_time.id DESC;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                maxConsumption = result.getInt("maxConsumption");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return maxConsumption;
    }
    
    private int getAveragePowerConsumption24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer averageConsumption = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT AVG(powerConsumption) AS averageConsumption " 
                  + "FROM fact_readings INNER JOIN dim_aircons on " 
                  + "dim_aircons.id = fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY dim_date.id DESC, dim_time.id DESC;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                averageConsumption = result.getInt("averageConsumption");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return averageConsumption;
    }
    
    private float getTotalConsumedElectricity24h(String id, int currentDateId, 
            int oneDayBeforeDateId, int currentHour, int oneDayBeforeHour)
    {
        Integer totalConsumption = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(powerConsumption) AS totalConsumption " 
                  + "FROM fact_readings INNER JOIN dim_aircons on " 
                  + "dim_aircons.id = fact_readings.airconId "
                  + "RIGHT JOIN dim_date on dim_date.id = fact_readings.dateId "
                  + "LEFT JOIN dim_time on dim_time.id = fact_readings.timeId "
                  + "WHERE name = ? "
                  + "AND ((dateId = ? AND hour BETWEEN ? AND 23) "
                  + "OR (dateId = ? AND hour BETWEEN 0 AND ?)) "
                  + "ORDER BY dim_date.id DESC, dim_time.id DESC;");
            
            statement.setString(1, id);
            statement.setInt(2, oneDayBeforeDateId);
            statement.setInt(3, oneDayBeforeHour);
            statement.setInt(4, currentDateId);
            statement.setInt(5, currentHour);
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                totalConsumption = result.getInt("totalConsumption");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return totalConsumption / 1000.0f;
    }
    
    private List<String> getAllAirconIds()
    {
        List<String> airconIds = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection(
                        dbSettings.getProperty("connectionString"),
                        dbSettings.getProperty("name"),
                        dbSettings.getProperty("password"));)
        {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT name FROM dim_aircons");
            
            while (result.next())
            {
                airconIds.add(result.getString("name"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return airconIds;
    }
    
    private boolean currentHourMeasurementExists(String id)
    {
        boolean exists = false;
        Map<String, Integer> currentDateTime = getDateTime(new Date());
        
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
    
    private int getTimeId(int hour)
    {
        Integer timeId = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM dim_time WHERE hour = ?;");
            
            statement.setInt(1, hour);
            
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                timeId = result.getInt("id");
            }
            
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return timeId;
    }
    
    private int getDateId(int year, int month, int day)
    {
        Integer dateId = null;
        
        try (Connection connection = DriverManager.getConnection(
                    dbSettings.getProperty("connectionString"),
                    dbSettings.getProperty("name"),
                    dbSettings.getProperty("password"));)
        {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM dim_date WHERE year = ? "
                  + "AND month = ? AND day = ?;");
            
            statement.setInt(1, year);
            statement.setInt(2, month);
            statement.setInt(3, day);
            
            ResultSet result = statement.executeQuery();
            
            while (result.next())
            {
                dateId = result.getInt("id");
            }  
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return dateId;
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
    
    private Map<String, Integer> getDateTime(Date date)
    {
        Map<String, Integer> currentDateTime = new HashMap<>();
        
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        DateFormat monthFormat = new SimpleDateFormat("M");
        DateFormat dayFormat = new SimpleDateFormat("d");
        DateFormat hourFormat = new SimpleDateFormat("H");
        
        currentDateTime.put("year", Integer.parseInt(yearFormat.format(date)));
        currentDateTime.put("month", Integer.parseInt(monthFormat.format(date)));
        currentDateTime.put("day", Integer.parseInt(dayFormat.format(date)));
        currentDateTime.put("hour", Integer.parseInt(hourFormat.format(date)));

        return currentDateTime;
    }
    
    private Date getOneDayBeforeDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date oneDayBefore = calendar.getTime();
        
        return oneDayBefore;
    }
}
