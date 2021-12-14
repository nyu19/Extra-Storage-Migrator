package me.nyu19;

import org.yaml.snakeyaml.Yaml;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static String databaseUrl = null;
    private static String userdataFolder = null;
    private static Connection conn = null;

    public static boolean sqlWriter(String playerUUID, int enabled, long space, String partner,String unused,String materials) throws ClassNotFoundException {
        String dbUrl = null;

        if (databaseUrl!=null && databaseUrl.endsWith(".db")){
            dbUrl = "jdbc:sqlite:" + databaseUrl;
        }
        else{
            System.out.println("Database Not Found!");
            System.exit(0);
        }

        String query = String.format("INSERT INTO playerdata(playerUUID,texture,enabled,space,partners,unused,materials) VALUES('%s',\"\",'%d','%d','%s','%s','%s')",
                playerUUID,enabled,space,partner,unused,materials);
        Class.forName("org.sqlite.JDBC");

        try{
//            conn = DriverManager.getConnection(dbUrl);
            PreparedStatement prep = conn.prepareStatement(query); //main firing
            prep.executeUpdate();
            return true;
        }catch (SQLException se){
            if (se.getMessage().startsWith("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
                System.out.println("Data Already Exists... Ignoring...");
            return false;
        }

    }
    public static String simplify(String inputStr){
        if (inputStr.equals("{}")){
            return inputStr;
        }
        return inputStr.toString().replace("{","{\"")
                .replace("=","\":")
                .replace(", ",",\"");
    }

    public static void yamlParser(File yamlFile) throws Exception {
        InputStream inpStream = new FileInputStream(yamlFile);
        Yaml yml = new Yaml();
        Map<String, Object> data = (Map<String, Object>) yml.load(inpStream);
//        System.out.println("  - " + data.toString());

        String playerUUID = yamlFile.getName().replace(".yml","");
        int enabled = data.get("enabled").toString() == "true" ? 1 : 0;
        long space =  Long.parseLong(data.get("max-space").toString());
        String partners = simplify(data.get("ALLOWS").toString());
        String unused = simplify(data.get("REMOVED").toString());
        String materials = simplify(data.get("MATERIALS").toString());

        if (sqlWriter(playerUUID, enabled, space, partners, unused, materials)){
            System.out.println("✓ Migrated " + data.get("player-name"));
        }
        else {
            System.out.println("Error Lol");
        }


    }
    public static void main(String[] args) {
        ConsoleUtils.titlePrinter();
        Scanner sc = new Scanner(System.in);
        if (sc.nextLine().toLowerCase().equals("n")){
            return;
        }

        ConsoleUtils.infoUserdataFolder();
        userdataFolder = sc.nextLine();
        ConsoleUtils.infoDatabaseFile();
        databaseUrl = sc.nextLine();
        System.out.print("Do you Wish to continue? [Y/N]: ");
        if (sc.nextLine().toLowerCase().equals("n")){
            return;
        }

        final File activeFolder = new File(userdataFolder);

        try{
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseUrl);
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "playerdata" ,null);
            if(!(rs.next())){
                System.out.println("Creating Table...");
                PreparedStatement ps = conn.prepareStatement("CREATE TABLE playerdata(`playerUUID` CHAR(36) NOT NULL,`texture` TINYTEXT DEFAULT '',`enabled` BOOLEAN DEFAULT 1,`space` INTEGER NOT NULL,`partners` TINYTEXT DEFAULT '{}',`unused` TINYTEXT DEFAULT '{}',`materials` TINYTEXT DEFAULT '{}',PRIMARY KEY(playerUUID))");
                ps.execute();
            }
        }
        catch (SQLException se){
            System.out.println("Database NOT found!");
        }

        for (File file : activeFolder.listFiles()){
            if(file.getName().endsWith(".yml")){
//                System.out.println(file.getName());
                try{
                    yamlParser(file);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("-------------- " + file.getName() + " Ignored --------------");
            }
        }
//        System.out.println("\n>>> Player Data Has been successfully migrated...");
    }
}
